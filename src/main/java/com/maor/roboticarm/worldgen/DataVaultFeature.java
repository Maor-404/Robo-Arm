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

public class DataVaultFeature extends Feature<NoneFeatureConfiguration> {
    public DataVaultFeature() { super(Codec.unit(NoneFeatureConfiguration.INSTANCE)); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos o = context.origin();
        var level = context.level();
        if (!level.getBlockState(o).isSolidRender(level, o) || !level.getBlockState(o.above(3)).isSolidRender(level, o.above(3))) return false;
        for (int x=-2;x<=2;x++) for(int z=-2;z<=2;z++) for(int y=0;y<4;y++) {
            BlockPos p = o.offset(x, y, z);
            if (x == 0 && z == 0 && y == 1) level.setBlock(p, Blocks.CHEST.defaultBlockState(), 2);
            else if (x == 0 && z == 0 && (y == 1 || y == 2)) level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            else if (x == -2 || x == 2 || z == -2 || z == 2 || y == 0 || y == 3)
                level.setBlock(p, context.random().nextInt(6) == 0 ? RoboticArm.CABLE_BLOCK.get().defaultBlockState() :
                        RoboticArm.BROKEN_MACHINE.get().defaultBlockState(), 2);
            else level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        }
        RandomizableContainer.setBlockEntityLootTable(level, context.random(), o.offset(0, 1, 0),
                ResourceKey.create(Registries.LOOT_TABLE, RoboticArm.id("chests/data_vault")));
        return true;
    }
}
