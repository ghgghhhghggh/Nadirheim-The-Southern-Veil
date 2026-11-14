package ghgghhhghggh.nadirheim.mixin;

import ghgghhhghggh.nadirheim.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Un mixin modifie une fonction de Minecraft. Ici, Nautilus.handleAirSupply est la fonction qui,
 * à chaque tick, fait perdre de l'air au nautile hors de l'eau (15 secondes), puis lui inflige
 * des dégâts de dessèchement.
 *
 * On s'insère au tout début (HEAD) : si le nautile porte l'armure en violinite, on remplit son air
 * et on annule la suite de la fonction. Il peut donc rester hors de l'eau indéfiniment.
 * (Le nautile zombie ne se dessèche déjà pas en vanilla, il n'est pas concerné.)
 */
@Mixin(Nautilus.class)
public class NautilusMixin {
	@Inject(method = "handleAirSupply", at = @At("HEAD"), cancellable = true)
	private void nadirheim$breatheWithVioliniteArmor(ServerLevel level, int preTickAirSupply, CallbackInfo ci) {
		Nautilus nautilus = (Nautilus) (Object) this;
		if (nautilus.getItemBySlot(EquipmentSlot.BODY).is(ModItems.VIOLINITE_NAUTILUS_ARMOR)) {
			nautilus.setAirSupply(nautilus.getMaxAirSupply());
			ci.cancel();
		}
	}
}
