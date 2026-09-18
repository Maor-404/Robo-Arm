package com.maor.roboticarm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class RoboticArmModel extends Model {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("roboticarm", "robotic_arm"), "main");
    private final ModelPart base;
    private final ModelPart lowerArm;
    private final ModelPart upperArm;
    private final ModelPart claw;
    public RoboticArmModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        base = root.getChild("base");
        lowerArm = root.getChild("lower_arm");
        upperArm = lowerArm.getChild("upper_arm");
        claw = upperArm.getChild("claw");
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition(); PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0,0).addBox(-7,22,-7,14,2,14), PartPose.ZERO);
        PartDefinition lower = root.addOrReplaceChild("lower_arm",
                CubeListBuilder.create().texOffs(0,4).addBox(-2,-8,-2,4,8,4), PartPose.offset(0,22,0));
        PartDefinition upper = lower.addOrReplaceChild("upper_arm",
                CubeListBuilder.create().texOffs(12,4).addBox(-1.5f,-7,-1.5f,3,7,3), PartPose.offset(0,-8,0));
        upper.addOrReplaceChild("claw",
                CubeListBuilder.create().texOffs(20,4).addBox(-3,-2,-1,6,2,2), PartPose.offset(0,-7,0));
        return LayerDefinition.create(mesh, 64, 64);
    }
    public void animate(float time, boolean running) {
        lowerArm.yRot = running ? (float)Math.sin(time * .15f) * .6f : 0;
        upperArm.xRot = running ? -.8f + (float)Math.sin(time * .3f) * .4f : -.5f;
        claw.xRot = running ? (float)Math.sin(time * .3f) * .08f : 0;
    }
    @Override public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, int color) {
        base.render(pose, consumer, light, overlay, color); lowerArm.render(pose, consumer, light, overlay, color);
    }
}
