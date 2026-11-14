package ghgghhhghggh.nadirheim.worldgen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.Optional;

/*
 * Arbres de Nadirheim : la forme de chaque arbre (un "configured feature", écrit en JSON par la datagen via
 * bootstrap) et le "grower" qui la fait pousser à partir de la pousse.
 * FORMES PROVISOIRES, en attendant celles que l'utilisateur imaginera :
 * - arbre 1 : tronc droit + feuillage plat en ombrelle (comme l'acacia), d'après la forme de sa pousse ;
 * - arbre 2 : tronc droit + feuillage en boule (comme le chêne), plus haut.
 * Plus tard : ces mêmes formes serviront à générer les arbres dans la dimension.
 */
public class ModTreeFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> TREE1 = ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("tree1"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> TREE2 = ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("tree2"));

	// Placement dans les biomes de Nadirheim (voir dimension/ModDimensions) : arbre 1 clairsemé dans les plaines,
	// arbre 2 dense dans les bois
	public static final ResourceKey<PlacedFeature> TREE1_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Nadirheim.id("tree1"));
	public static final ResourceKey<PlacedFeature> TREE2_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Nadirheim.id("tree2"));

	public static final TreeGrower TREE1_GROWER = new TreeGrower("nadirheim_tree1", Optional.empty(), Optional.of(TREE1), Optional.empty());
	public static final TreeGrower TREE2_GROWER = new TreeGrower("nadirheim_tree2", Optional.empty(), Optional.of(TREE2), Optional.empty());

	public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
		// treePlacement : réparti dans le chunk, posé sur la surface, seulement là où la pousse survivrait
		PlacementUtils.register(context, TREE1_PLACED, configured.getOrThrow(TREE1),
				VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1F, 1), ModBlocks.TREE1_SAPLING));
		PlacementUtils.register(context, TREE2_PLACED, configured.getOrThrow(TREE2),
				VegetationPlacements.treePlacement(PlacementUtils.countExtra(7, 0.1F, 1), ModBlocks.TREE2_SAPLING));
	}

	public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		// Sous le tronc : de la terre à la place de l'herbe, comme les arbres vanilla
		BlockStateProvider belowTrunk = TreeConfiguration.defaultPlaceBelowTreeTrunkProvider(biomes);

		FeatureUtils.register(context, TREE1, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(ModBlocks.TREE1_LOG), new StraightTrunkPlacer(5, 2, 0),
				BlockStateProvider.simple(ModBlocks.TREE1_LEAVES), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
				new TwoLayersFeatureSize(1, 0, 2), belowTrunk).ignoreVines().build());

		FeatureUtils.register(context, TREE2, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(ModBlocks.TREE2_LOG), new StraightTrunkPlacer(6, 3, 0),
				BlockStateProvider.simple(ModBlocks.TREE2_LEAVES), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
				new TwoLayersFeatureSize(1, 0, 1), belowTrunk).ignoreVines().build());
	}
}
