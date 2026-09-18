package com.maor.roboticarm.client;

import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Direction;

public class RoboticArmRenderer implements BlockEntityRenderer<RoboticArmBlockEntity> {
    public RoboticArmRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(RoboticArmBlockEntity be, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (be.isRunning() && be.getStatus() == RoboticArmBlockEntity.Status.WORKING) {
            float swing = (float)Math.sin((be.getLevel().getGameTime() + partial) * .15) * .2f;
            pose.translate(0, swing, 0);
        }
    }
}
