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
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public class RoboticArmMenu extends AbstractContainerMenu {
    private final RoboticArmBlockEntity arm;
    private final ContainerData data;
    public RoboticArmMenu(int id, Inventory player, RoboticArmBlockEntity arm, ContainerData data) {
        super(RoboticArm.ROBOTIC_ARM_MENU.get(), id);
        this.arm = arm;
        this.data = data;
        addDataSlots(data);
        for (int i = 0; i < 21; i++) addSlot(new HandlerSlot(arm.getInventory(), i, slotX(i), slotY(i)));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(player, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(player, col, 8 + col * 18, 198));
    }
    public static RoboticArmMenu fromBuffer(int id, Inventory inventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        return new RoboticArmMenu(id, inventory, (RoboticArmBlockEntity) be, ((RoboticArmBlockEntity) be).data());
    }
    private static int slotX(int slot) {
        if (slot == 0) return 26;
        if (slot == 1) return 62;
        if (slot >= 2 && slot <= 10) return 44 + ((slot - 2) % 3) * 18;
        if (slot == 11) return 116;
        return 26 + ((slot - 12) % 3) * 18;
    }
    private static int slotY(int slot) {
        if (slot == 0 || slot == 1 || slot == 11) return 26;
        if (slot >= 2 && slot <= 10) return 44 + ((slot - 2) / 3) * 18;
        return 80 + ((slot - 12) / 3) * 18;
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
    private static class HandlerSlot extends Slot {
        private final ItemStackHandler handler;
        private final int handlerSlot;
        HandlerSlot(ItemStackHandler handler, int slot, int x, int y) {
            super(new EmptyContainer(), slot, x, y); this.handler = handler; this.handlerSlot = slot;
        }
        @Override public ItemStack getItem() { return handler.getStackInSlot(handlerSlot); }
        @Override public void set(ItemStack stack) { handler.setStackInSlot(handlerSlot, stack); setChanged(); }
        @Override public void setChanged() { handler.setStackInSlot(handlerSlot, getItem()); }
        @Override public boolean hasItem() { return !getItem().isEmpty(); }
        @Override public ItemStack remove(int amount) { return handler.extractItem(handlerSlot, amount, false); }
        @Override public boolean mayPlace(ItemStack stack) { return handlerSlot != 11; }
        @Override public int getMaxStackSize() { return handlerSlot == 0 || handlerSlot == 11 ? 1 : 64; }
    }
    private static class EmptyContainer implements Container {
        public int getContainerSize(){return 0;} public boolean isEmpty(){return true;} public ItemStack getItem(int i){return ItemStack.EMPTY;}
        public ItemStack removeItem(int i,int c){return ItemStack.EMPTY;} public ItemStack removeItemNoUpdate(int i){return ItemStack.EMPTY;}
        public void setItem(int i,ItemStack s){} public void setChanged(){} public boolean stillValid(Player p){return true;}
        public void clearContent(){}
    }
}
