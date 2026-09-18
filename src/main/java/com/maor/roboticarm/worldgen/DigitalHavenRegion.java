package com.maor.roboticarm.worldgen;

import com.maor.roboticarm.RoboticArm;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class DigitalHavenRegion extends Region {
    public static final ResourceKey<Biome> DIGITAL_HAVEN = ResourceKey.create(net.minecraft.core.registries.Registries.BIOME, RoboticArm.id("digital_haven"));
    public DigitalHavenRegion() { super(RoboticArm.id("digital_haven_region"), RegionType.OVERWORLD, 2); }
    @Override public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addModifiedVanillaOverworldBiomes(mapper, builder -> builder.replaceBiome(Biomes.PLAINS, DIGITAL_HAVEN));
    }
}
