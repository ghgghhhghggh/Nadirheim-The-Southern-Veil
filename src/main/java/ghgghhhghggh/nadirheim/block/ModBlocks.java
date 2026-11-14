package ghgghhhghggh.nadirheim.block;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.worldgen.ModTreeFeatures;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class ModBlocks {
	// Les minerais copient les propriétés des minerais de diamant (dureté, son, pioche en fer requise)
	// et lâchent 3 à 7 points d'expérience quand on les casse
	public static final Block VIOLINITE_ORE = register("violinite_ore",
			properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));
	public static final Block DEEPSLATE_VIOLINITE_ORE = register("deepslate_violinite_ore",
			properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE));
	// Variante de Nadirheim : mêmes drops, posée dans la pierre glacée (texture à fond de pierre glacée de l'utilisateur)
	public static final Block ICY_VIOLINITE_ORE = register("icy_violinite_ore",
			properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).mapColor(MapColor.ICE));
	public static final Block VIOLINITE_BLOCK = register("violinite_block", Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK));

	// Minerais de Nadirheim (noms provisoires), sous la violinite : minerai 2 puis, tout en bas, minerai 1.
	// Une seule texture chacun (fond de roche bleutée dessiné par l'utilisateur), utilisée dans la pierre et l'ardoise.
	// Outils requis : voir ModBlockTagProvider (minerai 2 = diamant ou mieux, minerai 1 = outils en minéral 2 ou mieux)
	public static final Block MINERAL2_ORE = register("mineral2_ore",
			properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE).mapColor(MapColor.COLOR_GREEN));
	public static final Block MINERAL1_ORE = register("mineral1_ore",
			properties -> new DropExperienceBlock(UniformInt.of(5, 10), properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE).mapColor(MapColor.COLOR_RED).strength(6.0F, 6.0F));

	// Torche sous-marine : peut être posée dans l'eau, lumière 14 comme une torche normale.
	// Deux blocs comme en vanilla : posée au sol, et accrochée au mur (même item, même nom, même drop)
	public static final Block UNDERWATER_TORCH = registerBlockOnly("underwater_torch", UnderwaterTorchBlock::new, torchProperties());
	public static final Block UNDERWATER_WALL_TORCH = registerBlockOnly("underwater_wall_torch", UnderwaterWallTorchBlock::new,
			torchProperties().overrideLootTable(UNDERWATER_TORCH.getLootTable()).overrideDescription(UNDERWATER_TORCH.getDescriptionId()));
	public static final Item UNDERWATER_TORCH_ITEM = registerItem("underwater_torch",
			key -> new StandingAndWallBlockItem(UNDERWATER_TORCH, UNDERWATER_WALL_TORCH, Direction.DOWN,
					new Item.Properties().setId(key).useBlockDescriptionPrefix()));

	// Piliers de la salle du portail (donjon de glace), de haut en bas : chapiteau, fût haut, fût bas, base.
	// Incassables comme la bedrock (et donc impossibles à pousser au piston). Le chapiteau recevra plus tard
	// un Inner Star (voir PillarCapitalBlock), qui s'affiche en flottant au-dessus du pilier.
	public static final Block PILLAR_CAPITAL = register("pillar_capital", PillarCapitalBlock::new,
			pillarProperties().lightLevel(state -> state.getValue(PillarCapitalBlock.HAS_STAR) ? 15 : 0));
	public static final Block PILLAR_SHAFT_UPPER = register("pillar_shaft_upper", Block::new, pillarProperties());
	public static final Block PILLAR_SHAFT_LOWER = register("pillar_shaft_lower", Block::new, pillarProperties());
	public static final Block PILLAR_BASE = register("pillar_base", Block::new, pillarProperties());

	// Arbres de Nadirheim. NOMS PROVISOIRES ("arbre 1" / "arbre 2") en attendant ceux de l'utilisateur.
	// Bûche : se pose dans les 3 sens (dessus = cernes, côtés = écorce) ; bois = écorce sur les 6 faces ;
	// feuilles : disparaissent loin d'une bûche, donnent des bâtons, et laissent tomber des feuilles de leur couleur
	// (particule vanilla "tinted_leaves" colorée : orange pour l'arbre 1, mauve pour l'arbre 2)
	public static final Block TREE1_LOG = register("tree1_log", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_PINK, MapColor.COLOR_GREEN, SoundType.WOOD));
	public static final Block TREE1_WOOD = register("tree1_wood", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_GREEN, MapColor.COLOR_GREEN, SoundType.WOOD));
	public static final Block TREE1_LEAVES = register("tree1_leaves",
			properties -> new UntintedParticleLeavesBlock(0.04F, ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, 0xFFE88836), properties),
			Blocks.leavesProperties(SoundType.GRASS).mapColor(MapColor.COLOR_ORANGE));
	public static final Block TREE2_LOG = register("tree2_log", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_RED, MapColor.TERRACOTTA_BROWN, SoundType.WOOD));
	public static final Block TREE2_WOOD = register("tree2_wood", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.TERRACOTTA_BROWN, MapColor.TERRACOTTA_BROWN, SoundType.WOOD));
	public static final Block TREE2_LEAVES = register("tree2_leaves",
			properties -> new UntintedParticleLeavesBlock(0.04F, ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, 0xFF8F74DC), properties),
			Blocks.leavesProperties(SoundType.GRASS).mapColor(MapColor.COLOR_PURPLE));

	// Pousses : poussent en arbre (formes PROVISOIRES dans worldgen/ModTreeFeatures), se posent aussi en pot de fleurs
	public static final Block TREE1_SAPLING = register("tree1_sapling", properties -> new SaplingBlock(ModTreeFeatures.TREE1_GROWER, properties),
			saplingProperties(MapColor.COLOR_ORANGE));
	public static final Block POTTED_TREE1_SAPLING = registerBlockOnly("potted_tree1_sapling",
			properties -> new FlowerPotBlock(TREE1_SAPLING, properties), Blocks.flowerPotProperties());
	public static final Block TREE2_SAPLING = register("tree2_sapling", properties -> new SaplingBlock(ModTreeFeatures.TREE2_GROWER, properties),
			saplingProperties(MapColor.COLOR_PURPLE));
	public static final Block POTTED_TREE2_SAPLING = registerBlockOnly("potted_tree2_sapling",
			properties -> new FlowerPotBlock(TREE2_SAPLING, properties), Blocks.flowerPotProperties());

	// Bûches et bois écorcés : on les obtient avec un clic droit à la hache sur la bûche ou le bois (voir initialize)
	public static final Block STRIPPED_TREE1_LOG = register("stripped_tree1_log", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_PINK, MapColor.COLOR_PINK, SoundType.WOOD));
	public static final Block STRIPPED_TREE1_WOOD = register("stripped_tree1_wood", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_PINK, MapColor.COLOR_PINK, SoundType.WOOD));
	public static final Block STRIPPED_TREE2_LOG = register("stripped_tree2_log", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_RED, MapColor.COLOR_RED, SoundType.WOOD));
	public static final Block STRIPPED_TREE2_WOOD = register("stripped_tree2_wood", RotatedPillarBlock::new,
			Blocks.logProperties(MapColor.COLOR_RED, MapColor.COLOR_RED, SoundType.WOOD));

	// Planches et leurs formes (comme le bois de cerisier vanilla). Sons et comportement des portillons, boutons et
	// plaques de pression : ceux du chêne en attendant un type de bois à nous (il faudra un WoodType / BlockSetType
	// propre quand viendront les portes, trappes et pancartes).
	public static final Block TREE1_PLANKS = register("tree1_planks", Block::new, planksProperties(MapColor.COLOR_PINK));
	public static final Block TREE1_STAIRS = registerStairs("tree1_stairs", TREE1_PLANKS);
	public static final Block TREE1_SLAB = registerSlab("tree1_slab", TREE1_PLANKS);
	public static final Block TREE1_FENCE = register("tree1_fence", FenceBlock::new, planksProperties(MapColor.COLOR_PINK));
	public static final Block TREE1_FENCE_GATE = register("tree1_fence_gate", properties -> new FenceGateBlock(WoodType.OAK, properties),
			planksProperties(MapColor.COLOR_PINK).forceSolidOn());
	public static final Block TREE1_PRESSURE_PLATE = register("tree1_pressure_plate",
			properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollision()
					.strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
	public static final Block TREE1_BUTTON = register("tree1_button", properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
			Blocks.buttonProperties());
	public static final Block TREE2_PLANKS = register("tree2_planks", Block::new, planksProperties(MapColor.COLOR_RED));
	public static final Block TREE2_STAIRS = registerStairs("tree2_stairs", TREE2_PLANKS);
	public static final Block TREE2_SLAB = registerSlab("tree2_slab", TREE2_PLANKS);
	public static final Block TREE2_FENCE = register("tree2_fence", FenceBlock::new, planksProperties(MapColor.COLOR_RED));
	public static final Block TREE2_FENCE_GATE = register("tree2_fence_gate", properties -> new FenceGateBlock(WoodType.OAK, properties),
			planksProperties(MapColor.COLOR_RED).forceSolidOn());
	public static final Block TREE2_PRESSURE_PLATE = register("tree2_pressure_plate",
			properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollision()
					.strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
	public static final Block TREE2_BUTTON = register("tree2_button", properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
			Blocks.buttonProperties());

	// Porte et trappe (arbre 1 ; arbre 2 quand ses textures existeront). Sons du chêne en attendant un type de bois
	// propre. La porte a un objet spécial (DoubleHighBlockItem : pose les 2 moitiés d'un coup)
	public static final Block TREE1_DOOR = registerBlockOnly("tree1_door", properties -> new DoorBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.BASS).strength(3.0F)
					.noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
	public static final Item TREE1_DOOR_ITEM = registerItem("tree1_door",
			key -> new DoubleHighBlockItem(TREE1_DOOR, new Item.Properties().setId(key).useBlockDescriptionPrefix()));
	public static final Block TREE1_TRAPDOOR = register("tree1_trapdoor", properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.BASS).strength(3.0F)
					.noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
	public static final Block TREE2_DOOR = registerBlockOnly("tree2_door", properties -> new DoorBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(3.0F)
					.noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
	public static final Item TREE2_DOOR_ITEM = registerItem("tree2_door",
			key -> new DoubleHighBlockItem(TREE2_DOOR, new Item.Properties().setId(key).useBlockDescriptionPrefix()));
	public static final Block TREE2_TRAPDOOR = register("tree2_trapdoor", properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(3.0F)
					.noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());

	// Tags "toutes les bûches de l'arbre" (bûche, bois, et plus tard les versions écorcées) : 1 bûche -> 4 planches
	public static final TagKey<Item> TREE1_LOGS = TagKey.create(Registries.ITEM, Nadirheim.id("tree1_logs"));
	public static final TagKey<Item> TREE2_LOGS = TagKey.create(Registries.ITEM, Nadirheim.id("tree2_logs"));

	// Portail de Nadirheim : bloc de TEST de l'effet visuel (pas encore de téléportation), mêmes propriétés que
	// la passerelle de l'End (traversable, lumière 15, incassable en survie). Seulement dans l'onglet du mod.
	public static final Block NADIRHEIM_PORTAL = register("nadirheim_portal", NadirheimPortalBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).noCollision().lightLevel(state -> 15)
					.strength(-1.0F, 3600000.0F).noLootTable().pushReaction(PushReaction.BLOCK));

	// Tas de pièces d'xp (1 à 4) : pas d'item à lui, c'est la pièce d'xp qui le pose (ModItems.XP_COIN).
	// Luit faiblement : 2 de lumière par pièce (une bougie allumée en donne 3)
	public static final Block XP_COIN_PILE = registerBlockOnly("xp_coin_pile", XpCoinPileBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noCollision().instabreak().sound(SoundType.CHAIN)
					.lightLevel(state -> 2 * state.getValue(XpCoinPileBlock.AMOUNT)).pushReaction(PushReaction.DESTROY));

	// Dalles du temple : pavé mosaïque en damier du donjon de glace. Comme les temples maçonniques qui traversent
	// les âges, il est incassable (mêmes valeurs que la bedrock) et n'a pas de recette : il n'existe que dans le temple.
	// Lieu sacré : aucune créature n'apparaît dessus (comme sur la bedrock).
	// Adrénochrome posé (liquide, voir fluid/AdrenochromeFluid). Pas d'objet : on le pose avec son seau (ModItems)
	public static final Block ADRENOCHROME = registerBlockOnly("adrenochrome", AdrenochromeLiquidBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).replaceable().noCollision().strength(100.0F)
					.pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));

	public static final Block TEMPLE_TILES = register("temple_tiles", Block::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM)
					.strength(-1.0F, 3600000.0F).noLootTable().isValidSpawn(Blocks::never).sound(SoundType.STONE));

	// Tamis : gravier -> sable / roche / silex (voir SieveBlockEntity). Bois, se casse vite à la hache.
	// noOcclusion : le modèle est ajouré (comme l'échafaudage), donc il ne doit pas cacher les faces des blocs voisins
	public static final Block SIEVE = register("sieve", SieveBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()
					.noOcclusion());

	// Pierre glacée : mêmes propriétés que la pierre (pioche requise), couleur glace sur les cartes.
	// Sa texture sert aussi de fond à l'onglet de progrès du mod
	public static final Block ICY_STONE = register("icy_stone", Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.ICE));
	public static final Block ICY_STONE_STAIRS = registerStairs("icy_stone_stairs", ICY_STONE);
	public static final Block ICY_STONE_SLAB = registerSlab("icy_stone_slab", ICY_STONE);
	public static final Block ICY_STONE_WALL = registerWall("icy_stone_wall", ICY_STONE);

	// Pierre glacée brute : ce que lâche la pierre glacée quand on la mine (comme pierre -> roche)
	public static final Block COBBLED_ICY_STONE = register("cobbled_icy_stone", Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.ICE));
	public static final Block COBBLED_ICY_STONE_STAIRS = registerStairs("cobbled_icy_stone_stairs", COBBLED_ICY_STONE);
	public static final Block COBBLED_ICY_STONE_SLAB = registerSlab("cobbled_icy_stone_slab", COBBLED_ICY_STONE);
	public static final Block COBBLED_ICY_STONE_WALL = registerWall("cobbled_icy_stone_wall", COBBLED_ICY_STONE);

	// Pierre glacée taillée : version décorative (comme le grès taillé)
	public static final Block CUT_ICY_STONE = register("cut_icy_stone", Block::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.ICE));
	public static final Block CUT_ICY_STONE_STAIRS = registerStairs("cut_icy_stone_stairs", CUT_ICY_STONE);
	public static final Block CUT_ICY_STONE_SLAB = registerSlab("cut_icy_stone_slab", CUT_ICY_STONE);
	public static final Block CUT_ICY_STONE_WALL = registerWall("cut_icy_stone_wall", CUT_ICY_STONE);

	// Champignon de glace : ne se pose que sur la neige et la glace (voir IceMushroomBlock)
	public static final Block ICE_MUSHROOM = register("ice_mushroom", IceMushroomBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.ICE)
					.noCollision()
					.randomTicks()
					.instabreak()
					.sound(SoundType.GRASS)
					.pushReaction(PushReaction.DESTROY));

	private static BlockBehaviour.Properties pillarProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM)
				.strength(-1.0F, 3600000.0F).noLootTable().isValidSpawn(Blocks::never).sound(SoundType.STONE);
	}

	// Pousses (valeurs vanilla)
	private static BlockBehaviour.Properties saplingProperties(MapColor color) {
		return BlockBehaviour.Properties.of().mapColor(color).noCollision().randomTicks().instabreak().sound(SoundType.GRASS)
				.pushReaction(PushReaction.DESTROY);
	}

	// Planches, barrières, portillons (valeurs des planches vanilla)
	private static BlockBehaviour.Properties planksProperties(MapColor color) {
		return BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F)
				.sound(SoundType.WOOD).ignitedByLava();
	}

	private static BlockBehaviour.Properties torchProperties() {
		return BlockBehaviour.Properties.of().noCollision().instabreak().lightLevel(state -> 14)
				.sound(SoundType.STONE).pushReaction(PushReaction.DESTROY);
	}

	// Bloc sans item (quand l'item est enregistré à part, comme pour les torches)
	private static Block registerBlockOnly(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Nadirheim.id(name));
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));
	}

	private static Item registerItem(String name, Function<ResourceKey<Item>, Item> factory) {
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Nadirheim.id(name));
		return Registry.register(BuiltInRegistries.ITEM, itemKey, factory.apply(itemKey));
	}

	// Escaliers, dalles et murets : mêmes propriétés que le bloc de base (comme Blocks.registerStair/Slab/Wall)
	private static Block registerStairs(String name, Block base) {
		return register(name, properties -> new StairBlock(base.defaultBlockState(), properties), BlockBehaviour.Properties.ofFullCopy(base));
	}

	private static Block registerSlab(String name, Block base) {
		return register(name, SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(base));
	}

	private static Block registerWall(String name, Block base) {
		return register(name, WallBlock::new, BlockBehaviour.Properties.ofFullCopy(base).forceSolidOn());
	}

	// Enregistre le bloc ET l'item correspondant (celui qu'on tient dans l'inventaire)
	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Nadirheim.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Nadirheim.id(name));
		Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void initialize() {
		// Le bois et les feuilles brûlent (mêmes valeurs que les arbres vanilla : chance de prendre feu, de se consumer)
		FlammableBlockRegistry flammable = FlammableBlockRegistry.getDefaultInstance();
		for (Block wood : new Block[]{TREE1_LOG, TREE1_WOOD, TREE2_LOG, TREE2_WOOD,
				STRIPPED_TREE1_LOG, STRIPPED_TREE1_WOOD, STRIPPED_TREE2_LOG, STRIPPED_TREE2_WOOD}) {
			flammable.add(wood, 5, 5);
		}
		// Hache sur une bûche / un bois -> version écorcée (garde l'orientation)
		StrippableBlockRegistry.register(TREE1_LOG, STRIPPED_TREE1_LOG);
		StrippableBlockRegistry.register(TREE1_WOOD, STRIPPED_TREE1_WOOD);
		StrippableBlockRegistry.register(TREE2_LOG, STRIPPED_TREE2_LOG);
		StrippableBlockRegistry.register(TREE2_WOOD, STRIPPED_TREE2_WOOD);
		for (Block planks : new Block[]{TREE1_PLANKS, TREE1_STAIRS, TREE1_SLAB, TREE1_FENCE, TREE1_FENCE_GATE,
				TREE2_PLANKS, TREE2_STAIRS, TREE2_SLAB, TREE2_FENCE, TREE2_FENCE_GATE}) {
			flammable.add(planks, 5, 20);
		}
		flammable.add(TREE1_LEAVES, 30, 60);
		flammable.add(TREE2_LEAVES, 30, 60);
		// Composteur : pousses et feuilles (30 %, comme en vanilla)
		for (Block plant : new Block[]{TREE1_SAPLING, TREE2_SAPLING, TREE1_LEAVES, TREE2_LEAVES}) {
			CompostableRegistry.INSTANCE.add(plant, 0.3F);
		}

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> {
					output.insertAfter(Blocks.DEEPSLATE_DIAMOND_ORE, VIOLINITE_ORE, DEEPSLATE_VIOLINITE_ORE, ICY_VIOLINITE_ORE, MINERAL2_ORE, MINERAL1_ORE);
					output.insertAfter(Blocks.RED_MUSHROOM, ICE_MUSHROOM);
					output.insertAfter(Blocks.BLUE_ICE, ICY_STONE);
					output.insertAfter(Blocks.CHERRY_LOG, TREE1_LOG, TREE2_LOG);
					output.insertAfter(Blocks.FLOWERING_AZALEA_LEAVES, TREE1_LEAVES, TREE2_LEAVES);
					output.insertAfter(Blocks.CHERRY_SAPLING, TREE1_SAPLING, TREE2_SAPLING);
				});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
			output.insertAfter(Blocks.COPPER_TORCH, UNDERWATER_TORCH_ITEM);
			output.insertAfter(Blocks.STONECUTTER, SIEVE);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
			output.insertAfter(Blocks.DIAMOND_BLOCK, VIOLINITE_BLOCK);
			output.insertAfter(Blocks.DEEPSLATE_TILE_WALL,
					ICY_STONE, ICY_STONE_STAIRS, ICY_STONE_SLAB, ICY_STONE_WALL,
					COBBLED_ICY_STONE, COBBLED_ICY_STONE_STAIRS, COBBLED_ICY_STONE_SLAB, COBBLED_ICY_STONE_WALL,
					CUT_ICY_STONE, CUT_ICY_STONE_STAIRS, CUT_ICY_STONE_SLAB, CUT_ICY_STONE_WALL,
					TEMPLE_TILES);
			output.insertAfter(Blocks.CHERRY_BUTTON,
					TREE1_LOG, TREE1_WOOD, STRIPPED_TREE1_LOG, STRIPPED_TREE1_WOOD, TREE1_PLANKS, TREE1_STAIRS, TREE1_SLAB, TREE1_FENCE, TREE1_FENCE_GATE, TREE1_DOOR_ITEM, TREE1_TRAPDOOR, TREE1_PRESSURE_PLATE, TREE1_BUTTON,
					TREE2_LOG, TREE2_WOOD, STRIPPED_TREE2_LOG, STRIPPED_TREE2_WOOD, TREE2_PLANKS, TREE2_STAIRS, TREE2_SLAB, TREE2_FENCE, TREE2_FENCE_GATE, TREE2_DOOR_ITEM, TREE2_TRAPDOOR, TREE2_PRESSURE_PLATE, TREE2_BUTTON);
		});
	}
}
