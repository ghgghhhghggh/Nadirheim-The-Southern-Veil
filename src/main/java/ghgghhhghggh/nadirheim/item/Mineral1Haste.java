package ghgghhhghggh.nadirheim.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

/*
 * Bonus des outils en minéral 1 : Célérité (Haste I) tant qu'un outil ou une arme en minéral 1 est dans la main
 * principale. Vérifié côté serveur toutes les 10 ticks (0,5 s) pour chaque joueur.
 * L'effet est posé avec une durée infinie et le drapeau "ambiant" (icône à bord bleu, comme un phare) : c'est à ça
 * qu'on le reconnaît pour le retirer quand l'outil quitte la main, sans toucher à une Célérité de potion ou de phare.
 */
public class Mineral1Haste {
	private static final int CHECK_INTERVAL = 10;
	private static int ticks;

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (++ticks % CHECK_INTERVAL != 0) return;
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				MobEffectInstance haste = player.getEffect(MobEffects.HASTE);
				boolean ours = haste != null && haste.isInfiniteDuration() && haste.isAmbient();
				if (isMineral1Tool(player.getMainHandItem())) {
					if (!ours) {
						player.addEffect(new MobEffectInstance(MobEffects.HASTE, MobEffectInstance.INFINITE_DURATION, 0, true, false, true));
					}
				} else if (ours) {
					player.removeEffect(MobEffects.HASTE);
				}
			}
		});
	}

	public static boolean isMineral1Tool(ItemStack stack) {
		return stack.is(ModItems.MINERAL1_SWORD) || stack.is(ModItems.MINERAL1_SHOVEL)
				|| stack.is(ModItems.MINERAL1_PICKAXE) || stack.is(ModItems.MINERAL1_AXE);
	}
}
