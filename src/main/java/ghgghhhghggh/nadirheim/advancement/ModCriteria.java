package ghgghhhghggh.nadirheim.advancement;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import java.util.Optional;

/*
 * Critères de progrès propres au mod (ceux que Minecraft ne sait pas détecter tout seul).
 * Les progrès eux-mêmes sont générés par la datagen (ModAdvancementProvider).
 */
public class ModCriteria {
	public static final TorpedoArrowHitTrigger TORPEDO_ARROW_HIT =
			Registry.register(BuiltInRegistries.TRIGGER_TYPES, Nadirheim.id("torpedo_arrow_hit"), new TorpedoArrowHitTrigger());

	public static void initialize() {
		// Une créature vient de prendre des dégâts : était-ce une flèche Torpille, dans l'eau, tirée par un joueur ?
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if (source.getDirectEntity() instanceof AbstractArrow arrow
					&& arrow.hasAttached(ModEnchantments.ARROW_WATER_INERTIA)
					&& arrow.isInWater()
					&& arrow.getOwner() instanceof ServerPlayer player) {
				TORPEDO_ARROW_HIT.trigger(player);
			}
		});
	}

	// Critère utilisable dans ModAdvancementProvider
	public static Criterion<TorpedoArrowHitTrigger.TriggerInstance> torpedoArrowHit() {
		return TORPEDO_ARROW_HIT.createCriterion(new TorpedoArrowHitTrigger.TriggerInstance(Optional.empty()));
	}
}
