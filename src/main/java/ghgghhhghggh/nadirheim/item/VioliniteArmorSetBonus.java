package ghgghhhghggh.nadirheim.item;

import ghgghhhghggh.nadirheim.Nadirheim;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/*
 * Bonus d'armure complète en violinite : on respire bien plus longtemps sous l'eau
 * (l'armure vient des profondeurs, elle est faite pour résister à la pression).
 *
 * On utilise l'attribut vanilla "oxygen_bonus", le même que l'enchantement Respiration :
 * sous l'eau, à chaque tick, l'air a une chance sur (bonus + 1) de baisser.
 * Avec un bonus de 3, l'air baisse 4 fois moins vite : 60 secondes au lieu de 15.
 * Ça se cumule avec Respiration III (bonus total 6 -> 7 fois plus long).
 *
 * Même bonus pour un cheval qui porte l'armure de cheval en violinite.
 * (Le nautile, lui, a un bonus à part : voir NautilusMixin.)
 */
public class VioliniteArmorSetBonus {
	public static final double OXYGEN_BONUS = 3.0;

	private static final Identifier MODIFIER_ID = Nadirheim.id("violinite_armor_set_oxygen");

	public static void initialize() {
		// Appelé côté serveur à chaque fois qu'une entité (joueur, zombie...) change d'équipement,
		// y compris quand le joueur se connecte
		ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, previous, current) -> {
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || slot.getType() == EquipmentSlot.Type.ANIMAL_ARMOR) {
				update(entity);
			}
		});
	}

	private static void update(LivingEntity entity) {
		AttributeInstance oxygen = entity.getAttribute(Attributes.OXYGEN_BONUS);
		if (oxygen == null) {
			return;
		}

		boolean fullSet = entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.VIOLINITE_HELMET)
				&& entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.VIOLINITE_CHESTPLATE)
				&& entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.VIOLINITE_LEGGINGS)
				&& entity.getItemBySlot(EquipmentSlot.FEET).is(ModItems.VIOLINITE_BOOTS);

		// Seuls les chevaux (et ânes, mules...) peuvent porter une armure de cheval
		boolean horseArmor = entity.getItemBySlot(EquipmentSlot.BODY).is(ModItems.VIOLINITE_HORSE_ARMOR);

		boolean hasBonus = fullSet || horseArmor;

		// Modificateur "transient" : il n'est pas sauvegardé, il est recalculé au chargement de l'entité
		if (hasBonus && !oxygen.hasModifier(MODIFIER_ID)) {
			oxygen.addTransientModifier(new AttributeModifier(MODIFIER_ID, OXYGEN_BONUS, AttributeModifier.Operation.ADD_VALUE));
		} else if (!hasBonus) {
			oxygen.removeModifier(MODIFIER_ID);
		}
	}
}
