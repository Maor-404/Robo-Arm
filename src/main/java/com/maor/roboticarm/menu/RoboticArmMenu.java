package com.maor.roboticarm.menu;

import com.maor.roboticarm.RoboticArm;
import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RoboticArmMenu extends AbstractContainerMenu {
    private final RoboticArmBlockEntity arm;
    private final ContainerData data;
    public RoboticArmMenu(int id, Inventory player, RoboticArmBlockEntity arm, ContainerData data) {
        super(RoboticArm.ROBOTIC_ARM_MENU.get(), id);
        this.arm = arm;
        this.data = data;
        addDataSlots(data);
        addSlot(new LimitedSlot(arm.getInventory(), 0, 8, 66, 1, true));
        addSlot(new LimitedSlot(arm.getInventory(), 1, 8, 88, 64, true));
        for (int i = 2; i <= 10; i++) addSlot(new LimitedSlot(arm.getInventory(), i,
                44 + ((i - 2) % 3) * 18, 66 + ((i - 2) / 3) * 18, 64, true));
        addSlot(new LimitedSlot(arm.getInventory(), 11, 134, 84, 64, false));
        for (int i = 12; i < 21; i++) addSlot(new LimitedSlot(arm.getInventory(), i, 8 + (i - 12) * 18, 122, 64, true));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(player, col + row * 9 + 9, 8 + col * 18, 160 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 8 + col * 18, 214));
    }
    public static RoboticArmMenu fromBuffer(int id, Inventory inventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        return new RoboticArmMenu(id, inventory, (RoboticArmBlockEntity) be, ((RoboticArmBlockEntity) be).data());
    }
    public int energy() { return data.get(0); }
    public int maxEnergy() { return data.get(1); }
    public RoboticArmBlockEntity.Task task() { return RoboticArmBlockEntity.Task.values()[data.get(2)]; }
    public RoboticArmBlockEntity.Status status() { return RoboticArmBlockEntity.Status.values()[data.get(3)]; }
    public String targetName() { return arm.getTargetName(); }
    public BlockPos pos() { return arm.getBlockPos(); }
    @Override public boolean stillValid(Player player) { return player.distanceToSqr(arm.getBlockPos().getX()+.5, arm.getBlockPos().getY()+.5, arm.getBlockPos().getZ()+.5) < 64; }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (!source.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = source.getItem();
        ItemStack copy = stack.copy();
        if (index < 21) {
            if (!moveItemStackTo(stack, 21, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 21, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) source.set(ItemStack.EMPTY); else source.setChanged();
        return copy;
    }
    private static class LimitedSlot extends SlotItemHandler {
        private final int maxStack;
        private final boolean mayPlace;
        LimitedSlot(net.neoforged.neoforge.items.IItemHandler handler, int index, int x, int y, int maxStack, boolean mayPlace) {
            super(handler, index, x, y);
            this.maxStack = maxStack;
            this.mayPlace = mayPlace;
        }
        @Override public boolean mayPlace(ItemStack stack) { return mayPlace && super.mayPlace(stack); }
        @Override public int getMaxStackSize() { return maxStack; }
        @Override public int getMaxStackSize(ItemStack stack) { return maxStack; }
    }
}
