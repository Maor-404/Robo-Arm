package com.maor.roboticarm.worldgen;

import com.maor.roboticarm.RoboticArm;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CircuitRuinFeature extends Feature<NoneFeatureConfiguration> {
    public CircuitRuinFeature() { super(Codec.unit(NoneFeatureConfiguration.INSTANCE)); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        var level = context.level();
        BlockPos surface = origin;
        while (surface.getY() > level.getMinBuildHeight() && level.isEmptyBlock(surface)) surface = surface.below();
        for (int i = 0; i < 5; i++) {
            BlockPos p = surface.offset(context.random().nextInt(7)-3, 1, context.random().nextInt(7)-3);
            while (p.getY() > level.getMinBuildHeight() && level.isEmptyBlock(p.below())) p = p.below();
            level.setBlock(p, i % 3 == 0 ? RoboticArm.ABANDONED_CIRCUIT.get().defaultBlockState() :
                    i % 3 == 1 ? RoboticArm.CABLE_BLOCK.get().defaultBlockState() : RoboticArm.BROKEN_MACHINE.get().defaultBlockState(), 2);
        }
        BlockPos chest = surface.above();
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 2);
        RandomizableContainer.setBlockEntityLootTable(level, context.random(), chest,
                ResourceKey.create(Registries.LOOT_TABLE, RoboticArm.id("chests/digital_haven_ruin")));
        return true;
    }
}
