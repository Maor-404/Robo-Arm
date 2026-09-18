package com.maor.roboticarm.worldgen;

import com.maor.roboticarm.RoboticArm;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DataVaultFeature extends Feature<NoneFeatureConfiguration> {
    public DataVaultFeature() { super(Codec.unit(NoneFeatureConfiguration.INSTANCE)); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos o = context.origin().atY(Math.max(20, Math.min(40, context.origin().getY())));
        for (int x=-2;x<=2;x++) for(int z=-2;z<=2;z++) for(int y=0;y<4;y++) {
            if (x == -2 || x == 2 || z == -2 || z == 2 || y == 0 || y == 3)
                context.level().setBlock(o.offset(x,y,z), RoboticArm.BROKEN_MACHINE.get().defaultBlockState(), 2);
        }
        return true;
    }
}
