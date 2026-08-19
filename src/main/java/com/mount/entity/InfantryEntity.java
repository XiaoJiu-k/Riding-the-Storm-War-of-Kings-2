package com.mount.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
