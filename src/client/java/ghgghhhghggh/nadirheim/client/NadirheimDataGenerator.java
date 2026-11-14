package ghgghhhghggh.nadirheim.client;

import ghgghhhghggh.nadirheim.client.color.XpOrbTint;
import ghgghhhghggh.nadirheim.client.datagen.ModAdvancementProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModAtlasProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModBiomeTagProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModBlockLootProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModBlockTagProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModChestLootProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModDynamicRegistryProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModEnchantmentTagProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModEquipmentAssetProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModFluidTagProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModItemTagProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModLanguageProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModModelProvider;
import ghgghhhghggh.nadirheim.client.datagen.ModRecipeProvider;
import ghgghhhghggh.nadirheim.dimension.ModDimensions;
import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import ghgghhhghggh.nadirheim.worldgen.ModOreGeneration;
import ghgghhhghggh.nadirheim.worldgen.ModTreeFeatures;
import ghgghhhghggh.nadirheim.worldgen.ModStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class NadirheimDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		// Types de teinte du mod : il faut les connaître pour écrire les définitions d'objets (items/*.json)
		XpOrbTint.register();

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModAdvancementProvider::new);
		pack.addProvider(ModLanguageProvider.English::new);
		pack.addProvider(ModLanguageProvider.French::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModBlockLootProvider::new);
		pack.addProvider(ModChestLootProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModFluidTagProvider::new);
		pack.addProvider(ModBiomeTagProvider::new);
		pack.addProvider(ModEnchantmentTagProvider::new);
		pack.addProvider(ModDynamicRegistryProvider::new);
		pack.addProvider(ModEquipmentAssetProvider::new);
		pack.addProvider(ModAtlasProvider::new);
	}

	// Déclare les éléments de registres dynamiques du mod pour que ModDynamicRegistryProvider puisse les écrire
	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, context -> {
			ModOreGeneration.bootstrapConfigured(context);
			ModTreeFeatures.bootstrap(context);
		});
		registryBuilder.add(Registries.PLACED_FEATURE, context -> {
			ModOreGeneration.bootstrapPlaced(context);
			ModTreeFeatures.bootstrapPlaced(context);
		});
		// La dimension Nadirheim : type, biome, puis la dimension qui les assemble
		registryBuilder.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes);
		registryBuilder.add(Registries.DENSITY_FUNCTION, ModDimensions::bootstrapDensityFunctions);
		registryBuilder.add(Registries.NOISE_SETTINGS, ModDimensions::bootstrapNoiseSettings);
		registryBuilder.add(Registries.BIOME, ModDimensions::bootstrapBiomes);
		registryBuilder.add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevelStems);
		registryBuilder.add(Registries.TRIM_MATERIAL, ModMaterials::bootstrapTrimMaterials);
		registryBuilder.add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);
		registryBuilder.add(Registries.TEMPLATE_POOL, ModStructures::bootstrapPools);
		registryBuilder.add(Registries.STRUCTURE, ModStructures::bootstrapStructures);
		registryBuilder.add(Registries.STRUCTURE_SET, ModStructures::bootstrapStructureSets);
	}
}
