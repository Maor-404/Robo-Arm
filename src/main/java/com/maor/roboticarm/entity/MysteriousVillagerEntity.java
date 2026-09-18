package com.maor.roboticarm.entity;

import com.maor.roboticarm.RoboticArm;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class MysteriousVillagerEntity extends PathfinderMob {
    public MysteriousVillagerEntity(EntityType<? extends PathfinderMob> type, Level level) { super(type, level); setPersistenceRequired(); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(3, new RandomStrollGoal(this, .6));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.npc.Villager.createAttributes().add(Attributes.MOVEMENT_SPEED, .5);
    }
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(RoboticArm.MYSTERIOUS_VILLAGER.get(), createAttributes().build());
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) return InteractionResult.SUCCESS;
        int stage = player.getData(RoboticArm.MISSION_STAGE);
        if (stage == 0 && take(player, new ItemStack(RoboticArm.POWER_CORE.get()), 1)) {
            player.setData(RoboticArm.MISSION_STAGE, 1);
            player.sendSystemMessage(Component.literal("Mission 1 complete. Bring 8 iron, 8 copper, 8 redstone, 2 pistons and 4 quartz."));
        } else if (stage == 1 && hasMaterials(player)) {
            consume(player, Items.IRON_INGOT, 8); consume(player, Items.COPPER_INGOT, 8); consume(player, Items.REDSTONE, 8);
            consume(player, Items.PISTON, 2); consume(player, Items.QUARTZ, 4);
            player.setData(RoboticArm.MISSION_STAGE, 2);
            player.sendSystemMessage(Component.literal("Mission 2 complete. Find a processing core."));
        } else if (stage == 2 && take(player, new ItemStack(RoboticArm.PROCESSING_CORE.get()), 1)) {
            player.setData(RoboticArm.MISSION_STAGE, 3);
            player.getInventory().placeItemBackInInventory(new ItemStack(RoboticArm.ROBOTIC_ARM_BLUEPRINT.get()));
            player.sendSystemMessage(Component.literal("Mission complete! You received the Robotic Arm blueprint."));
        } else {
            player.sendSystemMessage(Component.literal(stage == 0 ? "Bring me a power core." : stage == 1 ? "The assembly materials, please." : "Bring me a processing core."));
        }
        return InteractionResult.CONSUME;
    }
    private static boolean take(Player player, ItemStack wanted, int count) {
        for (int i=0;i<player.getInventory().getContainerSize();i++) {
            ItemStack stack=player.getInventory().getItem(i);
            if (ItemStack.isSameItem(stack,wanted) && stack.getCount()>=count) { stack.shrink(count); return true; }
        }
        return false;
    }
    private static boolean has(Player p) { return p.getInventory().countItem(Items.IRON_INGOT)>=8 && p.getInventory().countItem(Items.COPPER_INGOT)>=8 && p.getInventory().countItem(Items.REDSTONE)>=8 && p.getInventory().countItem(Items.PISTON)>=2 && p.getInventory().countItem(Items.QUARTZ)>=4; }
    private static boolean hasMaterials(Player p) { return has(p); }
    private static void consume(Player p, net.minecraft.world.item.Item item, int n) { int remaining=n; for(int i=0;i<p.getInventory().getContainerSize()&&remaining>0;i++){ItemStack s=p.getInventory().getItem(i);if(s.is(item)){int take=Math.min(remaining,s.getCount());s.shrink(take);remaining-=take;}} }
}
