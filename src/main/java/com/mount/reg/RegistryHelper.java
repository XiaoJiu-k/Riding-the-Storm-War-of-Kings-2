package com.mount.reg;

import com.mount.RidingTheStormWarOfKings2;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * 通用注册工具类。
 * 使用延迟 Supplier 模式，避免循环依赖导致注册失败。
 *
 * @param <T> 注册对象类型
 * @deprecated 1.21+ 推荐直接使用 Registry.register() + BuiltInRegistries
 */
@Deprecated
public class RegistryHelper {

    private RegistryHelper() {}

    /**
     * 向实体注册表注册实体类型。
     *
     * @param registry  目标注册表 ResourceKey（如 Registries.ENTITY_TYPE）
     * @param name      实体名称（如 "infantry"）
     * @param builder   EntityType.Builder
     * @return          注册后的 Supplier
     * @deprecated 直接使用 ModEntities 中的注册方法
     */
    @Deprecated
    public static <T extends Entity> Supplier<net.minecraft.world.entity.EntityType<T>> register(
            ResourceKey<Registry<net.minecraft.world.entity.EntityType<?>>> registry,
            String name,
            net.minecraft.world.entity.EntityType.Builder<T> builder) {
        net.minecraft.resources.ResourceLocation id = RidingTheStormWarOfKings2.id(name);
        net.minecraft.resources.ResourceKey<net.minecraft.world.entity.EntityType<?>> key =
                ResourceKey.create(registry, id);
        net.minecraft.world.entity.EntityType<T> entityType = builder.build(key);
        Registry.register(
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE,
                id, entityType);
        return () -> entityType;
    }

    /**
     * 向指定注册表注册对象，并返回 Supplier 包装。
     *
     * @param registry 目标注册表
     * @param name     对象名称（ID）
     * @param object   对象实例
     * @return         注册后的 Supplier
     * @deprecated 直接使用 BuiltInRegistries.register()
     */
    @Deprecated
    public static <T> Supplier<T> register(
            Registry<T> registry,
            String name,
            T object) {
        T registered = Registry.register(registry, RidingTheStormWarOfKings2.id(name), object);
        return () -> registered;
    }
}
