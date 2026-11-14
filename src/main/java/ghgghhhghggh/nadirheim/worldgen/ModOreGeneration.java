package ghgghhhghggh.nadirheim.worldgen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.Map;

/*
 * La génération d'un minerai se fait en deux étapes :
 * - la "configured feature" décrit QUOI placer : un petit filon (taille 4, comme les petits
 *   filons de diamant), qui remplace la pierre par le minerai normal et la deepslate par le
 *   minerai de deepslate. Comme le diamant, la moitié des blocs touchant l'air sont supprimés
 *   (0.5F), donc on en voit peu sur les parois des grottes ;
 * - la "placed feature" décrit OÙ et COMBIEN : 2 filons par chunk, répartis uniformément entre
 *   Y=-64 et Y=-49. Un filon ne monte jamais au-dessus de son point de départ (il ne peut que
 *   descendre de 3 blocs, voir OreFeature), donc aucun minerai au-dessus de la couche -49.
 *   À comparer au diamant : 4 types de filons (7 + 2 + 4 par chunk, plus un gros filon rare).
 * Les JSON correspondants sont produits par la datagen (voir NadirheimDataGenerator).
 */
public class ModOreGeneration {
	public static final ResourceKey<ConfiguredFeature<?, ?>> VIOLINITE_ORE_CONFIGURED =
			ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("ore_violinite"));
	public static final ResourceKey<PlacedFeature> VIOLINITE_ORE_PLACED =
			ResourceKey.create(Registries.PLACED_FEATURE, Nadirheim.id("ore_violinite"));

	// Minerais de Nadirheim (un seul bloc par minerai, posé dans la pierre comme dans l'ardoise)
	public static final ResourceKey<ConfiguredFeature<?, ?>> MINERAL2_ORE_CONFIGURED = ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("ore_mineral2"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> MINERAL1_ORE_CONFIGURED = ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("ore_mineral1"));

	/*
	 * Répartition dans la dimension Nadirheim (voir dimension/ModDimensions), dont tout le sous-sol est en pierre
	 * glacée. AUCUN minerai de l'Overworld (décision de l'utilisateur du 2026-10-09) : seulement la violinite (version
	 * "glacée", bien plus abondante qu'en surface) dans les couches hautes, le minerai 2 en dessous de 0 et le
	 * minerai 1 tout en bas.
	 */
	public static final ResourceKey<ConfiguredFeature<?, ?>> ICY_VIOLINITE_ORE_CONFIGURED = ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("ore_icy_violinite"));
	public static final ResourceKey<PlacedFeature> NADIRHEIM_VIOLINITE = placedKey("nadirheim_violinite");
	public static final ResourceKey<PlacedFeature> NADIRHEIM_MINERAL2 = placedKey("nadirheim_mineral2");
	public static final ResourceKey<PlacedFeature> NADIRHEIM_MINERAL1 = placedKey("nadirheim_mineral1");

	private static ResourceKey<PlacedFeature> placedKey(String path) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Nadirheim.id(path));
	}

	public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
		for (var mineral : List.of(Map.entry(MINERAL2_ORE_CONFIGURED, ModBlocks.MINERAL2_ORE), Map.entry(MINERAL1_ORE_CONFIGURED, ModBlocks.MINERAL1_ORE))) {
			context.register(mineral.getKey(), new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
					OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), mineral.getValue().defaultBlockState()),
					OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), mineral.getValue().defaultBlockState())), 5, 0.3F)));
		}

		List<OreConfiguration.TargetBlockState> targets = List.of(
				OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), ModBlocks.VIOLINITE_ORE.defaultBlockState()),
				OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), ModBlocks.DEEPSLATE_VIOLINITE_ORE.defaultBlockState()));

		context.register(VIOLINITE_ORE_CONFIGURED, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, 4, 0.5F)));

		// Nadirheim : filons un peu plus gros, posés dans la pierre glacée, moins effacés sur les parois des grottes
		context.register(ICY_VIOLINITE_ORE_CONFIGURED, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
				OreConfiguration.target(new BlockMatchTest(ModBlocks.ICY_STONE), ModBlocks.ICY_VIOLINITE_ORE.defaultBlockState())), 5, 0.3F)));
	}

	public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

		context.register(VIOLINITE_ORE_PLACED, new PlacedFeature(configuredFeatures.getOrThrow(VIOLINITE_ORE_CONFIGURED), List.of(
				CountPlacement.of(2),
				InSquarePlacement.spread(),
				HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-49)),
				BiomeFilter.biome())));

		// Nadirheim : filons par chunk et couches (réglages PROVISOIRES)
		nadirheimOre(context, NADIRHEIM_VIOLINITE, configuredFeatures.getOrThrow(ICY_VIOLINITE_ORE_CONFIGURED), 10, 0, 72);
		nadirheimOre(context, NADIRHEIM_MINERAL2, configuredFeatures.getOrThrow(MINERAL2_ORE_CONFIGURED), 5, -40, 0);
		nadirheimOre(context, NADIRHEIM_MINERAL1, configuredFeatures.getOrThrow(MINERAL1_ORE_CONFIGURED), 3, -64, -36);
	}

	private static void nadirheimOre(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
			Holder<ConfiguredFeature<?, ?>> ore, int veinsPerChunk, int minY, int maxY) {
		context.register(key, new PlacedFeature(ore, List.of(CountPlacement.of(veinsPerChunk), InSquarePlacement.spread(),
				HeightRangePlacement.uniform(VerticalAnchor.absolute(minY), VerticalAnchor.absolute(maxY)), BiomeFilter.biome())));
	}

	public static void initialize() {
		// Ajoute le minerai à tous les biomes de l'Overworld
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, VIOLINITE_ORE_PLACED);
	}
}
