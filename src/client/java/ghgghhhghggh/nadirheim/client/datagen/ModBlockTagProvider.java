package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.IceMushroomBlock;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// Ces blocs se minent vite à la pioche...
		builder(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(ModBlocks.VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.DEEPSLATE_VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.VIOLINITE_BLOCK.builtInRegistryHolder().key())
				.add(ModBlocks.MINERAL2_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.MINERAL1_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_STONE.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_STONE_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_STONE_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_STONE_WALL.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_WALL.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_WALL.builtInRegistryHolder().key());

		// Tags vanilla des formes : les murets ne se relient entre eux que s'ils sont dans le tag "walls"
		builder(BlockTags.STAIRS)
				.add(ModBlocks.ICY_STONE_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_STAIRS.builtInRegistryHolder().key());
		builder(BlockTags.SLABS)
				.add(ModBlocks.ICY_STONE_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_SLAB.builtInRegistryHolder().key());
		builder(BlockTags.WALLS)
				.add(ModBlocks.ICY_STONE_WALL.builtInRegistryHolder().key())
				.add(ModBlocks.COBBLED_ICY_STONE_WALL.builtInRegistryHolder().key())
				.add(ModBlocks.CUT_ICY_STONE_WALL.builtInRegistryHolder().key());

		// ...et il faut au moins une pioche en fer pour qu'ils lâchent quelque chose
		builder(BlockTags.NEEDS_IRON_TOOL)
				.add(ModBlocks.VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.DEEPSLATE_VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.VIOLINITE_BLOCK.builtInRegistryHolder().key());

		// Tag commun "c:ores" (partagé entre mods) : la Charge des profondeurs épargne les blocs de ce tag
		builder(ConventionalBlockTags.ORES)
				.add(ModBlocks.VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.DEEPSLATE_VIOLINITE_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.ICY_VIOLINITE_ORE.builtInRegistryHolder().key());

		// Arbres : les bûches et le bois brûlent (et comptent comme des bûches : les feuilles ne disparaissent pas à
		// côté), se coupent à la hache ; les feuilles se cassent vite à la houe
		builder(BlockItemTags.LOGS_THAT_BURN.block())
				.add(ModBlocks.TREE1_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE1_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE1_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE2_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE2_WOOD.builtInRegistryHolder().key());
		// Tags communs entre mods (Fabric "c:") : bûches et bois écorcés
		builder(ConventionalBlockTags.STRIPPED_LOGS).add(ModBlocks.STRIPPED_TREE1_LOG.builtInRegistryHolder().key()).add(ModBlocks.STRIPPED_TREE2_LOG.builtInRegistryHolder().key());
		builder(ConventionalBlockTags.STRIPPED_WOODS).add(ModBlocks.STRIPPED_TREE1_WOOD.builtInRegistryHolder().key()).add(ModBlocks.STRIPPED_TREE2_WOOD.builtInRegistryHolder().key());
		builder(BlockItemTags.SAPLINGS.block()).add(ModBlocks.TREE1_SAPLING.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_SAPLING.builtInRegistryHolder().key());
		builder(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_TREE1_SAPLING.builtInRegistryHolder().key())
				.add(ModBlocks.POTTED_TREE2_SAPLING.builtInRegistryHolder().key());
		builder(BlockTags.LEAVES)
				.add(ModBlocks.TREE1_LEAVES.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_LEAVES.builtInRegistryHolder().key());
		builder(BlockTags.MINEABLE_WITH_HOE)
				.add(ModBlocks.TREE1_LEAVES.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_LEAVES.builtInRegistryHolder().key());

		// Planches et formes : tags vanilla du bois (combustible, recettes comme l'établi ou les bâtons, sons...)
		builder(BlockItemTags.PLANKS.block()).add(ModBlocks.TREE1_PLANKS.builtInRegistryHolder().key()).add(ModBlocks.TREE2_PLANKS.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_STAIRS.block()).add(ModBlocks.TREE1_STAIRS.builtInRegistryHolder().key()).add(ModBlocks.TREE2_STAIRS.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_SLABS.block()).add(ModBlocks.TREE1_SLAB.builtInRegistryHolder().key()).add(ModBlocks.TREE2_SLAB.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_FENCES.block()).add(ModBlocks.TREE1_FENCE.builtInRegistryHolder().key()).add(ModBlocks.TREE2_FENCE.builtInRegistryHolder().key());
		builder(BlockItemTags.FENCE_GATES.block()).add(ModBlocks.TREE1_FENCE_GATE.builtInRegistryHolder().key()).add(ModBlocks.TREE2_FENCE_GATE.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_PRESSURE_PLATES.block()).add(ModBlocks.TREE1_PRESSURE_PLATE.builtInRegistryHolder().key()).add(ModBlocks.TREE2_PRESSURE_PLATE.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_BUTTONS.block()).add(ModBlocks.TREE1_BUTTON.builtInRegistryHolder().key()).add(ModBlocks.TREE2_BUTTON.builtInRegistryHolder().key());

		builder(BlockItemTags.WOODEN_DOORS.block()).add(ModBlocks.TREE1_DOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_DOOR.builtInRegistryHolder().key());
		builder(BlockItemTags.WOODEN_TRAPDOORS.block()).add(ModBlocks.TREE1_TRAPDOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_TRAPDOOR.builtInRegistryHolder().key());

		// Minerais de Nadirheim : le minerai 2 demande au moins le diamant ; le minerai 1 n'est minable ni au diamant,
		// ni à la netherite / violinite (qui partagent le tag netherite) : il faut des outils en minéral 2 ou 1,
		// dont les tags "incorrect" sont vides (ils minent tout)
		// La pierre glacée est la roche de Nadirheim : les minerais s'y posent (stone_ore_replaceables) et les grottes,
		// lacs, sources, poches de terre / gravier la creusent comme de la pierre (base_stone_overworld)
		builder(BlockTags.BASE_STONE_OVERWORLD).add(ModBlocks.ICY_STONE.builtInRegistryHolder().key());
		builder(BlockTags.STONE_ORE_REPLACEABLES).add(ModBlocks.ICY_STONE.builtInRegistryHolder().key());

		builder(BlockTags.NEEDS_DIAMOND_TOOL)
				.add(ModBlocks.MINERAL2_ORE.builtInRegistryHolder().key())
				.add(ModBlocks.MINERAL1_ORE.builtInRegistryHolder().key());
		builder(BlockTags.INCORRECT_FOR_DIAMOND_TOOL).add(ModBlocks.MINERAL1_ORE.builtInRegistryHolder().key());
		builder(BlockTags.INCORRECT_FOR_NETHERITE_TOOL).add(ModBlocks.MINERAL1_ORE.builtInRegistryHolder().key());
		builder(ModMaterials.INCORRECT_FOR_MINERAL2_TOOL);
		builder(ModMaterials.INCORRECT_FOR_MINERAL1_TOOL);

		// Le tamis se casse vite à la hache
		builder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.SIEVE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_DOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_TRAPDOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_DOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_TRAPDOOR.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_PLANKS.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_FENCE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_FENCE_GATE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_PRESSURE_PLATE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_BUTTON.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_PLANKS.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_STAIRS.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_SLAB.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_FENCE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_FENCE_GATE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_PRESSURE_PLATE.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_BUTTON.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.TREE1_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.TREE2_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE1_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE1_WOOD.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE2_LOG.builtInRegistryHolder().key())
				.add(ModBlocks.STRIPPED_TREE2_WOOD.builtInRegistryHolder().key());

		// Blocs sur lesquels le champignon de glace peut pousser
		builder(IceMushroomBlock.PLACEABLE_ON)
				.add(Blocks.SNOW_BLOCK.builtInRegistryHolder().key())
				.add(Blocks.ICE.builtInRegistryHolder().key())
				.add(Blocks.PACKED_ICE.builtInRegistryHolder().key())
				.add(Blocks.BLUE_ICE.builtInRegistryHolder().key());
	}
}
