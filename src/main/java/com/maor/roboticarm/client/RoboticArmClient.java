package com.maor.roboticarm.client;

import com.maor.roboticarm.RoboticArm;
import com.maor.roboticarm.client.screen.RoboticArmScreen;
import com.maor.roboticarm.entity.MysteriousVillagerEntity;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = RoboticArm.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RoboticArmClient {
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RoboticArm.ROBOTIC_ARM_BE.get(), RoboticArmRenderer::new);
        event.registerEntityRenderer(RoboticArm.MYSTERIOUS_VILLAGER.get(), MysteriousVillagerRenderer::new);
    }
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RoboticArmModel.LAYER, RoboticArmModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void screens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(RoboticArm.ROBOTIC_ARM_MENU.get(), RoboticArmScreen::new);
    }
}
