package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.client.NadirheimClient;
import ghgghhhghggh.nadirheim.client.color.XpOrbTint;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_BOOTS;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_CHESTPLATE;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_HELMET;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_LEGGINGS;

public class ModModelProvider extends FabricModelProvider {
	// Patron "sphère en pixels" : la forme est écrite à la main dans assets/nadirheim/models/item/template_sphere.json
	// (la datagen ne sait pas générer des formes), la datagen n'y branche que la texture (#all)
	private static final ModelTemplate SPHERE = new ModelTemplate(Optional.of(Nadirheim.id("item/template_sphere")), Optional.empty(), TextureSlot.ALL);
	// Patron "petit objet" : objet plat comme item/generated, mais affiché à ~60 % en main, au sol et dans les cadres
	// (échelles écrites à la main dans assets/nadirheim/models/item/template_small_item.json ; l'inventaire ne change pas)
	private static final ModelTemplate SMALL_ITEM = new ModelTemplate(Optional.of(Nadirheim.id("item/template_small_item")), Optional.empty(), TextureSlot.LAYER0);
	// Patron "tamis" : forme de l'échafaudage vanilla (4 pieds + rebord + grille sur le dessus), écrite à la main
	// dans assets/nadirheim/models/block/template_sieve.json
	private static final ModelTemplate SIEVE = new ModelTemplate(Optional.of(Nadirheim.id("block/template_sieve")), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);

	// Matériaux d'ornement vanilla + la violinite (la liste vanilla TRIM_MATERIAL_MODELS n'est pas modifiable)
	private static final List<ItemModelGenerators.TrimMaterialData> TRIM_MATERIALS = Stream.concat(
			ItemModelGenerators.TRIM_MATERIAL_MODELS.stream(),
			Stream.of(new ItemModelGenerators.TrimMaterialData(ModMaterials.VIOLINITE_TRIM_ASSETS, ModMaterials.VIOLINITE_TRIM))
	).toList();

	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
		// Cube simple avec la même texture sur les 6 faces (textures/block/<nom>.png)
		blockModelGenerators.createTrivialCube(ModBlocks.VIOLINITE_ORE);
		blockModelGenerators.createTrivialCube(ModBlocks.DEEPSLATE_VIOLINITE_ORE);
		blockModelGenerators.createTrivialCube(ModBlocks.ICY_VIOLINITE_ORE);
		blockModelGenerators.createTrivialCube(ModBlocks.VIOLINITE_BLOCK);
		blockModelGenerators.createTrivialCube(ModBlocks.MINERAL2_ORE);
		blockModelGenerators.createTrivialCube(ModBlocks.MINERAL1_ORE);

		// Piliers : même texture sur toutes les faces, sauf le chapiteau (côtés et dessous = pillar_capital,
		// dessus = pillar_capital_top)
		blockModelGenerators.createTrivialCube(ModBlocks.PILLAR_SHAFT_UPPER);
		blockModelGenerators.createTrivialCube(ModBlocks.PILLAR_SHAFT_LOWER);
		blockModelGenerators.createTrivialCube(ModBlocks.PILLAR_BASE);
		blockModelGenerators.createTrivialCube(ModBlocks.TEMPLE_TILES);
		Material capitalSide = TextureMapping.getBlockTexture(ModBlocks.PILLAR_CAPITAL);
		Identifier capitalModel = ModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.PILLAR_CAPITAL, new TextureMapping()
				.put(TextureSlot.SIDE, capitalSide)
				.put(TextureSlot.BOTTOM, capitalSide)
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.PILLAR_CAPITAL, "_top")), blockModelGenerators.modelOutput);
		blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.PILLAR_CAPITAL,
				BlockModelGenerators.plainVariant(capitalModel)));

		// Arbres : bûche (textures/block/<bûche>.png = écorce, <bûche>_top.png = cernes), bois (écorce partout),
		// feuilles (modèle vanilla des feuilles, sans teinte de biome : la couleur est dans la texture)
		blockModelGenerators.woodProvider(ModBlocks.TREE1_LOG).logWithHorizontal(ModBlocks.TREE1_LOG).wood(ModBlocks.TREE1_WOOD);
		blockModelGenerators.woodProvider(ModBlocks.TREE2_LOG).logWithHorizontal(ModBlocks.TREE2_LOG).wood(ModBlocks.TREE2_WOOD);
		blockModelGenerators.woodProvider(ModBlocks.STRIPPED_TREE1_LOG).logWithHorizontal(ModBlocks.STRIPPED_TREE1_LOG)
				.wood(ModBlocks.STRIPPED_TREE1_WOOD);
		blockModelGenerators.woodProvider(ModBlocks.STRIPPED_TREE2_LOG).logWithHorizontal(ModBlocks.STRIPPED_TREE2_LOG)
				.wood(ModBlocks.STRIPPED_TREE2_WOOD);
		blockModelGenerators.createTrivialBlock(ModBlocks.TREE1_LEAVES, TexturedModel.LEAVES);
		blockModelGenerators.createTrivialBlock(ModBlocks.TREE2_LEAVES, TexturedModel.LEAVES);
		// Pousses : plante en croix + version en pot, objet plat avec la texture de la pousse
		blockModelGenerators.createPlantWithDefaultItem(ModBlocks.TREE1_SAPLING, ModBlocks.POTTED_TREE1_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
		blockModelGenerators.createPlantWithDefaultItem(ModBlocks.TREE2_SAPLING, ModBlocks.POTTED_TREE2_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

		// Adrénochrome posé : bloc invisible (le liquide est dessiné par le moteur des liquides), modèle pour les particules
		blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.ADRENOCHROME,
				BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create(ModBlocks.ADRENOCHROME,
						TextureMapping.particle(new Material(Identifier.withDefaultNamespace("block/water_still"))), blockModelGenerators.modelOutput))));

		// Portail de Nadirheim (test) : le bloc est invisible (dessiné par son renderer), son modèle ne sert qu'aux
		// particules ; l'objet est un cube avec la petite icône textures/block/nadirheim_portal.png
		blockModelGenerators.createParticleOnlyBlock(ModBlocks.NADIRHEIM_PORTAL);
		blockModelGenerators.registerSimpleItemModel(ModBlocks.NADIRHEIM_PORTAL, ModelTemplates.CUBE_ALL.create(
				ModelLocationUtils.getModelLocation(ModBlocks.NADIRHEIM_PORTAL.asItem()),
				TextureMapping.cube(ModBlocks.NADIRHEIM_PORTAL), blockModelGenerators.modelOutput));

		// Tas de pièces d'xp : modèles de la litière de feuilles vanilla (une pièce par quart de bloc, texture 16x16
		// textures/block/xp_coin_pile.png avec 4 pièces vues de dessus : 1 = coin nord-ouest, 2 = sud-ouest, 3 = sud-est,
		// 4 = nord-est), tournés selon la direction du joueur au moment de la pose
		blockModelGenerators.createSegmentedBlock(ModBlocks.XP_COIN_PILE,
				BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_1.create(ModBlocks.XP_COIN_PILE, blockModelGenerators.modelOutput)),
				condition -> condition.term(BlockStateProperties.SEGMENT_AMOUNT, 1),
				BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_2.create(ModBlocks.XP_COIN_PILE, blockModelGenerators.modelOutput)),
				condition -> condition.term(BlockStateProperties.SEGMENT_AMOUNT, 2, 3),
				BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_3.create(ModBlocks.XP_COIN_PILE, blockModelGenerators.modelOutput)),
				condition -> condition.term(BlockStateProperties.SEGMENT_AMOUNT, 3),
				BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_4.create(ModBlocks.XP_COIN_PILE, blockModelGenerators.modelOutput)),
				condition -> condition.term(BlockStateProperties.SEGMENT_AMOUNT, 4));

		// Tamis : grille (sieve_top, visible aussi par-dessous) sur un cadre ouvert (sieve_side)
		Identifier sieveModel = SIEVE.create(ModBlocks.SIEVE, new TextureMapping()
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.SIEVE, "_top"))
				.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(ModBlocks.SIEVE, "_side")), blockModelGenerators.modelOutput);
		blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.SIEVE,
				BlockModelGenerators.plainVariant(sieveModel)));

		// Torche : modèles au sol et au mur, item plat, tous avec textures/block/underwater_torch.png
		blockModelGenerators.createNormalTorch(ModBlocks.UNDERWATER_TORCH, ModBlocks.UNDERWATER_WALL_TORCH);

		// Pierre glacée, brute et taillée : bloc + escaliers + dalle + muret (voir ModBlockFamilies)
		for (BlockFamily family : ModBlockFamilies.ALL) {
			blockModelGenerators.family(family.getBaseBlock()).generateFor(family);
		}

		// Plante en croix (deux plans croisés, comme les champignons et les fleurs vanilla) ;
		// l'item utilise la même texture (textures/block/ice_mushroom.png)
		blockModelGenerators.createCrossBlockWithDefaultItem(ModBlocks.ICE_MUSHROOM, BlockModelGenerators.PlantType.NOT_TINTED);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerators) {
		// FLAT_ITEM : item plat classique (textures/item/violinite.png)
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_OXIDE, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_OXIDE_POWDER, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.ADRENALINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.ADRENALINE_POWDER, ModelTemplates.FLAT_ITEM);
		// Fioles : calque 0 = le liquide du mod (textures/item/<nom>.png, semi-transparent), calque 1 = la fiole de
		// potion VANILLA (minecraft:item/potion), lue dans les fichiers du jeu sans être recopiée dans le mod
		// Fiole d'adrénochrome : comme les vraies potions, le liquide gris vanilla (potion_overlay) teinté par le code
		itemModelGenerators.itemModelOutput.accept(ModItems.ADRENOCHROME, ItemModelUtils.tintedModel(ModelTemplates.TWO_LAYERED_ITEM.create(
				ModItems.ADRENOCHROME, TextureMapping.layered(new Material(Identifier.withDefaultNamespace("item/potion_overlay")),
						new Material(Identifier.withDefaultNamespace("item/potion"))), itemModelGenerators.modelOutput),
				ItemModelUtils.constantTint(NadirheimClient.ADRENOCHROME_COLOR)));
		// Seau d'adrénochrome : seau vide VANILLA dessous, liquide du mod (textures/item/adrenochrome_bucket.png) par-dessus
		itemModelGenerators.itemModelOutput.accept(ModItems.ADRENOCHROME_BUCKET, ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
				ModItems.ADRENOCHROME_BUCKET, TextureMapping.layered(new Material(Identifier.withDefaultNamespace("item/bucket")),
						TextureMapping.getItemTexture(ModItems.ADRENOCHROME_BUCKET)), itemModelGenerators.modelOutput)));
		for (Item vial : List.of(ModItems.ADRENALINE, ModItems.BLOOD)) {
			itemModelGenerators.itemModelOutput.accept(vial, ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(vial,
					TextureMapping.layered(TextureMapping.getItemTexture(vial), new Material(Identifier.withDefaultNamespace("item/potion"))),
					itemModelGenerators.modelOutput)));
		}
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.OLD_BERRY, ModelTemplates.FLAT_ITEM);
		// Pièce d'xp : texture grise teintée vert <-> jaune comme les orbes, affichée en petit (en main, au sol, cadre)
		itemModelGenerators.itemModelOutput.accept(ModItems.XP_COIN, ItemModelUtils.tintedModel(
				SMALL_ITEM.create(ModItems.XP_COIN, TextureMapping.layer0(ModItems.XP_COIN), itemModelGenerators.modelOutput),
				new XpOrbTint()));
		itemModelGenerators.generateFlatItem(ModItems.STONE_STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.DEEP_CHARGE, ModelTemplates.FLAT_ITEM);

		// Inner Star : une planète en 3D (sphère en pixels), dans l'inventaire comme au-dessus des piliers
		Identifier innerStarModel = SPHERE.create(ModelLocationUtils.getModelLocation(ModItems.INNER_STAR),
				TextureMapping.cube(TextureMapping.getItemTexture(ModItems.INNER_STAR)), itemModelGenerators.modelOutput);
		itemModelGenerators.itemModelOutput.accept(ModItems.INNER_STAR, ItemModelUtils.plainModel(innerStarModel));

		// FLAT_HANDHELD_ITEM : texture plate tenue en main comme un outil (textures/item/<nom>.png)
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
		for (Item mineral : List.of(ModItems.MINERAL2, ModItems.MINERAL1)) {
			itemModelGenerators.generateFlatItem(mineral, ModelTemplates.FLAT_ITEM);
		}
		for (Item tool : List.of(ModItems.MINERAL2_SWORD, ModItems.MINERAL2_SHOVEL, ModItems.MINERAL2_PICKAXE, ModItems.MINERAL2_AXE,
				ModItems.MINERAL1_SWORD, ModItems.MINERAL1_SHOVEL, ModItems.MINERAL1_PICKAXE, ModItems.MINERAL1_AXE)) {
			itemModelGenerators.generateFlatItem(tool, ModelTemplates.FLAT_HANDHELD_ITEM);
		}

		// Lance : petite texture dans l'inventaire (violinite_spear.png),
		// grande texture 32x32 quand on la tient en main (violinite_spear_in_hand.png)
		itemModelGenerators.generateSpear(ModItems.VIOLINITE_SPEAR);

		// Armures pour animaux : icône plate classique
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ModItems.VIOLINITE_NAUTILUS_ARMOR, ModelTemplates.FLAT_ITEM);

		// Armures : modèle d'item qui affiche l'ornement (trim) appliqué, y compris un ornement en violinite
		generateTrimmableArmorSet(itemModelGenerators, ModMaterials.VIOLINITE_EQUIPMENT_ASSET, false,
				ModItems.VIOLINITE_HELMET, ModItems.VIOLINITE_CHESTPLATE, ModItems.VIOLINITE_LEGGINGS, ModItems.VIOLINITE_BOOTS);

		// Armures vanilla : on régénère leurs modèles pour qu'elles affichent aussi un ornement en violinite
		// dans l'inventaire (mêmes appels que ItemModelGenerators.run, avec la violinite en plus)
		generateTrimmableItem(itemModelGenerators, Items.TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE, TRIM_PREFIX_HELMET, false);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.LEATHER, true,
				Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.COPPER, false,
				Items.COPPER_HELMET, Items.COPPER_CHESTPLATE, Items.COPPER_LEGGINGS, Items.COPPER_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.CHAINMAIL, false,
				Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.IRON, false,
				Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.DIAMOND, false,
				Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.GOLD, false,
				Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);
		generateTrimmableArmorSet(itemModelGenerators, EquipmentAssets.NETHERITE, false,
				Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);
	}

	private static void generateTrimmableArmorSet(ItemModelGenerators generators, ResourceKey<EquipmentAsset> equipmentAsset,
			boolean dyed, Item helmet, Item chestplate, Item leggings, Item boots) {
		generateTrimmableItem(generators, helmet, equipmentAsset, TRIM_PREFIX_HELMET, dyed);
		generateTrimmableItem(generators, chestplate, equipmentAsset, TRIM_PREFIX_CHESTPLATE, dyed);
		generateTrimmableItem(generators, leggings, equipmentAsset, TRIM_PREFIX_LEGGINGS, dyed);
		generateTrimmableItem(generators, boots, equipmentAsset, TRIM_PREFIX_BOOTS, dyed);
	}

	// Copie de ItemModelGenerators.generateTrimmableItem, qui utilise notre liste TRIM_MATERIALS
	// au lieu de la liste vanilla. "dyed" = armure en cuir (couche teintable en plus)
	private static void generateTrimmableItem(ItemModelGenerators generators, Item armor,
			ResourceKey<EquipmentAsset> equipmentAsset, Identifier slotTrimPrefix, boolean dyed) {
		Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
		Material itemTexture = TextureMapping.getItemTexture(armor);
		Material overlayTexture = TextureMapping.getItemTexture(armor, "_overlay");
		List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>(TRIM_MATERIALS.size());

		for (ItemModelGenerators.TrimMaterialData material : TRIM_MATERIALS) {
			Identifier trimModelLocation = modelLocation.withSuffix("_" + material.assets().base().suffix() + "_trim");
			Material trimOverlayTexture = new Material(slotTrimPrefix.withSuffix("_" + material.assets().assetId(equipmentAsset).suffix()));
			ItemModel.Unbaked trimModel;
			if (dyed) {
				generators.generateLayeredItem(trimModelLocation, itemTexture, overlayTexture, trimOverlayTexture);
				trimModel = ItemModelUtils.tintedModel(trimModelLocation, new Dye(-6265536));
			} else {
				generators.generateLayeredItem(trimModelLocation, itemTexture, trimOverlayTexture);
				trimModel = ItemModelUtils.plainModel(trimModelLocation);
			}
			cases.add(ItemModelUtils.when(material.materialKey(), trimModel));
		}

		ItemModel.Unbaked untrimmedModel;
		if (dyed) {
			ModelTemplates.TWO_LAYERED_ITEM.create(modelLocation, TextureMapping.layered(itemTexture, overlayTexture), generators.modelOutput);
			untrimmedModel = ItemModelUtils.tintedModel(modelLocation, new Dye(-6265536));
		} else {
			ModelTemplates.FLAT_ITEM.create(modelLocation, TextureMapping.layer0(itemTexture), generators.modelOutput);
			untrimmedModel = ItemModelUtils.plainModel(modelLocation);
		}

		generators.itemModelOutput.accept(armor, ItemModelUtils.select(new TrimMaterialProperty(), untrimmedModel, cases));
	}
}
