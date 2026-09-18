package com.maor.roboticarm.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BlueprintItem extends Item {
    public BlueprintItem(Properties properties) { super(properties); }
    @Override public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return stack.copyWithCount(1); }
}
