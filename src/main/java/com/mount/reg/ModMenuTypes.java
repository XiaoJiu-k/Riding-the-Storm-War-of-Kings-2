package com.mount.reg;

import com.mount.RidingTheStormWarOfKings2;
import com.mount.entity.SoldierContainerMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;

/**
 * 菜单类型注册中心。
 * 注册士兵背包的 MenuType。
 */
public class ModMenuTypes {

    private ModMenuTypes() {}

    /** 士兵背包菜单类型（创建时即注册，避免静态初始化顺序问题） */
    public static final MenuType<SoldierContainerMenu> SOLDIER_INVENTORY = register(
            "soldier_inventory",
            SoldierContainerMenu::new
    );

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(
                BuiltInRegistries.MENU,
                ResourceLocation.fromNamespaceAndPath(RidingTheStormWarOfKings2.MOD_ID, name),
                new MenuType<>(constructor, FeatureFlags.DEFAULT_FLAGS)
        );
    }

    /**
     * 注册所有菜单类型。
     * 在 ModInitializer.onInitialize() 中调用（实际注册已在静态字段初始化时完成）。
     */
    public static void initialize() {
        // MenuType 已在静态字段初始化时注册
    }
}
