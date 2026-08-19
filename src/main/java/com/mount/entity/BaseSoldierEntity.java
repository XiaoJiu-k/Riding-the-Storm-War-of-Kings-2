package com.mount.entity;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 兵种实体基类 — 所有士兵（步兵、弓箭手、骑兵等）均继承此类。
 * 外观使用原版史蒂夫模型（HumanoidModel），后续可通过渲染器切换皮肤。
 */
public abstract class BaseSoldierEntity extends Monster {

    /** 玩家绑定的 UUID（用于区分阵营归属） */
    @Nullable
    private UUID ownerUUID;

    /** 攻击动画状态 */
    public final AnimationState attackAnimationState = new AnimationState();

    /** idle 动画状态 */
    public final AnimationState idleAnimationState = new AnimationState();

    /** idle 动画 tick 计数器 */
    private int idleAnimationTick = 0;

    protected BaseSoldierEntity(EntityType<? extends BaseSoldierEntity> entityType, Level level) {
        super(entityType, level);
    }

    // ─────────────────────────────────────────────
    // 抽象方法（子类实现）
    // ─────────────────────────────────────────────

    /**
     * 子类应在此覆写以提供自定义属性（静态方法，供 FabricDefaultAttributeRegistry 使用）。
     */
    protected static AttributeSupplier.Builder createBaseSoldierAttributes() {
        return createBaseAttributes();
    }

    /**
     * 返回该兵种的本地化显示名称键（如 "entity.modid.infantry"）。
     */
    public abstract String getDisplayNameKey();

    /**
     * 返回该兵种英文名称（用于调试）。
     */
    public abstract String getEntityName();

    // ─────────────────────────────────────────────
    // 基础属性注册
    // ─────────────────────────────────────────────

    /**
     * 兵种基础属性池，供子类继承后叠加自定义值。
     */
    public static AttributeSupplier.Builder createBaseAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    // ─────────────────────────────────────────────
    // 所有者 UUID 绑定
    // ─────────────────────────────────────────────

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
    }

    public boolean hasOwner() {
        return ownerUUID != null;
    }

    // ─────────────────────────────────────────────
    // 动画状态更新（tick）
    // ─────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        // 攻击动画：正在挥拳时启动
        if (getAttackAnim(1.0F) > 0.0F) {
            attackAnimationState.startIfStopped(tickCount);
        }

        // idle 动画：未移动时推进
        if (!this.walkAnimation.isMoving()) {
            if (idleAnimationTick <= 3 && this.walkAnimation.speed() == 0.0F) {
                idleAnimationState.startIfStopped(tickCount);
            } else if (idleAnimationTick > 4 && this.walkAnimation.speed() == 0.0F) {
                idleAnimationState.stop();
            }
        } else {
            idleAnimationState.stop();
        }
        idleAnimationTick++;
    }

    // ─────────────────────────────────────────────
    // 实体数据读写（1.21 使用 ValueOutput/ValueInput 替代 CompoundTag）
    // ─────────────────────────────────────────────

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (ownerUUID != null) {
            // 将 UUID 拆为两个 long 分别存储
            output.putLong("OwnerMost", ownerUUID.getMostSignificantBits());
            output.putLong("OwnerLeast", ownerUUID.getLeastSignificantBits());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        var mostBits = input.getLong("OwnerMost");
        var leastBits = input.getLong("OwnerLeast");
        if (mostBits.isPresent() && leastBits.isPresent()) {
            ownerUUID = new UUID(mostBits.get(), leastBits.get());
        }
    }

    // ─────────────────────────────────────────────
    // 实体初始化（1.21 使用 finalizeSpawn 替代 initialize）
    // ─────────────────────────────────────────────

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, spawnData);
        // 新生成的士兵默认不绑定所有者
        return data;
    }

    // ─────────────────────────────────────────────
    // Dummy 子类（用于 EntityType 工厂，避免无法实例化抽象类）
    // ─────────────────────────────────────────────

    public static class DummySoldier extends BaseSoldierEntity {
        public DummySoldier(EntityType<? extends BaseSoldierEntity> entityType, Level level) {
            super(entityType, level);
        }

        @Override
        public String getDisplayNameKey() {
            return "entity.riding-the-storm-war-of-kings-2.dummy_soldier";
        }

        @Override
        public String getEntityName() {
            return "Dummy Soldier";
        }
    }
}
