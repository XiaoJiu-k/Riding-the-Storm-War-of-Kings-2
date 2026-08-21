package com.mount.client.gui;

import com.mount.RidingTheStormWarOfKings2;
import com.mount.entity.BaseSoldierEntity;
import com.mount.entity.SoldierContainerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * 士兵背包 GUI 屏幕。
 * 左侧显示玩家列表用于选择阵营，右侧显示装备槽和玩家背包。
 */
@Environment(EnvType.CLIENT)
public class SoldierInventoryScreen extends AbstractContainerScreen<SoldierContainerMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RidingTheStormWarOfKings2.MOD_ID, "textures/gui/soldier_inventory.png"
    );

    private BaseSoldierEntity soldier;
    private String selectedPlayerName = "";
    private List<Button> playerButtons = new ArrayList<>();

    public SoldierInventoryScreen(SoldierContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
        this.soldier = menu.getSoldier();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // 整体背景
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);

        // 标题栏
        guiGraphics.fill(x, y, x + this.imageWidth, y + 20, 0xFFA0A0A0);

        // 装备槽背景（5 个，垂直排列在右侧）
        for (int i = 0; i < 5; i++) {
            int slotX = x + 78;
            int slotY = y + 28 + i * 18;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF373737);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 18, 0xFF373737);
            guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
            guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
        }

        // 玩家背包背景（3 行 x 9 列）
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

        // 快捷栏背景（1 行 x 9 列）
        for (int col = 0; col < 9; col++) {
            int slotX = x + 8 + col * 18;
            int slotY = y + 200;
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 1, 0xFF373737);
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + 18, 0xFF373737);
            guiGraphics.fill(slotX + 17, slotY, slotX + 18, slotY + 18, 0xFFFFFFFF);
            guiGraphics.fill(slotX, slotY + 17, slotX + 18, slotY + 18, 0xFFFFFFFF);
        }

        // 绘制经验条（在装备槽下方）
        if (soldier != null) {
            renderXpBar(guiGraphics, x, y, soldier);
        }
    }


    /**
     * 绘制经验条
     */
    private void renderXpBar(GuiGraphics guiGraphics, int x, int y, BaseSoldierEntity soldier) {
        int barWidth = 160;
        int barHeight = 8;
        int barX = x + 8;
        int barY = y + 136; // 在装备槽下方

        // 经验条背景
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF373737);

        // 经验条填充（绿色）
        float progress = soldier.getXpProgress();
        int fillWidth = (int) (barWidth * progress);
        if (fillWidth > 0) {
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFF00FF00);
        }

        // 经验条边框
        guiGraphics.fill(barX, barY, barX + barWidth, barY + 1, 0xFF8B8B8B);
        guiGraphics.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF8B8B8B);
        guiGraphics.fill(barX, barY, barX + 1, barY + barHeight, 0xFF8B8B8B);
        guiGraphics.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF8B8B8B);

        // 经验文字：XP: XX/XX
        int xpTotal = soldier.getXpTotal();
        int xpNeeded = soldier.getXpNeededForNextLevel();
        String xpText = "XP: " + xpTotal + "/" + xpNeeded;
        guiGraphics.drawString(this.font, xpText, barX + barWidth / 2 - this.font.width(xpText) / 2, barY + 10, 0xFFFFFFFF, false);

        // 等级文字：Lv.X
        String levelText = "Lv." + soldier.getLevel();
        guiGraphics.drawString(this.font, levelText, barX + barWidth + 4, barY + 2, 0xFFFFFFFF, false);
    }
}
