package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.worldgen.ModStructures;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

// Biomes où apparaissent les structures du mod
public class ModBiomeTagProvider extends FabricTagsProvider<Biome> {
	public ModBiomeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, Registries.BIOME, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// Fontaine : plaines, forêts, désert, savanes, bosquet de cerisiers, jungles, taïgas, badlands, champignons
		builder(ModStructures.HAS_FONTAINE)
				.add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT, Biomes.CHERRY_GROVE, Biomes.MUSHROOM_FIELDS)
				.forceAddTag(BiomeTags.IS_FOREST)
				.forceAddTag(BiomeTags.IS_SAVANNA)
				.forceAddTag(BiomeTags.IS_JUNGLE)
				.forceAddTag(BiomeTags.IS_TAIGA)
				.forceAddTag(BiomeTags.IS_BADLANDS);
	}
}
