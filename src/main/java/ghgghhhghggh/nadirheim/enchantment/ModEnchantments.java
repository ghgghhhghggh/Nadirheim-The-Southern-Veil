package ghgghhhghggh.nadirheim.enchantment;

import com.mojang.serialization.Codec;
import ghgghhhghggh.nadirheim.Nadirheim;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/*
 * Enchantement Torpille (arc et arbalète) : les flèches gardent leur vitesse sous l'eau.
 *
 * En vanilla, à chaque tick dans l'eau, une flèche garde 60 % de sa vitesse (AbstractArrow.getWaterInertia = 0.6),
 * elle s'arrête donc très vite. Le trident, lui, garde 99 %, comme une flèche dans l'air.
 *   Torpille I  -> 90 % : portée sous l'eau environ 4 fois plus longue
 *   Torpille II -> 99 % : même trajectoire qu'à l'air libre
 *
 * La trajectoire est calculée à la fois par le serveur et par le client (pour l'affichage), donc la valeur est
 * stockée sur la flèche dans une "attachment" Fabric synchronisée avec les joueurs (voir AbstractArrowMixin).
 */
public class ModEnchantments {
	public static final ResourceKey<Enchantment> TORPEDO = ResourceKey.create(Registries.ENCHANTMENT, Nadirheim.id("torpedo"));

	// Items qui peuvent recevoir Torpille : tous les arcs et arbalètes (rempli par la datagen)
	public static final TagKey<Item> TORPEDO_ENCHANTABLE = TagKey.create(Registries.ITEM, Nadirheim.id("enchantable/torpedo"));

	private static final float[] WATER_INERTIA_PER_LEVEL = {0.9F, 0.99F};

	// Inertie dans l'eau d'une flèche tirée avec Torpille (absente = comportement vanilla)
	public static final AttachmentType<Float> ARROW_WATER_INERTIA = AttachmentRegistry.create(Nadirheim.id("arrow_water_inertia"),
			builder -> builder.persistent(Codec.FLOAT).syncWith(ByteBufCodecs.FLOAT, AttachmentSyncPredicate.all()));

	// Les enchantements sont des données (data/nadirheim/enchantment/torpedo.json), générées par la datagen
	public static void bootstrap(BootstrapContext<Enchantment> context) {
		HolderGetter<Item> items = context.lookup(Registries.ITEM);
		context.register(TORPEDO, Enchantment.enchantment(Enchantment.definition(
				items.getOrThrow(TORPEDO_ENCHANTABLE),
				2,                                  // poids : rare à la table (Fortune = 2, Toucher de soie = 1)
				2,                                  // niveau max
				Enchantment.dynamicCost(20, 10),    // puissance minimale : 20 pour le niveau I, 30 pour le niveau II
				Enchantment.dynamicCost(50, 10),    // puissance maximale
				4,                                  // coût de base à l'enclume
				EquipmentSlotGroup.MAINHAND
		)).build(TORPEDO.identifier()));
	}

	public static void initialize() {
		// Quand une flèche apparaît dans le monde, on regarde si l'arme qui l'a tirée a Torpille
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (!(entity instanceof AbstractArrow arrow) || arrow.hasAttached(ARROW_WATER_INERTIA)) {
				return;
			}
			ItemStack weapon = arrow.getWeaponItem();
			if (weapon == null) {
				return;
			}
			int enchantLevel = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(TORPEDO)
					.map(torpedo -> EnchantmentHelper.getItemEnchantmentLevel(torpedo, weapon))
					.orElse(0);
			if (enchantLevel > 0) {
				arrow.setAttached(ARROW_WATER_INERTIA, WATER_INERTIA_PER_LEVEL[Math.min(enchantLevel, WATER_INERTIA_PER_LEVEL.length) - 1]);
			}
		});
	}
}
