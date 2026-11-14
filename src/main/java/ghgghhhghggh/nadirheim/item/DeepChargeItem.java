package ghgghhhghggh.nadirheim.item;

import ghgghhhghggh.nadirheim.worldgen.DeepChargeBlasts;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/*
 * Charge des profondeurs : clic droit -> une rangée de 6 charges explose en chaîne sur 30 blocs devant le joueur,
 * comme de la TNT posée tous les 5 blocs (voir DeepChargeBlasts). Ne marche qu'à la couche -52 et en dessous,
 * là où se trouve la violinite.
 */
public class DeepChargeItem extends Item {
	public static final int MAX_USE_Y = -52;

	public DeepChargeItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getBlockY() > MAX_USE_Y) {
			if (!level.isClientSide()) {
				player.sendOverlayMessage(Component.translatable("item.nadirheim.deep_charge.too_high"));
			}
			return InteractionResult.FAIL;
		}
		if (level instanceof ServerLevel serverLevel) {
			// Direction horizontale du regard (on creuse à l'horizontale, à hauteur de poitrine)
			float yaw = player.getYRot() * ((float) Math.PI / 180F);
			Vec3 direction = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
			Vec3 start = player.position().add(0, 1, 0);
			DeepChargeBlasts.start(serverLevel, start, direction, player);
			level.playSound(null, player.blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 0.8F);
			player.getItemInHand(hand).consume(1, player);
		}
		return InteractionResult.SUCCESS;
	}
}
