package com.mount;

import com.mount.reg.ModEntities;
import com.mount.reg.ModItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RidingTheStormWarOfKings2 implements ModInitializer {
	public static final String MOD_ID = "riding-the-storm-war-of-kings-2";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world! - War of Kings 2 initializing...");

		// 注册实体
		ModEntities.initialize();

		// 注册物品（含刷怪蛋）
		ModItems.initialize();

		// 注册创造模式物品栏
		// CreativeModeTab 已在 ModItems 中通过 Supplier 延迟注册

		LOGGER.info("Entities and items registered successfully.");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
