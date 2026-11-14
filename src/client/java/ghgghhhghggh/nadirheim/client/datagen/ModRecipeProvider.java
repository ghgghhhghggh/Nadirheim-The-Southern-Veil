package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				// 9 violinites -> 1 bloc de violinite, et 1 bloc -> 9 violinites
				nineBlockStorageRecipes(RecipeCategory.MISC, ModItems.VIOLINITE, RecipeCategory.BUILDING_BLOCKS, ModBlocks.VIOLINITE_BLOCK);

				// Cuire un minerai récupéré avec Toucher de soie donne une violinite (four et haut fourneau)
				List<ItemLike> ores = List.of(ModBlocks.VIOLINITE_ORE, ModBlocks.DEEPSLATE_VIOLINITE_ORE, ModBlocks.ICY_VIOLINITE_ORE);
				oreSmelting(ores, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.VIOLINITE, 1.0F, 200, "violinite");
				oreBlasting(ores, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.VIOLINITE, 1.0F, 100, "violinite");

				// Pierre glacée : escaliers, dalles, murets à l'établi et au tailleur de pierre (voir ModBlockFamilies)
				for (BlockFamily family : ModBlockFamilies.ALL) {
					generateRecipes(family, FeatureFlags.VANILLA_SET);
				}
				// Arbres : 1 bûche (ou bois) -> 4 planches ; 4 bûches -> 3 bois
				planksFromLog(ModBlocks.TREE1_PLANKS, ModBlocks.TREE1_LOGS, 4);
				planksFromLog(ModBlocks.TREE2_PLANKS, ModBlocks.TREE2_LOGS, 4);
				woodFromLogs(ModBlocks.TREE1_WOOD, ModBlocks.TREE1_LOG);
				woodFromLogs(ModBlocks.TREE2_WOOD, ModBlocks.TREE2_LOG);
				woodFromLogs(ModBlocks.STRIPPED_TREE1_WOOD, ModBlocks.STRIPPED_TREE1_LOG);
				woodFromLogs(ModBlocks.STRIPPED_TREE2_WOOD, ModBlocks.STRIPPED_TREE2_LOG);
				// Bateaux et pancartes propres aux arbres : pas encore (prévus pour une mise à jour). En attendant, leurs
				// planches donnent les versions en chêne (mêmes recettes que le vanilla ; le bateau à coffre se fait
				// ensuite avec la recette vanilla)
				Ingredient nadirheimPlanks = Ingredient.of(ModBlocks.TREE1_PLANKS, ModBlocks.TREE2_PLANKS);
				shaped(RecipeCategory.TRANSPORTATION, Items.OAK_BOAT)
						.group("boat")
						.pattern("# #")
						.pattern("###")
						.define('#', nadirheimPlanks)
						.unlockedBy("in_water", insideOf(Blocks.WATER))
						.save(output, "nadirheim:oak_boat_from_nadirheim_planks");
				signBuilder(Items.OAK_SIGN, nadirheimPlanks)
						.unlockedBy("has_tree1_planks", has(ModBlocks.TREE1_PLANKS))
						.unlockedBy("has_tree2_planks", has(ModBlocks.TREE2_PLANKS))
						.save(output, "nadirheim:oak_sign_from_nadirheim_planks");
				// Pierre glacée brute cuite au four -> pierre glacée (comme roche -> pierre)
				smeltingResultFromBase(ModBlocks.ICY_STONE, ModBlocks.COBBLED_ICY_STONE);
				// Pierre glacée taillée : 4 pierres glacées en carré -> 4, ou au tailleur de pierre 1 -> 1
				shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_ICY_STONE, 4)
						.pattern("##")
						.pattern("##")
						.define('#', ModBlocks.ICY_STONE)
						.unlockedBy("has_icy_stone", has(ModBlocks.ICY_STONE))
						.save(output);
				stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_ICY_STONE, ModBlocks.ICY_STONE);

				// Bâton de pierre : 2 blocs de pierre ou de roche (ou leurs variantes glacées) l'un sur l'autre -> 4
				shaped(RecipeCategory.MISC, ModItems.STONE_STICK, 4)
						.pattern("#")
						.pattern("#")
						.define('#', Ingredient.of(Items.STONE, Items.COBBLESTONE, ModBlocks.ICY_STONE, ModBlocks.COBBLED_ICY_STONE))
						.unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
						.save(output);

				// Torche sous-marine : pierre lumineuse sur un bâton de pierre -> 4 (comme les torches vanilla)
				shaped(RecipeCategory.DECORATIONS, ModBlocks.UNDERWATER_TORCH_ITEM, 4)
						.pattern("G")
						.pattern("S")
						.define('G', Items.GLOWSTONE)
						.define('S', ModItems.STONE_STICK)
						.unlockedBy("has_stone_stick", has(ModItems.STONE_STICK))
						.save(output);

				// Charge des profondeurs -> 3 (les seaux vides sont rendus, comme pour le gâteau)
				//   T T T      T = TNT
				//   E V L      E = seau d'eau, V = violinite, L = seau de lave
				//   B B B      B = bâton de pierre
				shaped(RecipeCategory.TOOLS, ModItems.DEEP_CHARGE, 3)
						.pattern("TTT")
						.pattern("EVL")
						.pattern("BBB")
						.define('T', Items.TNT)
						.define('E', Items.WATER_BUCKET)
						.define('V', ModItems.VIOLINITE)
						.define('L', Items.LAVA_BUCKET)
						.define('B', ModItems.STONE_STICK)
						.unlockedBy("has_violinite", has(ModItems.VIOLINITE))
						.save(output);

				// Tamis : cadre en bois, maille en ficelle renforcée de fer, base en roche
				//   B F B      B = bâton, F = ficelle
				//   F L F      L = lingot de fer
				//   P R P      P = planches (toutes), R = roche (ou roche des abîmes, pierre glacée brute...)
				shaped(RecipeCategory.DECORATIONS, ModBlocks.SIEVE)
						.pattern("SFS")
						.pattern("FIF")
						.pattern("PCP")
						.define('S', Items.STICK)
						.define('F', Items.STRING)
						.define('I', Items.IRON_INGOT)
						.define('P', ItemTags.PLANKS)
						.define('C', ItemTags.STONE_CRAFTING_MATERIALS)
						.unlockedBy("has_gravel", has(Items.GRAVEL))
						.save(output);

				// Duplication du modèle, comme pour la netherite : 7 diamants + 1 modèle + 1 deepslate -> 2 modèles
				//   D M D
				//   D S D     (S = deepslate ou deepslate brute, celle qu'on obtient en la minant sans Toucher de soie)
				//   D D D
				copySmithingTemplate(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE, Ingredient.of(Items.DEEPSLATE, Items.COBBLED_DEEPSLATE));

				// Les objets en violinite ne se fabriquent pas : on améliore un objet en diamant à la table de forge
				violiniteSmithing(Items.DIAMOND_SWORD, RecipeCategory.COMBAT, ModItems.VIOLINITE_SWORD);
				violiniteSmithing(Items.DIAMOND_SPEAR, RecipeCategory.COMBAT, ModItems.VIOLINITE_SPEAR);
				violiniteSmithing(Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, ModItems.VIOLINITE_SHOVEL);
				violiniteSmithing(Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, ModItems.VIOLINITE_PICKAXE);
				violiniteSmithing(Items.DIAMOND_AXE, RecipeCategory.TOOLS, ModItems.VIOLINITE_AXE);
				violiniteSmithing(Items.DIAMOND_HOE, RecipeCategory.TOOLS, ModItems.VIOLINITE_HOE);
				violiniteSmithing(Items.DIAMOND_HELMET, RecipeCategory.COMBAT, ModItems.VIOLINITE_HELMET);
				violiniteSmithing(Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, ModItems.VIOLINITE_CHESTPLATE);
				violiniteSmithing(Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, ModItems.VIOLINITE_LEGGINGS);
				violiniteSmithing(Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, ModItems.VIOLINITE_BOOTS);
				violiniteSmithing(Items.DIAMOND_HORSE_ARMOR, RecipeCategory.COMBAT, ModItems.VIOLINITE_HORSE_ARMOR);
				violiniteSmithing(Items.DIAMOND_NAUTILUS_ARMOR, RecipeCategory.COMBAT, ModItems.VIOLINITE_NAUTILUS_ARMOR);

				// Minerais de Nadirheim (décision du 2026-10-09, PROVISOIRE en attendant l'athanor) : même modèle de forge,
				// objet en violinite + minéral 2 -> objet en minéral 2, puis objet en minéral 2 + minéral 1 -> minéral 1.
				// Pas de recette d'établi.
				upgradeSmithing(ModItems.VIOLINITE_SWORD, ModItems.MINERAL2, RecipeCategory.COMBAT, ModItems.MINERAL2_SWORD);
				upgradeSmithing(ModItems.VIOLINITE_SHOVEL, ModItems.MINERAL2, RecipeCategory.TOOLS, ModItems.MINERAL2_SHOVEL);
				upgradeSmithing(ModItems.VIOLINITE_PICKAXE, ModItems.MINERAL2, RecipeCategory.TOOLS, ModItems.MINERAL2_PICKAXE);
				upgradeSmithing(ModItems.VIOLINITE_AXE, ModItems.MINERAL2, RecipeCategory.TOOLS, ModItems.MINERAL2_AXE);
				upgradeSmithing(ModItems.MINERAL2_SWORD, ModItems.MINERAL1, RecipeCategory.COMBAT, ModItems.MINERAL1_SWORD);
				upgradeSmithing(ModItems.MINERAL2_SHOVEL, ModItems.MINERAL1, RecipeCategory.TOOLS, ModItems.MINERAL1_SHOVEL);
				upgradeSmithing(ModItems.MINERAL2_PICKAXE, ModItems.MINERAL1, RecipeCategory.TOOLS, ModItems.MINERAL1_PICKAXE);
				upgradeSmithing(ModItems.MINERAL2_AXE, ModItems.MINERAL1, RecipeCategory.TOOLS, ModItems.MINERAL1_AXE);
			}

			// Table de forge : modèle de forge + objet en diamant + violinite -> objet en violinite.
			// Comme pour la netherite, l'objet garde ses enchantements et sa durabilité.
			private void violiniteSmithing(Item base, RecipeCategory category, Item result) {
				upgradeSmithing(base, ModItems.VIOLINITE, category, result);
			}

			// Même modèle de forge pour tous les paliers ; l'objet garde ses enchantements et sa durabilité
			private void upgradeSmithing(Item base, Item addition, RecipeCategory category, Item result) {
				SmithingTransformRecipeBuilder.smithing(
								Ingredient.of(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE),
								Ingredient.of(base),
								Ingredient.of(addition),
								category, result)
						.unlocks("has_" + getItemName(addition), has(addition))
						.save(output, getItemName(result) + "_smithing");
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
