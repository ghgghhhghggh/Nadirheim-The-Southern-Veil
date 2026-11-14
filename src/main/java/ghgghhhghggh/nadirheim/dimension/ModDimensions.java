package ghgghhhghggh.nadirheim.dimension;

import com.mojang.datafixers.util.Pair;
import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.worldgen.ModOreGeneration;
import ghgghhhghggh.nadirheim.worldgen.ModTreeFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.timeline.Timeline;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * La dimension Nadirheim (terre creuse). Tout est "data-driven" : le type de dimension, les biomes, les réglages de
 * génération et la dimension elle-même sont des JSON écrits par la datagen (voir NadirheimDataGenerator).
 * PREMIÈRE VERSION :
 * - ciel ouvert avec le soleil intérieur FIXE au zénith : pas de cycle jour/nuit (aucune "timeline" de jour, donc
 *   le soleil garde l'angle par défaut = midi), pas de lune (renvoyée sous l'horizon), pas de pluie ;
 * - relief généré comme l'Overworld, mais SANS OCÉAN (voir bootstrapNoiseSettings) ;
 * - deux biomes, un par arbre : les plaines (arbre 1, clairsemé) et les bois (arbre 2, dense), répartis selon
 *   l'humidité du bruit de génération ;
 * - on y arrive en tombant du ciel (voir NadirheimPortalBlock).
 */
public class ModDimensions {
	public static final ResourceKey<Level> NADIRHEIM = ResourceKey.create(Registries.DIMENSION, Nadirheim.id("nadirheim"));
	public static final ResourceKey<LevelStem> NADIRHEIM_STEM = ResourceKey.create(Registries.LEVEL_STEM, Nadirheim.id("nadirheim"));
	public static final ResourceKey<DimensionType> NADIRHEIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Nadirheim.id("nadirheim"));
	public static final ResourceKey<NoiseGeneratorSettings> NADIRHEIM_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, Nadirheim.id("nadirheim"));
	public static final ResourceKey<Biome> NADIRHEIM_PLAINS = ResourceKey.create(Registries.BIOME, Nadirheim.id("nadirheim_plains"));
	public static final ResourceKey<Biome> NADIRHEIM_WOODS = ResourceKey.create(Registries.BIOME, Nadirheim.id("nadirheim_woods"));

	// Couleurs PROVISOIRES (à choisir avec l'utilisateur) : ciel et brouillard dorés sous le soleil intérieur
	private static final int SKY_COLOR = ARGB.opaque(0xF5D9A0);
	private static final int FOG_COLOR = ARGB.opaque(0xF8E4BD);
	private static final int GRASS_COLOR = 0x9FCB7A;
	private static final int FOLIAGE_COLOR = 0x7FB85A;
	private static final int WATER_COLOR = 0x3F76E4;

	public static void bootstrapDimensionTypes(BootstrapContext<DimensionType> context) {
		HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
		HolderGetter<Timeline> timelines = context.lookup(Registries.TIMELINE);
		EnvironmentAttributeMap attributes = EnvironmentAttributeMap.builder()
				.set(EnvironmentAttributes.SKY_COLOR, SKY_COLOR)
				.set(EnvironmentAttributes.FOG_COLOR, FOG_COLOR)
				.set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, -16119286)     // comme l'Overworld
				.set(EnvironmentAttributes.CLOUD_COLOR, ARGB.white(0.8F))
				.set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F)
				.set(EnvironmentAttributes.MOON_ANGLE, 180.0F)                // lune sous l'horizon (le soleil reste à 0 = midi)
				.set(EnvironmentAttributes.STAR_BRIGHTNESS, 0.0F)
				.set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
				.set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
				.set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)   // jamais nuit : le lit fixe juste le point de réapparition
				.set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
				.set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, false)
				.build();
		context.register(NADIRHEIM_TYPE, new DimensionType(
				true,                                   // temps fixe
				true,                                   // lumière du ciel
				false,                                  // pas de plafond
				false,                                  // pas de combat du dragon
				1.0,                                    // 1 bloc ici = 1 bloc dans l'Overworld
				-64, 384, 384,
				blocks.getOrThrow(BlockTags.INFINIBURN_OVERWORLD),
				0.0F,
				new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
				DimensionType.Skybox.OVERWORLD,
				CardinalLighting.Type.DEFAULT,
				attributes,
				timelines.getOrThrow(TimelineTags.UNIVERSAL),   // sans la timeline "day" : ni nuit, ni lever/coucher
				Optional.empty()));
	}

	public static void bootstrapBiomes(BootstrapContext<Biome> context) {
		HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
		context.register(NADIRHEIM_PLAINS, biome(placedFeatures, carvers, ModTreeFeatures.TREE1_PLACED));
		context.register(NADIRHEIM_WOODS, biome(placedFeatures, carvers, ModTreeFeatures.TREE2_PLACED));
	}

	// Les deux biomes ne diffèrent que par leur arbre (et sa densité, réglée dans ModTreeFeatures.bootstrapPlaced)
	private static Biome biome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> carvers,
			ResourceKey<PlacedFeature> trees) {
		MobSpawnSettings.Builder mobs = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.plainsSpawns(mobs);   // animaux de ferme, chevaux, et les monstres habituels dans les grottes

		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, carvers);
		// Comme OverworldBiomes.globalOverworldGeneration, SANS les géodes (calcite) ni les poches de granite, diorite,
		// andésite et tuf : le sous-sol de Nadirheim est entièrement en pierre glacée (voir bootstrapNoiseSettings)
		BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);
		BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
		BiomeDefaultFeatures.addDefaultSprings(generation);
		BiomeDefaultFeatures.addSurfaceFreezing(generation);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIRT);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRAVEL);
		generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.GLOW_LICHEN);
		// Seulement les minerais propres à Nadirheim (voir ModOreGeneration) : aucun minerai de l'Overworld
		for (ResourceKey<PlacedFeature> ore : List.of(ModOreGeneration.NADIRHEIM_VIOLINITE, ModOreGeneration.NADIRHEIM_MINERAL2,
				ModOreGeneration.NADIRHEIM_MINERAL1)) {
			generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ore);
		}
		BiomeDefaultFeatures.addDefaultSoftDisks(generation);
		generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, trees);
		BiomeDefaultFeatures.addDefaultFlowers(generation);
		BiomeDefaultFeatures.addPlainGrass(generation);
		BiomeDefaultFeatures.addDefaultMushrooms(generation);
		BiomeDefaultFeatures.addDefaultExtraVegetation(generation, true);

		return new Biome.BiomeBuilder()
				.hasPrecipitation(false)
				.temperature(0.8F)
				.downfall(0.4F)
				.setAttribute(EnvironmentAttributes.SKY_COLOR, SKY_COLOR)
				.setAttribute(EnvironmentAttributes.FOG_COLOR, FOG_COLOR)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.waterColor(WATER_COLOR)
						.grassColorOverride(GRASS_COLOR)
						.foliageColorOverride(FOLIAGE_COLOR)
						.build())
				.mobSpawnSettings(mobs.build())
				.generationSettings(generation.build())
				.build();
	}

	/*
	 * Réglages de génération = ceux de l'Overworld, SANS OCÉAN. Le relief vanilla dépend de la "continentalité" : en
	 * dessous d'environ -0,19 le terrain passe sous le niveau de la mer (océans). On refait les 5 fonctions de relief
	 * vanilla (offset, factor, jaggedness, depth, sloped_cheese) avec la même recette, mais en remplaçant la
	 * continentalité par max(continentalité, CONTINENTALNESS_FLOOR) : les zones d'océan sont alors générées comme des
	 * basses terres. Grottes, minerais, rivières et règles de surface restent celles de l'Overworld.
	 */
	private static final float CONTINENTALNESS_FLOOR = -0.05F;
	public static final ResourceKey<DensityFunction> OFFSET = densityKey("overworld_no_ocean/offset");
	public static final ResourceKey<DensityFunction> FACTOR = densityKey("overworld_no_ocean/factor");
	public static final ResourceKey<DensityFunction> JAGGEDNESS = densityKey("overworld_no_ocean/jaggedness");
	public static final ResourceKey<DensityFunction> DEPTH = densityKey("overworld_no_ocean/depth");
	public static final ResourceKey<DensityFunction> SLOPED_CHEESE = densityKey("overworld_no_ocean/sloped_cheese");
	// Fonction vanilla -> la nôtre
	private static final Map<ResourceKey<DensityFunction>, ResourceKey<DensityFunction>> REPLACEMENTS = Map.of(
			NoiseRouterData.OFFSET, OFFSET, NoiseRouterData.FACTOR, FACTOR, NoiseRouterData.JAGGEDNESS, JAGGEDNESS,
			NoiseRouterData.DEPTH, DEPTH, NoiseRouterData.SLOPED_CHEESE, SLOPED_CHEESE);

	private static ResourceKey<DensityFunction> densityKey(String path) {
		return ResourceKey.create(Registries.DENSITY_FUNCTION, Nadirheim.id(path));
	}

	public static void bootstrapDensityFunctions(BootstrapContext<DensityFunction> context) {
		HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);
		DensityFunction continents = DensityFunctions.max(NoiseRouterData.getFunction(functions, NoiseRouterData.CONTINENTS),
				DensityFunctions.constant(CONTINENTALNESS_FLOOR));
		DensityFunction erosion = NoiseRouterData.getFunction(functions, NoiseRouterData.EROSION);
		DensityFunction jaggedNoise = DensityFunctions.noise(noises.getOrThrow(Noises.JAGGED), 1500.0, 0.0);
		// Même recette que NoiseRouterData.bootstrap pour l'Overworld, avec notre continentalité
		NoiseRouterData.registerTerrainNoises(context, functions, jaggedNoise, continents, erosion, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);
	}

	public static void bootstrapNoiseSettings(BootstrapContext<NoiseGeneratorSettings> context) {
		HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
		NoiseRouter vanilla = NoiseRouterData.overworld(functions, context.lookup(Registries.NOISE), false, false);
		// Remplace les références aux 5 fonctions de relief vanilla par les nôtres, sans toucher au reste (les autres
		// références restent des références : on ne lit jamais leur contenu, inaccessible pendant la datagen)
		DensityFunction.Visitor swap = new DensityFunction.Visitor() {
			@Override
			public DensityFunction apply(DensityFunction input) {
				if (input instanceof DensityFunctions.HolderHolder holder) {
					ResourceKey<DensityFunction> replacement = holder.function().unwrapKey().map(REPLACEMENTS::get).orElse(null);
					return replacement == null ? input : NoiseRouterData.getFunction(functions, replacement);
				}
				return input.mapChildren(this);
			}
		};
		NoiseRouter router = new NoiseRouter(vanilla.barrierNoise(), vanilla.fluidLevelFloodednessNoise(), vanilla.fluidLevelSpreadNoise(),
				vanilla.lavaNoise(), vanilla.temperature(), vanilla.vegetation(), vanilla.continents(), vanilla.erosion(), swap.apply(vanilla.depth()),
				vanilla.ridges(), swap.apply(vanilla.preliminarySurfaceLevel()), swap.apply(vanilla.finalDensity()),
				vanilla.veinToggle(), vanilla.veinRidged(), vanilla.veinGap());
		// Règles de surface vanilla, mais tout le sous-sol est en PIERRE GLACÉE : c'est le bloc par défaut (à la place
		// de la pierre), et la règle "ardoise sous Y 0" vanilla est devancée par la même règle qui pose de la pierre
		// glacée (même nom de bruit "deepslate" -> même dégradé). Le sol de bedrock vanilla est repris tel quel avant.
		// Les filons de cuivre / fer (ore veins), qui apportent granite et tuf, sont désactivés.
		SurfaceRules.RuleSource icyUnderground = SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
						SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())),
				SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)),
						SurfaceRules.state(ModBlocks.ICY_STONE.defaultBlockState())),
				SurfaceRuleData.overworld(context.lookup(Registries.BIOME)));
		context.register(NADIRHEIM_NOISE, new NoiseGeneratorSettings(new NoiseSettings(-64, 384, 1, 2),
				ModBlocks.ICY_STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), router,
				icyUnderground, new OverworldBiomeBuilder().spawnTarget(),
				63, false, true, false, false));
	}

	public static void bootstrapLevelStems(BootstrapContext<LevelStem> context) {
		HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);

		// Répartition des biomes par le bruit d'humidité : sec = plaines (arbre 1), humide = bois (arbre 2)
		Climate.Parameter any = Climate.Parameter.span(-1.0F, 1.0F);
		List<Pair<Climate.ParameterPoint, Holder<Biome>>> biomeList = List.of(
				Pair.of(Climate.parameters(any, Climate.Parameter.span(-1.0F, 0.0F), any, any, any, any, 0.0F), biomes.getOrThrow(NADIRHEIM_PLAINS)),
				Pair.of(Climate.parameters(any, Climate.Parameter.span(0.0F, 1.0F), any, any, any, any, 0.0F), biomes.getOrThrow(NADIRHEIM_WOODS)));

		context.register(NADIRHEIM_STEM, new LevelStem(dimensionTypes.getOrThrow(NADIRHEIM_TYPE),
				new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(biomeList)),
						noiseSettings.getOrThrow(NADIRHEIM_NOISE))));
	}
}
