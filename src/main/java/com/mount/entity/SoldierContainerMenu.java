package com.mount.entity;

import com.mount.reg.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 士兵背包容器菜单。
 * 使用 SoldierEquipmentContainer 包装实体装备槽，装备会实际穿在士兵身上。
 */
public class SoldierContainerMenu extends AbstractContainerMenu {

    private final BaseSoldierEntity soldier;
    private final Container equipmentContainer;

    /**
     * 服务端构造函数。
     *
     * @param containerId 容器 ID
     * @param playerInventory 玩家背包
     * @param soldier 士兵实体
     */
    public SoldierContainerMenu(int containerId, Inventory playerInventory, BaseSoldierEntity soldier) {
        super(ModMenuTypes.SOLDIER_INVENTORY, containerId);
        this.soldier = soldier;

        if (soldier != null) {
            this.equipmentContainer = new SoldierEquipmentContainer(soldier);
        } else {
            this.equipmentContainer = new SimpleContainer(5);
        }

        // 添加装备槽（0-4）
        for (int i = 0; i < 5; i++) {
            addSlot(new Slot(equipmentContainer, i, 80, 28 + i * 18));
        }

        // 添加玩家背包槽（5-31）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = 5 + row * 9 + col;
                addSlot(new Slot(playerInventory, slotIndex - 5, 8 + col * 18, 142 + row * 18));
            }
        }

        // 添加玩家快捷栏槽（32-40）
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 200));
        }
    }

    /**
     * 客户端构造函数（用于 Screen 工厂）。
     *
     * @param containerId 容器 ID
     * @param playerInventory 玩家背包
     */
    public SoldierContainerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < 5) {
                // 从装备槽移动到玩家背包
                if (!moveItemStackTo(stackInSlot, 5, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移动到装备槽（简化：尝试放入第一个空装备槽）
                if (!moveItemStackTo(stackInSlot, 0, 5, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return soldier == null || (soldier.isAlive() && player.distanceTo(soldier) < 8.0F);
    }

    @Nullable
    public BaseSoldierEntity getSoldier() {
        return soldier;
    }
}
