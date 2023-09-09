package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public class TwilightForestDistBiomeProviderHelper implements BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		return ProvidersReflection.getBiomeProvider(getClassName(),
				new Class<?>[] { long.class, Registry.class }, new Object[] { seed, biomes });
	}

	@Override
	public String getClassName() {
		return "twilightforest.world.TFBiomeDistributor";
	}
}
