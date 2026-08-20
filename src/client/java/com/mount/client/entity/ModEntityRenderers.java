package com.mount.client.entity;

import com.mount.entity.InfantryEntity;
import com.mount.reg.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * 客户端实体渲染注册中心。
 * 在 RidingTheStormWarOfKings2Client.onInitializeClient() 中调用。
 */
public class ModEntityRenderers {

    private ModEntityRenderers() {}

    /**
     * 步兵模型层位置（使用自定义命名空间，避免与原版玩家模型层冲突）。
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
        // 注册步兵模型层（64x64 是玩家皮肤的标准尺寸）
        EntityModelLayerRegistry.registerModelLayer(
                INFANTRY_LAYER,
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F),
                        64, 64
                )
        );

        // 注册步兵渲染器
        EntityRendererRegistry.register(
                ModEntities.INFANTRY.get(),
                InfantryRenderer::new
        );
    }

    /**
     * 步兵渲染器。
     * 使用 HumanoidMobRenderer，外观与原版史蒂夫一致。
     * 纹理使用自定义步兵纹理。
     * 盔甲使用原版 HumanoidArmorLayer + ArmorModelSet.bake() 方法渲染。
     */
    public static class InfantryRenderer extends HumanoidMobRenderer<InfantryEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

        public InfantryRenderer(EntityRendererProvider.Context context) {
            super(
                    context,
                    new HumanoidModel<>(context.bakeLayer(INFANTRY_LAYER)),
                    0.5F
            );
            // 使用原版 ArmorModelSet.bake() 烘焙盔甲模型，然后创建 HumanoidArmorLayer
            ArmorModelSet<HumanoidModel<HumanoidRenderState>> armorModelSet = ArmorModelSet.bake(
                    ModelLayers.PLAYER_ARMOR,
                    context.getModelSet(),
                    HumanoidModel::new
            );
            this.addLayer(new HumanoidArmorLayer<>(
                    this,
                    armorModelSet,
                    context.getEquipmentRenderer()
            ));
        }

        @Override
        public ResourceLocation getTextureLocation(HumanoidRenderState renderState) {
            return INFANTRY_TEXTURE;
        }

        @Override
        public HumanoidRenderState createRenderState() {
            return new HumanoidRenderState();
        }

        /**
         * 强制显示名称牌（等级文字），类似命令方块的效果。
         */
        @Override
        protected boolean shouldShowName(InfantryEntity entity, double squaredDistanceToCamera) {
            return true;
        }
    }
}
