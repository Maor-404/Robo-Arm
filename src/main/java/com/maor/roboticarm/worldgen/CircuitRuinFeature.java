package com.maor.roboticarm.worldgen;

import com.maor.roboticarm.RoboticArm;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CircuitRuinFeature extends Feature<NoneFeatureConfiguration> {
    public CircuitRuinFeature() { super(Codec.unit(NoneFeatureConfiguration.INSTANCE)); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        var level = context.level();
        for (int i = 0; i < 5; i++) {
            BlockPos p = origin.offset(context.random().nextInt(7)-3, 0, context.random().nextInt(7)-3);
            while (p.getY() > level.getMinBuildHeight() && level.isEmptyBlock(p.below())) p = p.below();
            level.setBlock(p, i % 3 == 0 ? RoboticArm.ABANDONED_CIRCUIT.get().defaultBlockState() :
                    i % 3 == 1 ? RoboticArm.CABLE_BLOCK.get().defaultBlockState() : RoboticArm.BROKEN_MACHINE.get().defaultBlockState(), 2);
        }
        return true;
    }
}
