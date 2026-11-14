package ghgghhhghggh.nadirheim.mixin;

import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * AbstractArrow.getWaterInertia donne la part de vitesse qu'une flèche garde à chaque tick dans l'eau
 * (0.6 en vanilla). Si la flèche a été tirée avec Torpille, on renvoie la valeur de l'enchantement à la place.
 * Tourne côté serveur ET client : la valeur est synchronisée (voir ModEnchantments.ARROW_WATER_INERTIA).
 * Le trident redéfinit cette fonction sans passer par ici, il n'est donc pas affecté.
 */
@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
	@Inject(method = "getWaterInertia", at = @At("HEAD"), cancellable = true)
	private void nadirheim$torpedoWaterInertia(CallbackInfoReturnable<Float> cir) {
		Float inertia = ((AbstractArrow) (Object) this).getAttached(ModEnchantments.ARROW_WATER_INERTIA);
		if (inertia != null) {
			cir.setReturnValue(inertia);
		}
	}
}
