package com.maor.roboticarm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RoboticArmModel<T extends net.minecraft.world.entity.Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("roboticarm", "robotic_arm"), "main");
    private final ModelPart base;
    private final ModelPart lowerArm;
    private final ModelPart upperArm;
    private final ModelPart claw;
    public RoboticArmModel(ModelPart root) {
        base = root.getChild("base"); lowerArm = root.getChild("lower_arm"); upperArm = root.getChild("upper_arm"); claw = root.getChild("claw");
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition(); PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0,0).addBox(-6,-2,-6,12,2,12), PartPose.ZERO);
        root.addOrReplaceChild("lower_arm", CubeListBuilder.create().texOffs(0,14).addBox(-2,-8,-2,4,8,4), PartPose.offset(0,-2,0));
        root.addOrReplaceChild("upper_arm", CubeListBuilder.create().texOffs(16,14).addBox(-1,-7,-1,2,7,2), PartPose.offset(0,-10,0));
        root.addOrReplaceChild("claw", CubeListBuilder.create().texOffs(24,14).addBox(-3,-2,-1,6,2,2), PartPose.offset(0,-17,0));
        return LayerDefinition.create(mesh, 64, 64);
    }
    public void animate(float time, boolean running) {
        lowerArm.yRot = running ? (float)Math.sin(time*.15)*.5f : 0;
        upperArm.xRot = running ? (float)Math.sin(time*.12)*.25f : 0;
    }
    @Override public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { animate(ageInTicks, true); }
    @Override public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, int color) {
        base.render(pose, consumer, light, overlay, color); lowerArm.render(pose, consumer, light, overlay, color);
        upperArm.render(pose, consumer, light, overlay, color); claw.render(pose, consumer, light, overlay, color);
    }
}
