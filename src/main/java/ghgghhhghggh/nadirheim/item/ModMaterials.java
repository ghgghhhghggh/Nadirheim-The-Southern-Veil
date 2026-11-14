package ghgghhhghggh.nadirheim.item;

import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Map;

// Matériaux violinite : mêmes valeurs que la netherite, mais réparables avec de la violinite
public class ModMaterials {
	// Tags des items qui réparent les outils / armures en violinite (remplis par la datagen : contiennent la violinite)
	public static final TagKey<Item> VIOLINITE_TOOL_MATERIALS =
			TagKey.create(Registries.ITEM, Nadirheim.id("violinite_tool_materials"));
	public static final TagKey<Item> REPAIRS_VIOLINITE_ARMOR =
			TagKey.create(Registries.ITEM, Nadirheim.id("repairs_violinite_armor"));

	// Outils : 2031 de durabilité, vitesse de minage 9, +4 de dégâts, enchantabilité 15
	public static final ToolMaterial VIOLINITE_TOOL = new ToolMaterial(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, VIOLINITE_TOOL_MATERIALS);

	// Minerais de Nadirheim (NOMS PROVISOIRES "minéral 1 / 2"). Progression : les outils diamant / violinite minent
	// le minerai 2 ; seuls les outils en minéral 2 (ou 1) minent le minerai 1, le plus profond. Valeurs PROVISOIRES,
	// un cran au-dessus de la netherite (2031 / 9 / +4 / 15) à chaque étage. Dégâts décidés par l'utilisateur le
	// 2026-10-09 : minéral 2 = +5, minéral 1 = +6 (épée 9 et 10). Les outils en minéral 1 donnent aussi Célérité en
	// main (voir Mineral1Haste).
	public static final TagKey<Item> MINERAL2_TOOL_MATERIALS = TagKey.create(Registries.ITEM, Nadirheim.id("mineral2_tool_materials"));
	public static final TagKey<Item> MINERAL1_TOOL_MATERIALS = TagKey.create(Registries.ITEM, Nadirheim.id("mineral1_tool_materials"));
	// Blocs que ces outils ne peuvent PAS miner (vides : ils minent tout)
	public static final TagKey<Block> INCORRECT_FOR_MINERAL2_TOOL = TagKey.create(Registries.BLOCK, Nadirheim.id("incorrect_for_mineral2_tool"));
	public static final TagKey<Block> INCORRECT_FOR_MINERAL1_TOOL = TagKey.create(Registries.BLOCK, Nadirheim.id("incorrect_for_mineral1_tool"));
	public static final ToolMaterial MINERAL2_TOOL = new ToolMaterial(INCORRECT_FOR_MINERAL2_TOOL, 2500, 10.0F, 5.0F, 17, MINERAL2_TOOL_MATERIALS);
	public static final ToolMaterial MINERAL1_TOOL = new ToolMaterial(INCORRECT_FOR_MINERAL1_TOOL, 3200, 12.0F, 6.0F, 20, MINERAL1_TOOL_MATERIALS);

	// Apparence de l'armure portée : assets/nadirheim/equipment/violinite.json (généré par la datagen),
	// qui pointe vers textures/entity/equipment/humanoid/violinite.png et humanoid_leggings/violinite.png
	public static final ResourceKey<EquipmentAsset> VIOLINITE_EQUIPMENT_ASSET =
			ResourceKey.create(EquipmentAssets.ROOT_ID, Nadirheim.id("violinite"));

	// Armure : protection bottes 3 / jambières 6 / plastron 8 / casque 3, robustesse 3,
	// résistance au recul 0.1, enchantabilité 15, durabilité x37
	public static final ArmorMaterial VIOLINITE_ARMOR = new ArmorMaterial(
			37, ArmorMaterials.makeDefense(3, 6, 8, 3, 19), 15, SoundEvents.ARMOR_EQUIP_NETHERITE,
			3.0F, 0.1F, REPAIRS_VIOLINITE_ARMOR, VIOLINITE_EQUIPMENT_ASSET);

	// Matériau d'ornement (trim) : la violinite colore les ornements en violet, sur n'importe quelle armure.
	// "violinite" -> palette textures/trims/color_palettes/violinite.png ;
	// sur l'armure en violinite elle-même on utilise la version plus foncée (violinite_darker.png),
	// comme le diamant sur l'armure en diamant, sinon l'ornement ne se verrait pas.
	public static final ResourceKey<TrimMaterial> VIOLINITE_TRIM =
			ResourceKey.create(Registries.TRIM_MATERIAL, Nadirheim.id("violinite"));
	public static final MaterialAssetGroup VIOLINITE_TRIM_ASSETS =
			MaterialAssetGroup.create("violinite", Map.of(VIOLINITE_EQUIPMENT_ASSET, "violinite_darker"));

	// Les matériaux d'ornement sont des données (data/nadirheim/trim_material/violinite.json), générées par la datagen
	public static void bootstrapTrimMaterials(BootstrapContext<TrimMaterial> context) {
		Component description = Component.translatable(Util.makeDescriptionId("trim_material", VIOLINITE_TRIM.identifier()))
				.withStyle(Style.EMPTY.withColor(0x8C81D9));
		context.register(VIOLINITE_TRIM, new TrimMaterial(VIOLINITE_TRIM_ASSETS, description));
	}
}
