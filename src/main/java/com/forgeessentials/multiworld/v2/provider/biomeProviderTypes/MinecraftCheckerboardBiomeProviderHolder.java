package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

@FEBiomeProvider(providerName = "minecraft:checkerboard")
public class MinecraftCheckerboardBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeSource createBiomeProvider(Registry<Biome> biomes, Registry<StructureSet> structureSets, long seed) {
		final HolderSet<Biome> allowedBiomes = new ArrayList<>();
		Registry<Biome> biomes1 = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		for (Entry<ResourceKey<Biome>, Biome> biome : biomes1.entrySet()) {
		}
		return new CheckerboardColumnBiomeSource(allowedBiomes, 2);
	}

	@Override
	public String getClassName() {
		return CheckerboardColumnBiomeSource.class.getName();
	}
}
