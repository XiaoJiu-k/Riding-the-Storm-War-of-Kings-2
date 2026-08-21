package com.mount.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * 步兵实体（Infantry）
 *
 * 属性（参考需求清单 1.1）：
 *   - 生命值：20❤
 *   - 近战攻击：剑
 *   - 移动速度：普通
 *   - 可穿戴铁甲
 *
 * 外观：使用原版史蒂夫模型（HumanoidModel）。
 *
 * AI 行为：
 *   - 与玩家同阵营（不攻击玩家）
 *   - 主动攻击敌对生物（Monster）
 *   - 随机走动
 */
public class InfantryEntity extends BaseSoldierEntity {

    public InfantryEntity(EntityType<? extends BaseSoldierEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 步兵属性注册（静态方法，供 FabricDefaultAttributeRegistry 使用）。
     */
    public static AttributeSupplier.Builder createInfantryAttributes() {
        return BaseSoldierEntity.createBaseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    /**
     * 注册 AI 目标与行为。
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();

        // ---- 行为（Movement / Actions） ----
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0D, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));

        // ---- 目标选择（Targeting） ----
        // 1. 反击目标：受到伤害时反击攻击者（排除同阵营）
        this.targetSelector.addGoal(1, new Goal() {
            @Override
            public boolean canUse() {
                LivingEntity attacker = getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && !attacker.isAlliedTo(InfantryEntity.this)) {
                    // 排除玩家（与玩家同阵营）
                    if (attacker instanceof Player) return false;
                    // 排除同阵营步兵
                    if (!InfantryEntity.this.canAttack(attacker)) return false;
                    setTarget(attacker);
                    return true;
                }
                return false;
            }

            @Override
            public boolean canContinueToUse() {
                LivingEntity target = getTarget();
                return target != null && target.isAlive() && distanceTo(target) <= 64.0
                        && !isSameFactionTarget(target);
            }

            @Override
            public void stop() {
                setTarget(null);
            }
        });

        // 2. 主动索敌：搜索范围内的敌对生物（Monster），排除同阵营
        this.targetSelector.addGoal(2, new Goal() {
            private int cooldown = 0;

            @Override
            public boolean canUse() {
                // 已有目标则不再重新搜索
                if (getTarget() != null) return false;

                // 冷却控制，避免每 tick 都扫描（提高性能）
                if (--cooldown > 0) return false;
                cooldown = 20;  // 每 1 秒尝试一次（20 ticks）

                // 在范围内搜索 Monster
                double searchRange = 16.0;
                List<Monster> monsters = level().getEntitiesOfClass(
                        Monster.class,
                        getBoundingBox().inflate(searchRange),
                        e -> e != null && e.isAlive() && !isSameFactionTarget(e)  // 排除同阵营
                );

                if (!monsters.isEmpty()) {
                    // 按距离排序，取最近的
                    monsters.sort((a, b) -> Double.compare(distanceToSqr(a), distanceToSqr(b)));
                    setTarget(monsters.get(0));
                    return true;
                }
                return false;
            }

            @Override
            public boolean canContinueToUse() {
                LivingEntity target = getTarget();
                return target != null && target.isAlive() && distanceTo(target) <= 64.0;
            }

            @Override
            public void stop() {
                setTarget(null);
            }
        });
    }

    // 辅助方法：检查是否同阵营（拥有相同阵营ID）
    // 在 InfantryEntity 中，将 isSameFactionTarget 方法替换为：
    private boolean isSameFactionTarget(LivingEntity entity) {
        if (entity == this) return true;

        // 1. 如果是同一个主人（BaseSoldierEntity 之间的检查）
        if (entity instanceof BaseSoldierEntity other) {
            UUID myOwner = this.getOwnerUUID();
            UUID otherOwner = other.getOwnerUUID();
            if (myOwner != null && myOwner.equals(otherOwner)) {
                return true;
            }
            // 检查阵营 ID
            if (this.isSameFaction(other)) {
                return true;
            }
        }

        // 2. 如果是玩家（检查是否为主人本人）
        if (entity instanceof Player player) {
            UUID myOwner = this.getOwnerUUID();
            if (myOwner != null && myOwner.equals(player.getUUID())) {
                return true;
            }
            String factionId = this.getFactionId();
            if (factionId != null && !factionId.isEmpty()) {
                return factionId.equals(player.getName().getString())
                        || factionId.equals(player.getUUID().toString());
            }
        }

        return false;
    }

    /**
     * 每次攻击敌人时调用。
     * 如果击杀了敌对生物则给予经验值。
     */
    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(level, target);
        if (result && target instanceof LivingEntity enemy && !enemy.isAlive()) {
            // 敌人被击杀，检查是否为敌对生物
            if (enemy instanceof Monster monster && !isSameFactionTarget(monster)) {
                int xpGain = 5 + this.random.nextInt(10); // 随机 5-14 XP
                this.addXp(xpGain);
            }
        }
        return result;
    }

    /**
     * 步兵本地化名称键。
     */
    @Override
    public String getDisplayNameKey() {
        return "entity.riding-the-storm-war-of-kings-2.infantry";
    }

    @Override
    public String getEntityName() {
        return "Infantry";
    }
}
