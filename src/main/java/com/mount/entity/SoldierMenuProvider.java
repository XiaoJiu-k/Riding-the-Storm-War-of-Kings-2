package com.mount.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * 士兵背包菜单提供者。
 * 用于打开士兵的装备/背包 GUI。
 */
public class SoldierMenuProvider implements MenuProvider {

    private final BaseSoldierEntity soldier;

    public SoldierMenuProvider(BaseSoldierEntity soldier) {
        this.soldier = soldier;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(soldier.getDisplayNameKey());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SoldierContainerMenu(containerId, playerInventory, soldier);
    }
}
