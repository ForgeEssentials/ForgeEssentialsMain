package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.structure.StructureSet;

@FEBiomeProvider(providerName = "lotr:middle_earth")
public class MiddleEarthBiomeProviderHelper extends BiomeProviderHolderBase {
	@Override
	public BiomeSource createBiomeProvider(Registry<Biome> biomes, Registry<StructureSet> structureSets, long seed) {
		return ProvidersReflection.getBiomeProvider(getClassName(),
				new Class<?>[] { long.class, boolean.class, Registry.class }, new Object[] { seed, false, biomes });
	}

	@Override
	public String getClassName() {
		return "lotr.common.world.biome.provider.MiddleEarthBiomeProvider";
	}
}
