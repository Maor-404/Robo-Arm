package com.maor.roboticarm.client;

import com.maor.roboticarm.entity.MysteriousVillagerEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MysteriousVillagerRenderer extends MobRenderer<MysteriousVillagerEntity, VillagerModel<MysteriousVillagerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("roboticarm", "textures/entity/mysterious_villager.png");
    public MysteriousVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), .5f);
    }
    @Override public ResourceLocation getTextureLocation(MysteriousVillagerEntity entity) { return TEXTURE; }
}
