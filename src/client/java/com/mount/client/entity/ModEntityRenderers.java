package com.mount.client.entity;

import com.mount.entity.InfantryEntity;
import com.mount.reg.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * 客户端实体渲染注册中心。
 * 在 RidingTheStormWarOfKings2Client.onInitializeClient() 中调用。
 */
public class ModEntityRenderers {

    private ModEntityRenderers() {}

    /**
     * 步兵模型层位置（用于 HumanoidModel 注册）。
     * 使用自定义名称 "infantry"，避免与 Minecraft 原生模型层冲突。
     */
    public static final ModelLayerLocation INFANTRY_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("riding-the-storm-war-of-kings-2", "infantry"),
            "main"
    );

    /**
     * 步兵纹理路径。
     * 使用自定义步兵纹理（军绿色制服），符合 HumanoidModel UV 布局。
     */
    private static final ResourceLocation INFANTRY_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "riding-the-storm-war-of-kings-2", "textures/entity/infantry.png"
    );

    /**
     * 注册所有实体渲染器和模型层。
     * 在 onInitializeClient 末尾调用。
     */
    public static void initialize() {
        // 注册步兵模型层（使用 HumanoidModel，外观为史蒂夫）
        EntityModelLayerRegistry.registerModelLayer(
                INFANTRY_LAYER,
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F),
                        64, 64
                )
        );

        // 注册步兵渲染器（HumanoidMobRenderer 支持盔甲层）
        // 使用 INFANTRY 直接获取具体类型，避免通配符类型推断问题
        EntityRendererRegistry.register(
                ModEntities.INFANTRY.get(),
                InfantryRenderer::new
        );
    }

    /**
     * 步兵渲染器。
     * 使用 HumanoidMobRenderer，外观与原版史蒂夫一致。
     * 纹理使用原版 Steve 玩家皮肤。
     * 1.21.10 的 HumanoidMobRenderer 需要 3 个类型参数：<Entity, RenderState, Model>
     */
    public static class InfantryRenderer extends HumanoidMobRenderer<InfantryEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

        public InfantryRenderer(EntityRendererProvider.Context context) {
            super(
                    context,
                    new HumanoidModel<>(context.bakeLayer(INFANTRY_LAYER)),
                    0.5F
            );
        }

        @Override
        public ResourceLocation getTextureLocation(HumanoidRenderState renderState) {
            return INFANTRY_TEXTURE;
        }

        @Override
        public HumanoidRenderState createRenderState() {
            return new HumanoidRenderState();
        }
    }
}
