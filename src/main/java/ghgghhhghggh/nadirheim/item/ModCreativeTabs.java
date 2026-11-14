package ghgghhhghggh.nadirheim.item;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

// Onglet du mode créatif propre au mod. Les objets restent aussi rangés dans les onglets vanilla
// (voir ModItems.initialize et ModBlocks.initialize).
public class ModCreativeTabs {
	public static final ResourceKey<CreativeModeTab> NADIRHEIM =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Nadirheim.id("nadirheim"));

	public static void initialize() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, NADIRHEIM, FabricCreativeModeTab.builder()
				.title(Component.translatable("itemGroup.nadirheim"))       // nom de l'onglet (traduit par la datagen)
				.icon(() -> new ItemStack(ModItems.VIOLINITE))               // icône de l'onglet
				.displayItems((parameters, output) -> {                      // contenu, dans l'ordre d'affichage
					// Violinite : minerais, matériau, bloc, modèle d'amélioration
					output.accept(ModBlocks.VIOLINITE_ORE);
					output.accept(ModBlocks.DEEPSLATE_VIOLINITE_ORE);
					output.accept(ModBlocks.ICY_VIOLINITE_ORE);
					output.accept(ModItems.VIOLINITE);
					output.accept(ModBlocks.VIOLINITE_BLOCK);
					output.accept(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE);

					// Outils
					output.accept(ModItems.VIOLINITE_SHOVEL);
					output.accept(ModItems.VIOLINITE_PICKAXE);
					output.accept(ModItems.VIOLINITE_AXE);
					output.accept(ModItems.VIOLINITE_HOE);
					output.accept(ModItems.DEEP_CHARGE);

					// Armes
					output.accept(ModItems.VIOLINITE_SWORD);
					output.accept(ModItems.VIOLINITE_SPEAR);

					// Armures
					output.accept(ModItems.VIOLINITE_HELMET);
					output.accept(ModItems.VIOLINITE_CHESTPLATE);
					output.accept(ModItems.VIOLINITE_LEGGINGS);
					output.accept(ModItems.VIOLINITE_BOOTS);
					output.accept(ModItems.VIOLINITE_HORSE_ARMOR);
					output.accept(ModItems.VIOLINITE_NAUTILUS_ARMOR);

					// Minerais de Nadirheim (noms provisoires), du moins profond au plus profond
					output.accept(ModBlocks.MINERAL2_ORE);
					output.accept(ModItems.MINERAL2);
					output.accept(ModItems.MINERAL2_SHOVEL);
					output.accept(ModItems.MINERAL2_PICKAXE);
					output.accept(ModItems.MINERAL2_AXE);
					output.accept(ModItems.MINERAL2_SWORD);
					output.accept(ModBlocks.MINERAL1_ORE);
					output.accept(ModItems.MINERAL1);
					output.accept(ModItems.MINERAL1_SHOVEL);
					output.accept(ModItems.MINERAL1_PICKAXE);
					output.accept(ModItems.MINERAL1_AXE);
					output.accept(ModItems.MINERAL1_SWORD);

					// Pierre glacée
					output.accept(ModBlocks.ICY_STONE);
					output.accept(ModBlocks.ICY_STONE_STAIRS);
					output.accept(ModBlocks.ICY_STONE_SLAB);
					output.accept(ModBlocks.ICY_STONE_WALL);
					output.accept(ModBlocks.COBBLED_ICY_STONE);
					output.accept(ModBlocks.COBBLED_ICY_STONE_STAIRS);
					output.accept(ModBlocks.COBBLED_ICY_STONE_SLAB);
					output.accept(ModBlocks.COBBLED_ICY_STONE_WALL);
					output.accept(ModBlocks.CUT_ICY_STONE);
					output.accept(ModBlocks.CUT_ICY_STONE_STAIRS);
					output.accept(ModBlocks.CUT_ICY_STONE_SLAB);
					output.accept(ModBlocks.CUT_ICY_STONE_WALL);

					// Arbres de Nadirheim (noms provisoires)
					output.accept(ModBlocks.TREE1_LOG);
					output.accept(ModBlocks.TREE1_WOOD);
					output.accept(ModBlocks.STRIPPED_TREE1_LOG);
					output.accept(ModBlocks.STRIPPED_TREE1_WOOD);
					output.accept(ModBlocks.TREE1_LEAVES);
					output.accept(ModBlocks.TREE1_SAPLING);
					output.accept(ModBlocks.TREE1_PLANKS);
					output.accept(ModBlocks.TREE1_STAIRS);
					output.accept(ModBlocks.TREE1_SLAB);
					output.accept(ModBlocks.TREE1_FENCE);
					output.accept(ModBlocks.TREE1_FENCE_GATE);
					output.accept(ModBlocks.TREE1_DOOR_ITEM);
					output.accept(ModBlocks.TREE1_TRAPDOOR);
					output.accept(ModBlocks.TREE1_PRESSURE_PLATE);
					output.accept(ModBlocks.TREE1_BUTTON);
					output.accept(ModBlocks.TREE2_LOG);
					output.accept(ModBlocks.TREE2_WOOD);
					output.accept(ModBlocks.STRIPPED_TREE2_LOG);
					output.accept(ModBlocks.STRIPPED_TREE2_WOOD);
					output.accept(ModBlocks.TREE2_LEAVES);
					output.accept(ModBlocks.TREE2_SAPLING);
					output.accept(ModBlocks.TREE2_PLANKS);
					output.accept(ModBlocks.TREE2_STAIRS);
					output.accept(ModBlocks.TREE2_SLAB);
					output.accept(ModBlocks.TREE2_FENCE);
					output.accept(ModBlocks.TREE2_FENCE_GATE);
					output.accept(ModBlocks.TREE2_DOOR_ITEM);
					output.accept(ModBlocks.TREE2_TRAPDOOR);
					output.accept(ModBlocks.TREE2_PRESSURE_PLATE);
					output.accept(ModBlocks.TREE2_BUTTON);

					// Végétation
					output.accept(ModBlocks.ICE_MUSHROOM);

					// Nourriture
					output.accept(ModItems.OLD_BERRY);
					output.accept(ModItems.XP_COIN);

					// Salle du portail
					output.accept(ModBlocks.PILLAR_CAPITAL);
					output.accept(ModBlocks.PILLAR_SHAFT_UPPER);
					output.accept(ModBlocks.PILLAR_SHAFT_LOWER);
					output.accept(ModBlocks.PILLAR_BASE);
					output.accept(ModBlocks.TEMPLE_TILES);
					output.accept(ModBlocks.NADIRHEIM_PORTAL);   // bloc de test de l'effet du portail
					output.accept(ModItems.INNER_STAR);

					// Blocs utilitaires
					output.accept(ModBlocks.SIEVE);

					// Alchimie
					output.accept(ModItems.VIOLINITE_OXIDE);
					output.accept(ModItems.VIOLINITE_OXIDE_POWDER);
					output.accept(ModItems.ADRENALINE);
					output.accept(ModItems.ADRENALINE_CRYSTALS);
					output.accept(ModItems.ADRENALINE_POWDER);
					output.accept(ModItems.BLOOD);
					output.accept(ModItems.ADRENOCHROME);
					output.accept(ModItems.ADRENOCHROME_BUCKET);

					// Divers
					output.accept(ModItems.STONE_STICK);
					output.accept(ModBlocks.UNDERWATER_TORCH_ITEM);
				})
				.build());
	}
}
