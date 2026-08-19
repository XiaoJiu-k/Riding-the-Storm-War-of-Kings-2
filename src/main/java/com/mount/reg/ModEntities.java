package com.mount.reg;

import com.mount.RidingTheStormWarOfKings2;
import com.mount.entity.InfantryEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

/**
 * 注册中心：集中注册所有实体。
 * 遵循 Fabric 1.21 标准注册流程，在 ModInitializer.onInitialize() 中调用。
 */
public class ModEntities {

    private ModEntities() {}

    /**
     * 步兵实体类型 Supplier（延迟初始化）。
     */
    public static final Supplier<EntityType<InfantryEntity>> INFANTRY = register(
            "infantry",
            EntityType.Builder.of(InfantryEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)          // 史蒂夫体型：宽0.6，高1.95
                    .clientTrackingRange(8)
    );

    /**
     * 通用实体注册方法。
     *
     * @param name    实体 ID（如 "infantry"）
     * @param builder EntityType.Builder
     * @return        注册后的 Supplier
     */
    private static <T extends net.minecraft.world.entity.Entity> Supplier<EntityType<T>> register(
            String name, EntityType.Builder<T> builder) {
        ResourceLocation id = RidingTheStormWarOfKings2.id(name);
        // 1.21+ build() 需要 ResourceKey<EntityType<?>>
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<T> entityType = builder.build(key);
        // 使用静态 Registry.register() 方法，通过 ResourceLocation 注册
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id, entityType);
        return () -> entityType;
    }

    /**
     * 获取步兵实体类型。
     */
    public static EntityType<?> getInfantry() {
        return INFANTRY.get();
    }

    /**
     * 初始化所有实体（在 onInitialize 中调用）。
     */
    public static void initialize() {
        // 注册步兵属性
        FabricDefaultAttributeRegistry.register(INFANTRY.get(), InfantryEntity.createInfantryAttributes());
    }
}
