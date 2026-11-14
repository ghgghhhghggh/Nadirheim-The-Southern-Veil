package ghgghhhghggh.nadirheim.item;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.alchemy.Adrenochrome;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.fluid.ModFluids;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.List;
import java.util.function.Function;

import static ghgghhhghggh.nadirheim.item.ModMaterials.VIOLINITE_ARMOR;
import static ghgghhhghggh.nadirheim.item.ModMaterials.VIOLINITE_TOOL;

public class ModItems {
	// Le minerai brut, obtenu en cassant un minerai de violinite. Sert aussi de matériau d'ornement (trim)
	public static final Item VIOLINITE = register("violinite", Item::new,
			new Item.Properties().trimMaterial(ModMaterials.VIOLINITE_TRIM));

	// Inner Star (Astre intérieur) : se pose sur les chapiteaux des piliers de la salle du portail.
	// TODO : recette (objets du mod), à définir avec l'utilisateur
	public static final Item INNER_STAR = register("inner_star", Item::new,
			new Item.Properties().stacksTo(16).rarity(Rarity.RARE));

	// Bâton de pierre : sert à fabriquer la torche sous-marine
	public static final Item STONE_STICK = register("stone_stick", Item::new, new Item.Properties());

	// Alchimie (voir le package alchemy) :
	// - oxyde de violinite : catalyseur, obtenu en "grattant" du cuivre totalement oxydé avec de la violinite ;
	// - fioles d'adrénaline (lâchée par un nitwit enfermé) et de sang (source à décider) : la fiole vide est rendue
	//   quand elles servent dans une recette. Image = fiole vanilla + liquide du mod par-dessus (voir ModModelProvider)
	public static final Item VIOLINITE_OXIDE = register("violinite_oxide", Item::new, new Item.Properties());
	// Poudres (sorties du broyeur, encore à créer) : catalyseur en poudre (sert 2 fois dans la cuve de mélange) et
	// adrénaline cristallisée (cristallisée à l'athanor puis broyée)
	public static final Item VIOLINITE_OXIDE_POWDER = register("violinite_oxide_powder", Item::new, new Item.Properties());
	public static final Item ADRENALINE_CRYSTALS = register("adrenaline_crystals", Item::new, new Item.Properties());
	public static final Item ADRENALINE_POWDER = register("adrenaline_powder", Item::new, new Item.Properties());
	public static final Item ADRENALINE = register("adrenaline", Item::new,
			new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
	public static final Item BLOOD = register("blood", Item::new,
			new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));

	// Adrénochrome : la fiole se boit (effets dans alchemy/Adrenochrome, rend la fiole vide), le seau pose le liquide
	public static final Item ADRENOCHROME = register("adrenochrome", Item::new, new Item.Properties().stacksTo(16)
			.craftRemainder(Items.GLASS_BOTTLE).usingConvertsTo(Items.GLASS_BOTTLE)
			.component(DataComponents.CONSUMABLE, Consumables.defaultDrink()
					.onConsume(new ApplyStatusEffectsConsumeEffect(Adrenochrome.effects())).build()));
	public static final Item ADRENOCHROME_BUCKET = register("adrenochrome_bucket",
			properties -> new BucketItem(ModFluids.ADRENOCHROME, properties), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

	// Baie ancienne : 1 de nourriture (une demi-cuisse), saturation = 1 x 0.4 x 2 = 0.8 (baies sucrées : 2 et 0.4).
	// En la mangeant : Vitesse II pendant 4 secondes (80 ticks ; amplificateur 1 = niveau II).
	// Plus tard, quand le buisson existera, ce sera l'item qui le plante (comme les baies sucrées).
	public static final Item OLD_BERRY = register("old_berry", Item::new,
			new Item.Properties().food(
					new FoodProperties.Builder().nutrition(1).saturationModifier(0.4F).build(),
					Consumables.defaultFood()
							.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 80, 1)))
							.build()));

	// Charge des profondeurs : clic droit à la couche -52 ou plus bas -> tunnel de 30 blocs d'explosions en chaîne
	// qui laisse les minerais à nu (voir DeepChargeItem et DeepChargeBlasts)
	public static final Item DEEP_CHARGE = register("deep_charge", DeepChargeItem::new, new Item.Properties().stacksTo(16));

	// Pièce d'xp : donne un peu d'xp au clic droit, se pose au sol accroupi (voir XpCoinItem et XpCoinPileBlock).
	// S'obtiendra seulement dans Nadirheim. useItemDescriptionPrefix : garde le nom "item.nadirheim.xp_coin"
	public static final Item XP_COIN = register("xp_coin", properties -> new XpCoinItem(ModBlocks.XP_COIN_PILE, properties),
			new Item.Properties().useItemDescriptionPrefix());

	// Modèle de forge : se trouve dans les coffres des structures souterraines (voir ModLootTableModifiers)
	// et se duplique avec 7 diamants + 1 deepslate (voir ModRecipeProvider). Il sert aux trois paliers :
	// diamant -> violinite -> minéral 2 -> minéral 1 (provisoire, en attendant l'athanor)
	public static final Item VIOLINITE_UPGRADE_SMITHING_TEMPLATE = register("violinite_upgrade_smithing_template",
			ModItems::createVioliniteUpgradeTemplate, new Item.Properties().rarity(Rarity.UNCOMMON));

	// Outils et armure : mêmes définitions que les versions en netherite (Items.NETHERITE_*),
	// y compris  : ils ne brûlent pas dans la lave
	public static final Item VIOLINITE_SWORD = register("violinite_sword", Item::new,
			new Item.Properties().sword(VIOLINITE_TOOL, 3.0F, -2.4F));
	public static final Item VIOLINITE_SPEAR = register("violinite_spear", Item::new,
			new Item.Properties().spear(VIOLINITE_TOOL, 1.15F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F));
	public static final Item VIOLINITE_SHOVEL = register("violinite_shovel",
			p -> new ShovelItem(VIOLINITE_TOOL, 1.5F, -3.0F, p), new Item.Properties());
	public static final Item VIOLINITE_PICKAXE = register("violinite_pickaxe", Item::new,
			new Item.Properties().pickaxe(VIOLINITE_TOOL, 1.0F, -2.8F));
	public static final Item VIOLINITE_AXE = register("violinite_axe",
			p -> new AxeItem(VIOLINITE_TOOL, 5.0F, -3.0F, p), new Item.Properties());
	public static final Item VIOLINITE_HOE = register("violinite_hoe",
			p -> new HoeItem(VIOLINITE_TOOL, -4.0F, 0.0F, p), new Item.Properties());

	// Minerais de Nadirheim (noms provisoires) : le matériau et ses outils (épée, pelle, pioche, hache ; pas de houe
	// ni d'armure pour l'instant). Fabriqués comme les outils en diamant (matériau + bâtons)
	public static final Item MINERAL2 = register("mineral2", Item::new, new Item.Properties());
	public static final Item MINERAL2_SWORD = register("mineral2_sword", Item::new,
			new Item.Properties().sword(ModMaterials.MINERAL2_TOOL, 3.0F, -2.4F));
	public static final Item MINERAL2_SHOVEL = register("mineral2_shovel",
			p -> new ShovelItem(ModMaterials.MINERAL2_TOOL, 1.5F, -3.0F, p), new Item.Properties());
	public static final Item MINERAL2_PICKAXE = register("mineral2_pickaxe", Item::new,
			new Item.Properties().pickaxe(ModMaterials.MINERAL2_TOOL, 1.0F, -2.8F));
	public static final Item MINERAL2_AXE = register("mineral2_axe",
			p -> new AxeItem(ModMaterials.MINERAL2_TOOL, 5.0F, -3.0F, p), new Item.Properties());
	public static final Item MINERAL1 = register("mineral1", Item::new, new Item.Properties());
	public static final Item MINERAL1_SWORD = register("mineral1_sword", Item::new,
			new Item.Properties().sword(ModMaterials.MINERAL1_TOOL, 3.0F, -2.4F));
	public static final Item MINERAL1_SHOVEL = register("mineral1_shovel",
			p -> new ShovelItem(ModMaterials.MINERAL1_TOOL, 1.5F, -3.0F, p), new Item.Properties());
	public static final Item MINERAL1_PICKAXE = register("mineral1_pickaxe", Item::new,
			new Item.Properties().pickaxe(ModMaterials.MINERAL1_TOOL, 1.0F, -2.8F));
	public static final Item MINERAL1_AXE = register("mineral1_axe",
			p -> new AxeItem(ModMaterials.MINERAL1_TOOL, 5.0F, -3.0F, p), new Item.Properties());

	public static final Item VIOLINITE_HELMET = register("violinite_helmet", Item::new,
			new Item.Properties().humanoidArmor(VIOLINITE_ARMOR, ArmorType.HELMET));
	public static final Item VIOLINITE_CHESTPLATE = register("violinite_chestplate", Item::new,
			new Item.Properties().humanoidArmor(VIOLINITE_ARMOR, ArmorType.CHESTPLATE));
	public static final Item VIOLINITE_LEGGINGS = register("violinite_leggings", Item::new,
			new Item.Properties().humanoidArmor(VIOLINITE_ARMOR, ArmorType.LEGGINGS));
	public static final Item VIOLINITE_BOOTS = register("violinite_boots", Item::new,
			new Item.Properties().humanoidArmor(VIOLINITE_ARMOR, ArmorType.BOOTS));

	// Armures pour animaux
	public static final Item VIOLINITE_HORSE_ARMOR = register("violinite_horse_armor", Item::new,
			new Item.Properties().horseArmor(VIOLINITE_ARMOR));
	public static final Item VIOLINITE_NAUTILUS_ARMOR = register("violinite_nautilus_armor", Item::new,
			new Item.Properties().nautilusArmor(VIOLINITE_ARMOR));

	// Même construction que SmithingTemplateItem.createNetheriteUpgradeTemplate : textes de l'infobulle
	// et icônes affichées en fond dans les cases vides de la table de forge
	private static SmithingTemplateItem createVioliniteUpgradeTemplate(Item.Properties properties) {
		return new SmithingTemplateItem(
				templateText("applies_to").withStyle(ChatFormatting.BLUE),
				templateText("ingredients").withStyle(ChatFormatting.BLUE),
				templateText("base_slot_description"),
				templateText("additions_slot_description"),
				List.of(slotIcon("helmet"), slotIcon("sword"), slotIcon("chestplate"), slotIcon("pickaxe"), slotIcon("leggings"),
						slotIcon("axe"), slotIcon("boots"), slotIcon("hoe"), slotIcon("shovel"), slotIcon("spear")),
				List.of(slotIcon("diamond")),
				properties);
	}

	private static MutableComponent templateText(String name) {
		return Component.translatable(Util.makeDescriptionId("item", Nadirheim.id("smithing_template.violinite_upgrade." + name)));
	}

	private static Identifier slotIcon(String name) {
		return Identifier.withDefaultNamespace("container/slot/" + name);
	}

	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Nadirheim.id(name));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void initialize() {
		// Onglets du mode créatif
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> {
					output.insertAfter(Items.GLOW_BERRIES, OLD_BERRY);
					output.insertAfter(Items.HONEY_BOTTLE, ADRENOCHROME);
				});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> {
					output.insertAfter(Items.DIAMOND, VIOLINITE, VIOLINITE_OXIDE, VIOLINITE_OXIDE_POWDER);
					output.insertAfter(Items.DRAGON_BREATH, ADRENALINE, ADRENALINE_CRYSTALS, ADRENALINE_POWDER, BLOOD);
					output.insertAfter(Items.STICK, STONE_STICK);
					output.insertAfter(Items.ENDER_EYE, INNER_STAR);
					output.insertAfter(Items.EXPERIENCE_BOTTLE, XP_COIN);
					output.insertBefore(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, VIOLINITE_UPGRADE_SMITHING_TEMPLATE);
					output.insertAfter(Items.NETHERITE_INGOT, MINERAL2, MINERAL1);
				});
		// Outils, armes et armure : placés AVANT la netherite (la violinite vient de l'Overworld, la netherite du Nether)
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
			output.insertBefore(Items.NETHERITE_SHOVEL, VIOLINITE_SHOVEL, VIOLINITE_PICKAXE, VIOLINITE_AXE, VIOLINITE_HOE);
			// Les minéraux de Nadirheim sont plus forts que la netherite : après elle
			output.insertAfter(Items.NETHERITE_HOE, MINERAL2_SHOVEL, MINERAL2_PICKAXE, MINERAL2_AXE, MINERAL1_SHOVEL, MINERAL1_PICKAXE, MINERAL1_AXE);
			output.insertAfter(Items.FLINT_AND_STEEL, DEEP_CHARGE);
			output.insertAfter(Items.LAVA_BUCKET, ADRENOCHROME_BUCKET);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
			output.insertBefore(Items.NETHERITE_SWORD, VIOLINITE_SWORD);
			output.insertAfter(Items.NETHERITE_SWORD, MINERAL2_SWORD, MINERAL1_SWORD);
			output.insertAfter(Items.NETHERITE_AXE, MINERAL2_AXE, MINERAL1_AXE);
			output.insertBefore(Items.NETHERITE_SPEAR, VIOLINITE_SPEAR);
			// La hache est aussi une arme : vanilla la range dans Combat, avec les autres haches
			output.insertBefore(Items.NETHERITE_AXE, VIOLINITE_AXE);
			output.insertBefore(Items.NETHERITE_HELMET,
					VIOLINITE_HELMET, VIOLINITE_CHESTPLATE, VIOLINITE_LEGGINGS, VIOLINITE_BOOTS);
			output.insertBefore(Items.NETHERITE_HORSE_ARMOR, VIOLINITE_HORSE_ARMOR);
			output.insertBefore(Items.NETHERITE_NAUTILUS_ARMOR, VIOLINITE_NAUTILUS_ARMOR);
		});
	}
}
