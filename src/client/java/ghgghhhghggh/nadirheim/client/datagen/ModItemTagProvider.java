package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// La violinite répare les outils et l'armure en violinite (enclume)
		builder(ModMaterials.VIOLINITE_TOOL_MATERIALS).add(key(ModItems.VIOLINITE));
		builder(ModMaterials.REPAIRS_VIOLINITE_ARMOR).add(key(ModItems.VIOLINITE));
		builder(ModMaterials.MINERAL2_TOOL_MATERIALS).add(key(ModItems.MINERAL2));
		builder(ModMaterials.MINERAL1_TOOL_MATERIALS).add(key(ModItems.MINERAL1));

		// La violinite est acceptée comme matériau d'ornement à la table de forge
		builder(ItemTags.TRIM_MATERIALS).add(key(ModItems.VIOLINITE));

		// Torpille s'applique à tout ce qui accepte les enchantements d'arc ou d'arbalète
		// (forceAddTag : ces tags vanilla ne sont pas connus du générateur, on saute la vérification)
		builder(ModEnchantments.TORPEDO_ENCHANTABLE).forceAddTag(ItemTags.BOW_ENCHANTABLE).forceAddTag(ItemTags.CROSSBOW_ENCHANTABLE);

		// Arbres : les bûches et le bois servent de combustible (comme les bûches vanilla) ; les feuilles sont des feuilles
		builder(BlockItemTags.LOGS_THAT_BURN.item()).add(key(ModBlocks.TREE1_LOG.asItem())).add(key(ModBlocks.TREE1_WOOD.asItem()))
				.add(key(ModBlocks.TREE2_LOG.asItem())).add(key(ModBlocks.TREE2_WOOD.asItem()))
				.add(key(ModBlocks.STRIPPED_TREE1_LOG.asItem())).add(key(ModBlocks.STRIPPED_TREE1_WOOD.asItem())).add(key(ModBlocks.STRIPPED_TREE2_LOG.asItem())).add(key(ModBlocks.STRIPPED_TREE2_WOOD.asItem()));
		builder(ConventionalItemTags.STRIPPED_LOGS).add(key(ModBlocks.STRIPPED_TREE1_LOG.asItem())).add(key(ModBlocks.STRIPPED_TREE2_LOG.asItem()));
		builder(ConventionalItemTags.STRIPPED_WOODS).add(key(ModBlocks.STRIPPED_TREE1_WOOD.asItem())).add(key(ModBlocks.STRIPPED_TREE2_WOOD.asItem()));
		builder(ItemTags.LEAVES).add(key(ModBlocks.TREE1_LEAVES.asItem())).add(key(ModBlocks.TREE2_LEAVES.asItem()));
		builder(BlockItemTags.SAPLINGS.item()).add(key(ModBlocks.TREE1_SAPLING.asItem())).add(key(ModBlocks.TREE2_SAPLING.asItem()));

		// Planches et formes (mêmes tags que pour les blocs) ; tags par arbre pour la recette des planches
		builder(BlockItemTags.PLANKS.item()).add(key(ModBlocks.TREE1_PLANKS.asItem())).add(key(ModBlocks.TREE2_PLANKS.asItem()));
		builder(BlockItemTags.WOODEN_STAIRS.item()).add(key(ModBlocks.TREE1_STAIRS.asItem())).add(key(ModBlocks.TREE2_STAIRS.asItem()));
		builder(BlockItemTags.WOODEN_SLABS.item()).add(key(ModBlocks.TREE1_SLAB.asItem())).add(key(ModBlocks.TREE2_SLAB.asItem()));
		builder(BlockItemTags.WOODEN_FENCES.item()).add(key(ModBlocks.TREE1_FENCE.asItem())).add(key(ModBlocks.TREE2_FENCE.asItem()));
		builder(BlockItemTags.FENCE_GATES.item()).add(key(ModBlocks.TREE1_FENCE_GATE.asItem())).add(key(ModBlocks.TREE2_FENCE_GATE.asItem()));
		builder(BlockItemTags.WOODEN_PRESSURE_PLATES.item()).add(key(ModBlocks.TREE1_PRESSURE_PLATE.asItem())).add(key(ModBlocks.TREE2_PRESSURE_PLATE.asItem()));
		builder(BlockItemTags.WOODEN_BUTTONS.item()).add(key(ModBlocks.TREE1_BUTTON.asItem())).add(key(ModBlocks.TREE2_BUTTON.asItem()));
		builder(BlockItemTags.WOODEN_DOORS.item()).add(key(ModBlocks.TREE1_DOOR_ITEM)).add(key(ModBlocks.TREE2_DOOR_ITEM));
		builder(BlockItemTags.WOODEN_TRAPDOORS.item()).add(key(ModBlocks.TREE1_TRAPDOOR.asItem())).add(key(ModBlocks.TREE2_TRAPDOOR.asItem()));
		builder(ModBlocks.TREE1_LOGS).add(key(ModBlocks.TREE1_LOG.asItem())).add(key(ModBlocks.TREE1_WOOD.asItem()))
				.add(key(ModBlocks.STRIPPED_TREE1_LOG.asItem())).add(key(ModBlocks.STRIPPED_TREE1_WOOD.asItem()));
		builder(ModBlocks.TREE2_LOGS).add(key(ModBlocks.TREE2_LOG.asItem())).add(key(ModBlocks.TREE2_WOOD.asItem()))
				.add(key(ModBlocks.STRIPPED_TREE2_LOG.asItem())).add(key(ModBlocks.STRIPPED_TREE2_WOOD.asItem()));

		// Formes de pierre glacée (mêmes tags que pour les blocs)
		builder(BlockItemTags.STAIRS.item()).add(key(ModBlocks.ICY_STONE_STAIRS.asItem())).add(key(ModBlocks.COBBLED_ICY_STONE_STAIRS.asItem())).add(key(ModBlocks.CUT_ICY_STONE_STAIRS.asItem()));
		builder(BlockItemTags.SLABS.item()).add(key(ModBlocks.ICY_STONE_SLAB.asItem())).add(key(ModBlocks.COBBLED_ICY_STONE_SLAB.asItem())).add(key(ModBlocks.CUT_ICY_STONE_SLAB.asItem()));
		builder(ItemTags.WALLS).add(key(ModBlocks.ICY_STONE_WALL.asItem())).add(key(ModBlocks.COBBLED_ICY_STONE_WALL.asItem())).add(key(ModBlocks.CUT_ICY_STONE_WALL.asItem()));

		// La pierre glacée brute remplace la roche pour les outils en pierre et le four (comme la roche des abîmes)
		builder(ItemTags.STONE_TOOL_MATERIALS).add(key(ModBlocks.COBBLED_ICY_STONE.asItem()));
		builder(ItemTags.STONE_CRAFTING_MATERIALS).add(key(ModBlocks.COBBLED_ICY_STONE.asItem()));

		// Tags vanilla : ils décident quels enchantements s'appliquent, que les armures
		// acceptent les ornements (trims), que les outils cassent les pots décorés, etc.
		builder(ItemTags.SWORDS).add(key(ModItems.VIOLINITE_SWORD));
		builder(ItemTags.SPEARS).add(key(ModItems.VIOLINITE_SPEAR));
		builder(ItemTags.SHOVELS).add(key(ModItems.VIOLINITE_SHOVEL));
		builder(ItemTags.PICKAXES).add(key(ModItems.VIOLINITE_PICKAXE));
		builder(ItemTags.AXES).add(key(ModItems.VIOLINITE_AXE));
		builder(ItemTags.HOES).add(key(ModItems.VIOLINITE_HOE));
		builder(ItemTags.SWORDS).add(key(ModItems.MINERAL2_SWORD)).add(key(ModItems.MINERAL1_SWORD));
		builder(ItemTags.SHOVELS).add(key(ModItems.MINERAL2_SHOVEL)).add(key(ModItems.MINERAL1_SHOVEL));
		builder(ItemTags.PICKAXES).add(key(ModItems.MINERAL2_PICKAXE)).add(key(ModItems.MINERAL1_PICKAXE));
		builder(ItemTags.AXES).add(key(ModItems.MINERAL2_AXE)).add(key(ModItems.MINERAL1_AXE));
		builder(ItemTags.HEAD_ARMOR).add(key(ModItems.VIOLINITE_HELMET));
		builder(ItemTags.CHEST_ARMOR).add(key(ModItems.VIOLINITE_CHESTPLATE));
		builder(ItemTags.LEG_ARMOR).add(key(ModItems.VIOLINITE_LEGGINGS));
		builder(ItemTags.FOOT_ARMOR).add(key(ModItems.VIOLINITE_BOOTS));
	}

	private static ResourceKey<Item> key(Item item) {
		return item.builtInRegistryHolder().key();
	}
}
