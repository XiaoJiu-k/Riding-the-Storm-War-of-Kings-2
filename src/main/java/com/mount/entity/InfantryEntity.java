package com.mount.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

        // 漂浮（防止溺水）
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // 近战攻击（速度 1.0）
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0, true));

        // 随机走动（速度 0.8）
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9));

        // 被攻击时反击（排除玩家，与玩家同阵营）
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Player.class));

        // 主动攻击敌对生物（Monster），搜索范围 16 格
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
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
