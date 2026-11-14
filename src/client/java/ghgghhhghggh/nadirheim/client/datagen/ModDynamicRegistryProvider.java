package ghgghhhghggh.nadirheim.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

// Écrit en JSON les éléments de registres "dynamiques" (définis par des données, pas par du code) :
// features de génération (ModOreGeneration), matériau d'ornement (ModMaterials) enchantements (ModEnchantments) et structures (ModStructures)
public class ModDynamicRegistryProvider extends FabricDynamicRegistryProvider {
	public ModDynamicRegistryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, Entries entries) {
		entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
		entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
		entries.addAll(registries.lookupOrThrow(Registries.TRIM_MATERIAL));
		entries.addAll(registries.lookupOrThrow(Registries.ENCHANTMENT));
		entries.addAll(registries.lookupOrThrow(Registries.TEMPLATE_POOL));
		entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
		entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
		entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE));
		entries.addAll(registries.lookupOrThrow(Registries.DENSITY_FUNCTION));
		entries.addAll(registries.lookupOrThrow(Registries.NOISE_SETTINGS));
		entries.addAll(registries.lookupOrThrow(Registries.BIOME));
		entries.addAll(registries.lookupOrThrow(Registries.LEVEL_STEM));
	}

	@Override
	public String getName() {
		return "Dynamic Registries";
	}
}
