package com.mount.client;

import com.mount.client.entity.ModEntityRenderers;
import com.mount.client.gui.SoldierInventoryScreen;
import com.mount.reg.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class RidingTheStormWarOfKings2Client implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// 注册实体渲染器
		ModEntityRenderers.initialize();

		// 注册士兵背包 GUI 屏幕
		MenuScreens.register(ModMenuTypes.SOLDIER_INVENTORY, SoldierInventoryScreen::new);
	}
}
