package com.mount.reg;

import com.mount.RidingTheStormWarOfKings2;
import com.mount.entity.InfantryEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

/**
 * 物品注册中心。
 * 当前仅注册步兵刷怪蛋，后续兵种刷怪蛋均在此添加。
 */
public class ModItems {

    private ModItems() {}

    /**
     * 步兵刷怪蛋。
     * 延迟到 initialize() 中注册，确保 ModEntities 已完全初始化。
     */
    public static Supplier<SpawnEggItem> INFANTRY_SPAWN_EGG;

    /**
     * 创造模式物品栏（主分类）。
     * 1.21+ 中 CreativeModeTab.builder() 需要 (Row, column) 参数。
     */
    public static Supplier<CreativeModeTab> CREATIVE_TAB;

    // ─────────────────────────────────────────────
    // 注册工具方法
    // ─────────────────────────────────────────────

    private static <T extends Item> Supplier<T> register(
            String name, T item) {
        // 1.21+ 使用静态 Registry.register() 方法
        ResourceLocation id = RidingTheStormWarOfKings2.id(name);
        T registered = Registry.register(BuiltInRegistries.ITEM, id, item);
        return () -> registered;
    }

    private static Supplier<CreativeModeTab> registerTab(
            String name, CreativeModeTab tab) {
        // CreativeModeTab 通过 Registries.CREATIVE_MODE_TAB 注册
        ResourceLocation id = RidingTheStormWarOfKings2.id(name);
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        return () -> tab;
    }

    /**
     * 注册所有物品（在 ModInitializer.onInitialize() 中调用）。
     * 必须在 ModEntities.initialize() 之后调用，确保实体类型已注册。
     */
    public static void initialize() {
        // 注册步兵刷怪蛋（延迟注册，确保 ModEntities 已初始化）
        // 1.21.2+ 需要在 Properties 中使用 ResourceKey 设置物品 ID，否则 SpawnEggItem 构造时会报 NPE
        ResourceKey<Item> infantryEggKey = ResourceKey.create(
                Registries.ITEM,
                RidingTheStormWarOfKings2.id("infantry_spawn_egg")
        );
        INFANTRY_SPAWN_EGG = register(
                "infantry_spawn_egg",
                new SpawnEggItem(
                        new Item.Properties()
                                .setId(infantryEggKey)
                                .spawnEgg(ModEntities.getInfantry())
                )
        );

        // 注册创造模式物品栏
        CREATIVE_TAB = registerTab(
                "war_of_kings_2",
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(net.minecraft.network.chat.Component.translatable(
                                "itemGroup." + RidingTheStormWarOfKings2.MOD_ID + ".main"))
                        .icon(() -> INFANTRY_SPAWN_EGG.get().getDefaultInstance())
                        .displayItems((params, output) -> {
                            output.accept(INFANTRY_SPAWN_EGG.get());
                        })
                        .build()
        );

        // 将刷怪蛋添加到原版的"刷怪蛋"标签中
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(itemGroup -> {
            itemGroup.accept(INFANTRY_SPAWN_EGG.get());
        });
    }

    /**
     * 获取步兵刷怪蛋。
     */
    public static Item getInfantrySpawnEgg() {
        return INFANTRY_SPAWN_EGG.get();
    }
}
