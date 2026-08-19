package com.mount.client;

import com.mount.client.entity.ModEntityRenderers;
import net.fabricmc.api.ClientModInitializer;

public class RidingTheStormWarOfKings2Client implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// 注册实体渲染器
		ModEntityRenderers.initialize();
	}
}
