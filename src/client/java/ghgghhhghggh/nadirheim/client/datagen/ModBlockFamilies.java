package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import net.minecraft.data.BlockFamily;

import java.util.List;

/*
 * Familles de blocs (comme BlockFamilies en vanilla) : un bloc de base + ses variantes.
 * À partir d'une famille, la datagen génère d'un coup les modèles (ModModelProvider)
 * et les recettes d'établi et de tailleur de pierre (ModRecipeProvider).
 */
public class ModBlockFamilies {
	public static final BlockFamily ICY_STONE = new BlockFamily.Builder(ModBlocks.ICY_STONE)
			.stairs(ModBlocks.ICY_STONE_STAIRS)
			.slab(ModBlocks.ICY_STONE_SLAB)
			.wall(ModBlocks.ICY_STONE_WALL)
			.generateStonecutterRecipe()
			.getFamily();

	public static final BlockFamily COBBLED_ICY_STONE = new BlockFamily.Builder(ModBlocks.COBBLED_ICY_STONE)
			.stairs(ModBlocks.COBBLED_ICY_STONE_STAIRS)
			.slab(ModBlocks.COBBLED_ICY_STONE_SLAB)
			.wall(ModBlocks.COBBLED_ICY_STONE_WALL)
			.generateStonecutterRecipe()
			.getFamily();

	public static final BlockFamily CUT_ICY_STONE = new BlockFamily.Builder(ModBlocks.CUT_ICY_STONE)
			.stairs(ModBlocks.CUT_ICY_STONE_STAIRS)
			.slab(ModBlocks.CUT_ICY_STONE_SLAB)
			.wall(ModBlocks.CUT_ICY_STONE_WALL)
			.generateStonecutterRecipe()
			.getFamily();

	// Planches des arbres de Nadirheim (recettes d'établi vanilla : escaliers, dalles, barrières...)
	public static final BlockFamily TREE1_PLANKS = new BlockFamily.Builder(ModBlocks.TREE1_PLANKS)
			.stairs(ModBlocks.TREE1_STAIRS)
			.slab(ModBlocks.TREE1_SLAB)
			.fence(ModBlocks.TREE1_FENCE)
			.fenceGate(ModBlocks.TREE1_FENCE_GATE)
			.pressurePlate(ModBlocks.TREE1_PRESSURE_PLATE)
			.button(ModBlocks.TREE1_BUTTON)
			.door(ModBlocks.TREE1_DOOR)
			.trapdoor(ModBlocks.TREE1_TRAPDOOR)
			.recipeGroupPrefix("wooden")
			.recipeUnlockedBy("has_planks")
			.getFamily();

	public static final BlockFamily TREE2_PLANKS = new BlockFamily.Builder(ModBlocks.TREE2_PLANKS)
			.stairs(ModBlocks.TREE2_STAIRS)
			.slab(ModBlocks.TREE2_SLAB)
			.fence(ModBlocks.TREE2_FENCE)
			.fenceGate(ModBlocks.TREE2_FENCE_GATE)
			.pressurePlate(ModBlocks.TREE2_PRESSURE_PLATE)
			.button(ModBlocks.TREE2_BUTTON)
			.door(ModBlocks.TREE2_DOOR)
			.trapdoor(ModBlocks.TREE2_TRAPDOOR)
			.recipeGroupPrefix("wooden")
			.recipeUnlockedBy("has_planks")
			.getFamily();

	public static final List<BlockFamily> ALL = List.of(ICY_STONE, COBBLED_ICY_STONE, CUT_ICY_STONE, TREE1_PLANKS, TREE2_PLANKS);
}
