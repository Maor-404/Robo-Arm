package com.maor.roboticarm.client;

import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;

public class RoboticArmRenderer implements BlockEntityRenderer<RoboticArmBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("roboticarm", "textures/entity/robotic_arm.png");
    private final RoboticArmModel model;
    public RoboticArmRenderer(BlockEntityRendererProvider.Context context) {
        model = new RoboticArmModel(context.bakeLayer(RoboticArmModel.LAYER));
    }
    @Override public void render(RoboticArmBlockEntity be, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose();
        pose.translate(.5, 1.5, .5);
        pose.scale(1, -1, -1);
        pose.mulPose(Axis.YP.rotationDegrees(-be.getBlockState().getValue(com.maor.roboticarm.block.RoboticArmBlock.FACING).toYRot()));
        boolean working = be.isRunning() && be.getStatus() == RoboticArmBlockEntity.Status.WORKING;
        float time = be.getLevel().getGameTime() + partial;
        model.animate(time, working);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }
}
