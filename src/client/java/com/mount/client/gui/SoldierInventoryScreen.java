package com.mount.client.gui;

import com.mount.RidingTheStormWarOfKings2;
import com.mount.entity.SoldierContainer;
import com.mount.entity.SoldierContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 士兵背包 GUI 屏幕。
 * 显示士兵的装备槽、物品槽和玩家背包。
 */
public class SoldierInventoryScreen extends AbstractContainerScreen<SoldierContainerMenu> {

    /** GUI 纹理路径 */
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RidingTheStormWarOfKings2.MOD_ID, "textures/gui/soldier_inventory.png"
    );

    public SoldierInventoryScreen(SoldierContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // GUI 尺寸：176x240（5 个装备槽 + 玩家背包）
        this.imageWidth = 176;
        this.imageHeight = 240;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // 绘制灰色背景
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);

        // 绘制标题栏
        guiGraphics.fill(x, y, x + this.imageWidth, y + 20, 0xFFA0A0A0);

        // 绘制装备槽背景（5 个，垂直排列）
        for (int i = 0; i < 5; i++) {
            int slotX = x + 72;
            int slotY = y + 28 + i * 18;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            // 绘制边框（左上暗，右下亮）
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF373737);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 18, 0xFF373737);
            guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
            guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
        }

        // 绘制玩家背包背景（3 行 x 9 列）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = x + 8 + col * 18;
                int slotY = y + 142 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF373737);
                guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 18, 0xFF373737);
                guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
                guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
            }
        }

        // 绘制快捷栏背景（1 行 x 9 列）
        for (int col = 0; col < 9; col++) {
            int slotX = x + 8 + col * 18;
            int slotY = y + 200;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF373737);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 18, 0xFF373737);
            guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
            guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
