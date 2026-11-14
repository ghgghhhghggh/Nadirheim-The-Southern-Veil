package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// Ce que lâche chaque bloc quand on le casse (loot tables)
public class ModBlockLootProvider extends FabricBlockLootSubProvider {
	public ModBlockLootProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(packOutput, registriesFuture);
	}

	@Override
	public void generate() {
		// Comme le diamant : 1 violinite (plus avec Fortune), ou le minerai lui-même avec Toucher de soie
		add(ModBlocks.VIOLINITE_ORE, block -> createOreDrop(block, ModItems.VIOLINITE));
		add(ModBlocks.DEEPSLATE_VIOLINITE_ORE, block -> createOreDrop(block, ModItems.VIOLINITE));
		add(ModBlocks.ICY_VIOLINITE_ORE, block -> createOreDrop(block, ModItems.VIOLINITE));
		add(ModBlocks.MINERAL2_ORE, block -> createOreDrop(block, ModItems.MINERAL2));
		add(ModBlocks.MINERAL1_ORE, block -> createOreDrop(block, ModItems.MINERAL1));
		dropSelf(ModBlocks.VIOLINITE_BLOCK);
		dropSelf(ModBlocks.ICE_MUSHROOM);
		dropSelf(ModBlocks.SIEVE);
		// Arbres : bûches et bois se lâchent eux-mêmes ; feuilles = elles-mêmes avec cisailles ou Toucher de soie,
		// sinon parfois une pousse (chances vanilla : 5 % sans Fortune) et parfois 1-2 bâtons
		for (Block wood : new Block[]{ModBlocks.TREE1_LOG, ModBlocks.TREE1_WOOD, ModBlocks.TREE2_LOG, ModBlocks.TREE2_WOOD,
				ModBlocks.STRIPPED_TREE1_LOG, ModBlocks.STRIPPED_TREE1_WOOD, ModBlocks.STRIPPED_TREE2_LOG, ModBlocks.STRIPPED_TREE2_WOOD}) {
			dropSelf(wood);
		}
		add(ModBlocks.TREE1_LEAVES, createLeavesDrops(ModBlocks.TREE1_LEAVES, ModBlocks.TREE1_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
		add(ModBlocks.TREE2_LEAVES, createLeavesDrops(ModBlocks.TREE2_LEAVES, ModBlocks.TREE2_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
		dropSelf(ModBlocks.TREE1_SAPLING);
		dropSelf(ModBlocks.TREE2_SAPLING);
		dropPottedContents(ModBlocks.POTTED_TREE1_SAPLING);   // pot cassé -> le pot + la pousse
		dropPottedContents(ModBlocks.POTTED_TREE2_SAPLING);
		add(ModBlocks.XP_COIN_PILE, createSegmentedBlockDrops(ModBlocks.XP_COIN_PILE));   // rend 1 à 4 pièces
		dropSelf(ModBlocks.UNDERWATER_TORCH);   // la version murale utilise la même table (overrideLootTable)
		// Pierre glacée : donne de la pierre glacée brute, sauf avec Toucher de soie (comme pierre -> roche)
		add(ModBlocks.ICY_STONE, block -> createSingleItemTableWithSilkTouch(block, ModBlocks.COBBLED_ICY_STONE));
		dropSelf(ModBlocks.COBBLED_ICY_STONE);
		dropSelf(ModBlocks.CUT_ICY_STONE);
		for (Block block : List.of(ModBlocks.ICY_STONE_STAIRS, ModBlocks.COBBLED_ICY_STONE_STAIRS, ModBlocks.CUT_ICY_STONE_STAIRS,
				ModBlocks.ICY_STONE_WALL, ModBlocks.COBBLED_ICY_STONE_WALL, ModBlocks.CUT_ICY_STONE_WALL)) {
			dropSelf(block);
		}
		// Dalles : une dalle double lâche 2 dalles
		for (Block block : List.of(ModBlocks.TREE1_PLANKS, ModBlocks.TREE1_STAIRS, ModBlocks.TREE1_FENCE, ModBlocks.TREE1_FENCE_GATE,
				ModBlocks.TREE1_PRESSURE_PLATE, ModBlocks.TREE1_BUTTON, ModBlocks.TREE2_PLANKS, ModBlocks.TREE2_STAIRS,
				ModBlocks.TREE2_FENCE, ModBlocks.TREE2_FENCE_GATE, ModBlocks.TREE2_PRESSURE_PLATE, ModBlocks.TREE2_BUTTON)) {
			dropSelf(block);
		}
		// Porte : seule la moitié du bas lâche la porte (sinon on en récupérerait deux)
		add(ModBlocks.TREE1_DOOR, createDoorTable(ModBlocks.TREE1_DOOR));
		dropSelf(ModBlocks.TREE1_TRAPDOOR);
		add(ModBlocks.TREE2_DOOR, createDoorTable(ModBlocks.TREE2_DOOR));
		dropSelf(ModBlocks.TREE2_TRAPDOOR);
		for (Block slab : List.of(ModBlocks.ICY_STONE_SLAB, ModBlocks.COBBLED_ICY_STONE_SLAB, ModBlocks.CUT_ICY_STONE_SLAB,
				ModBlocks.TREE1_SLAB, ModBlocks.TREE2_SLAB)) {
			add(slab, this::createSlabItemTable);
		}
	}
}
