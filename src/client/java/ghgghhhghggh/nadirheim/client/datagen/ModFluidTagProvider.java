package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.fluid.ModFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FabricTagsProvider.FluidTagsProvider {
	public ModFluidTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// Les deux formes de l'adrénochrome (source + écoulement) : utilisé pour la nage (ModFluids.initialize)
		builder(ModFluids.ADRENOCHROME_TAG)
				.add(ModFluids.ADRENOCHROME.builtInRegistryHolder().key())
				.add(ModFluids.FLOWING_ADRENOCHROME.builtInRegistryHolder().key());
	}
}
