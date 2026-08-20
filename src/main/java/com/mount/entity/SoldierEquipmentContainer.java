package com.mount.entity;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 士兵装备容器包装器。
 * 将 LivingEntity 的装备槽转换为 Container 接口，用于 GUI 显示。
 */
public class SoldierEquipmentContainer implements Container {

    private final LivingEntity entity;

    public SoldierEquipmentContainer(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public int getContainerSize() {
        return 5; // 头盔、胸甲、护腿、靴子、主手
    }

    @Override
    public boolean isEmpty() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!entity.getItemBySlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> entity.getItemBySlot(EquipmentSlot.HEAD);
            case 1 -> entity.getItemBySlot(EquipmentSlot.CHEST);
            case 2 -> entity.getItemBySlot(EquipmentSlot.LEGS);
            case 3 -> entity.getItemBySlot(EquipmentSlot.FEET);
            case 4 -> entity.getItemBySlot(EquipmentSlot.MAINHAND);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack removed = stack.split(amount);
            setItem(slot, stack);
            return removed;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            setItem(slot, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        EquipmentSlot equipSlot = switch (slot) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            case 4 -> EquipmentSlot.MAINHAND;
            default -> null;
        };
        if (equipSlot != null) {
            entity.setItemSlot(equipSlot, stack);
        }
    }

    @Override
    public void setChanged() {
        // 实体装备变化时通知
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            entity.setItemSlot(slot, ItemStack.EMPTY);
        }
    }
}
