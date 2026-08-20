package com.mount.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * 士兵背包容器。
 * 包含装备槽（头盔、胸甲、护腿、靴子、主手）和物品槽。
 *
 * 槽位布局：
 * - 0: 头盔
 * - 1: 胸甲
 * - 2: 护腿
 * - 3: 靴子
 * - 4: 主手（武器）
 * - 5-31: 物品槽（27格）
 */
public class SoldierContainer extends SimpleContainer {

    /** 装备槽数量 */
    public static final int EQUIPMENT_SLOTS = 5;

    /** 物品槽数量 */
    public static final int INVENTORY_SLOTS = 27;

    /** 总槽数 */
    public static final int TOTAL_SLOTS = EQUIPMENT_SLOTS + INVENTORY_SLOTS;

    public SoldierContainer() {
        super(TOTAL_SLOTS);
    }

    /**
     * 获取装备槽的 ItemStack。
     *
     * @param slot 槽位索引（0-4）
     * @return 装备物品
     */
    public ItemStack getEquipment(int slot) {
        if (slot < 0 || slot >= EQUIPMENT_SLOTS) {
            return ItemStack.EMPTY;
        }
        return getItem(slot);
    }

    /**
     * 设置装备槽的 ItemStack。
     *
     * @param slot 槽位索引（0-4）
     * @param stack 装备物品
     */
    public void setEquipment(int slot, ItemStack stack) {
        if (slot >= 0 && slot < EQUIPMENT_SLOTS) {
            setItem(slot, stack);
        }
    }

    /**
     * 获取物品槽的 ItemStack。
     *
     * @param slot 物品槽索引（0-26）
     * @return 物品
     */
    public ItemStack getInventoryItem(int slot) {
        if (slot < 0 || slot >= INVENTORY_SLOTS) {
            return ItemStack.EMPTY;
        }
        return getItem(EQUIPMENT_SLOTS + slot);
    }

    /**
     * 设置物品槽的 ItemStack。
     *
     * @param slot 物品槽索引（0-26）
     * @param stack 物品
     */
    public void setInventoryItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SLOTS) {
            setItem(EQUIPMENT_SLOTS + slot, stack);
        }
    }

    /**
     * 判断槽位是否为装备槽。
     *
     * @param slot 槽位索引
     * @return true 如果是装备槽
     */
    public boolean isEquipmentSlot(int slot) {
        return slot >= 0 && slot < EQUIPMENT_SLOTS;
    }

    /**
     * 判断槽位是否为物品槽。
     *
     * @param slot 槽位索引
     * @return true 如果是物品槽
     */
    public boolean isInventorySlot(int slot) {
        return slot >= EQUIPMENT_SLOTS && slot < TOTAL_SLOTS;
    }
}
