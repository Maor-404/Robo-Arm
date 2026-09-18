package com.maor.roboticarm.blockentity;

import com.maor.roboticarm.RoboticArm;
import com.maor.roboticarm.block.RoboticArmBlock;
import com.maor.roboticarm.menu.RoboticArmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoboticArmBlockEntity extends BlockEntity implements MenuProvider {
    public enum Task { MINING, CRAFTING, TRANSFER }
    public enum Status { IDLE, WORKING, NO_ENERGY, NO_TARGET, OUTPUT_FULL, NO_TOOL }
    private final EnergyStorage energy = new EnergyStorage(10000, 1000, 1000);
    private final ItemStackHandler inventory = new ItemStackHandler(21) {
        @Override protected void onContentsChanged(int slot) { RoboticArmBlockEntity.this.setChanged(); }
    };
    private final IItemHandler storage = new RangedWrapper(inventory, 12, 21);
    private Task task = Task.MINING;
    private Status status = Status.IDLE;
    private boolean running;
    private int progress;
    private String targetName = "";

    public RoboticArmBlockEntity(BlockPos pos, BlockState state) { super(RoboticArm.ROBOTIC_ARM_BE.get(), pos, state); }
    public EnergyStorage getEnergyStorage() { return energy; }
    public IItemHandler getStorageHandler() { return storage; }
    public Task getTask() { return task; }
    public Status getStatus() { return status; }
    public boolean isRunning() { return running; }
    public String getTargetName() { return targetName; }
    public int getEnergy() { return energy.getEnergyStored(); }
    public int getMaxEnergy() { return energy.getMaxEnergyStored(); }

    public void setTask(Task task) { this.task = task; setChanged(); }
    public void setRunning(boolean running) {
        if (this.running != running) {
            this.running = running;
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }
    private void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, RoboticArmBlockEntity arm) {
        if (arm.energy.getEnergyStored() <= arm.energy.getMaxEnergyStored() - 500 && arm.inventory.getStackInSlot(1).is(net.minecraft.world.item.Items.REDSTONE)) {
            arm.inventory.extractItem(1, 1, false);
            arm.energy.receiveEnergy(500, false);
        }
        if (!arm.running) return;
        if (arm.energy.getEnergyStored() < cost(arm.task)) { arm.setStatus(Status.NO_ENERGY); return; }
        if (arm.task == Task.MINING) arm.tickMining((ServerLevel) level);
        else if (arm.task == Task.CRAFTING) arm.tickCrafting((ServerLevel) level);
        else arm.tickTransfer((ServerLevel) level);
    }
    private static int cost(Task task) { return task == Task.MINING ? 200 : task == Task.CRAFTING ? 100 : 20; }

    private void tickMining(ServerLevel level) {
        Direction facing = getBlockState().getValue(RoboticArmBlock.FACING);
        BlockPos target = worldPosition.relative(facing);
        for (int i = 0; i < 3 && level.getBlockState(target).isAir(); i++) target = target.relative(facing);
        BlockState state = level.getBlockState(target);
        ItemStack tool = inventory.getStackInSlot(0);
        if (state.isAir() || state.getFluidState().isSource()) { setStatus(Status.NO_TARGET); return; }
        if (tool.isEmpty() || !(tool.getItem() instanceof DiggerItem || tool.isDamageableItem())) { setStatus(Status.NO_TOOL); return; }
        if (state.hasBlockEntity() || state.getDestroySpeed(level, target) < 0 ||
                (state.requiresCorrectToolForDrops() && !tool.isCorrectToolForDrops(state))) { setStatus(Status.NO_TARGET); return; }
        setStatus(Status.WORKING);
        int ticks = Math.max(10, (int) (state.getDestroySpeed(level, target) * 30 /
                Math.max(1, tool.getItem().getDestroySpeed(tool, state))));
        if (++progress < ticks) return;
        List<ItemStack> drops = Block.getDrops(state, level, target, level.getBlockEntity(target), null, tool);
        for (ItemStack drop : drops) {
            ItemStack left = insertStorage(drop);
            if (!left.isEmpty()) { setStatus(Status.OUTPUT_FULL); progress = 0; return; }
        }
        level.destroyBlock(target, false);
        tool.hurtAndBreak(1, level, null, ignored -> {});
        energy.extractEnergy(cost(Task.MINING), false);
        targetName = state.getBlock().getName().getString();
        progress = 0;
    }

    private void tickCrafting(ServerLevel level) {
        progress++;
        if (progress < 40) return;
        progress = 0;
        ItemStack output = inventory.getStackInSlot(11);
        if (!output.isEmpty()) { setStatus(Status.OUTPUT_FULL); return; }
        List<ItemStack> grid = new java.util.ArrayList<>();
        for (int i = 2; i <= 10; i++) grid.add(inventory.getStackInSlot(i));
        CraftingInput input = CraftingInput.of(3, 3, grid);
        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (recipe.isEmpty()) { setStatus(Status.NO_TARGET); return; }
        ItemStack result = recipe.get().value().assemble(input, level.registryAccess());
        if (result.isEmpty()) { setStatus(Status.NO_TARGET); return; }
        inventory.setStackInSlot(11, result.copy());
        var remaining = level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, input, level);
        for (int i = 0; i < 9; i++) {
            inventory.extractItem(i + 2, 1, false);
            if (!remaining.get(i).isEmpty()) {
                if (inventory.getStackInSlot(i + 2).isEmpty()) inventory.setStackInSlot(i + 2, remaining.get(i));
                else insertStorage(remaining.get(i));
            }
        }
        for (int i = 2; i <= 10; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) continue;
            for (int s = 12; s < 21; s++) {
                ItemStack stored = inventory.getStackInSlot(s);
                if (!stored.isEmpty() && (grid.get(i - 2).isEmpty() || ItemStack.isSameItemSameComponents(stored, grid.get(i - 2)))) {
                    inventory.setStackInSlot(i, inventory.extractItem(s, 1, false)); break;
                }
            }
        }
        targetName = result.getHoverName().getString();
        setStatus(Status.WORKING);
        energy.extractEnergy(cost(Task.CRAFTING), false);
    }

    private void tickTransfer(ServerLevel level) {
        Direction facing = getBlockState().getValue(RoboticArmBlock.FACING);
        var from = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                worldPosition.relative(facing.getOpposite()), facing);
        var to = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                worldPosition.relative(facing), facing.getOpposite());
        if (from == null || to == null) { setStatus(Status.NO_TARGET); return; }
        for (int slot = 0; slot < from.getSlots(); slot++) {
            ItemStack sample = from.extractItem(slot, 8, true);
            if (sample.isEmpty()) continue;
            ItemStack remainder = net.neoforged.neoforge.items.ItemHandlerHelper.insertItem(to, sample, false);
            int moved = sample.getCount() - remainder.getCount();
            if (moved > 0) {
                from.extractItem(slot, moved, false);
                targetName = sample.getHoverName().getString();
                setStatus(Status.WORKING);
                energy.extractEnergy(cost(Task.TRANSFER), false);
            }
            return;
        }
        setStatus(Status.NO_TARGET);
    }

    private ItemStack insertStorage(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 12; i < 21 && !remaining.isEmpty(); i++) remaining = inventory.insertItem(i, remaining, false);
        return remaining;
    }

    public ItemStackHandler getInventory() { return inventory; }
    public ContainerData data() {
        return new ContainerData() {
            public int get(int index) { return switch (index) { case 0 -> getEnergy(); case 1 -> getMaxEnergy(); case 2 -> task.ordinal(); default -> status.ordinal(); }; }
            public void set(int index, int value) { }
            public int getCount() { return 4; }
        };
    }
    @Override public Component getDisplayName() { return Component.translatable("block.roboticarm.robotic_arm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) { return new RoboticArmMenu(id, inv, this, data()); }
    @Override protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putBoolean("Running", running);
        tag.putInt("Task", task.ordinal());
        tag.putInt("Status", status.ordinal());
        tag.putInt("Progress", progress);
        tag.putString("Target", targetName);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }
    @Override protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy.receiveEnergy(tag.getInt("Energy"), false);
        running = tag.getBoolean("Running");
        task = Task.values()[Math.min(Task.values().length - 1, tag.getInt("Task"))];
        status = Status.values()[Math.min(Status.values().length - 1, tag.getInt("Status"))];
        progress = tag.getInt("Progress");
        targetName = tag.getString("Target");
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }
    @Override public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putBoolean("Running", running);
        tag.putInt("Status", status.ordinal());
        tag.putInt("Task", task.ordinal());
        tag.putString("Target", targetName);
        return tag;
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void handleUpdateTag(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        running = tag.getBoolean("Running");
        status = Status.values()[Math.min(Status.values().length - 1, tag.getInt("Status"))];
        task = Task.values()[Math.min(Task.values().length - 1, tag.getInt("Task"))];
        targetName = tag.getString("Target");
    }
}
