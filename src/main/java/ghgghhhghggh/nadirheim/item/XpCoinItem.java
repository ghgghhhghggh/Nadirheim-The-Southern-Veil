package ghgghhhghggh.nadirheim.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/*
 * Pièce d'xp. S'obtiendra seulement dans Nadirheim (offrandes, cadeaux des habitants...), jamais comme monnaie.
 * - clic droit : absorbe 1 pièce, l'xp va directement dans la barre (pas d'orbes, donc pas de Raccommodage) ;
 * - accroupi + clic droit sur un bloc : pose la pièce au sol, ou l'ajoute au tas (voir block/XpCoinPileBlock) ;
 * - accroupi + clic droit dans le vide : absorbe toute la pile.
 * Côté client, sa couleur pulse vert <-> jaune comme les orbes d'xp (voir client/color/XpOrbTint).
 */
public class XpCoinItem extends BlockItem {
	public static final int XP_PER_COIN = 2;

	public XpCoinItem(Block pile, Item.Properties properties) {
		super(pile, properties);
	}

	// Clic sur un bloc : on ne pose que si le joueur est accroupi. Sinon PASS -> le jeu appelle use() (absorber)
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		return player != null && player.isSecondaryUseActive() ? super.useOn(context) : InteractionResult.PASS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			int count = player.isSecondaryUseActive() ? stack.getCount() : 1;
			player.giveExperiencePoints(XP_PER_COIN * count);
			// Même son que quand on ramasse une orbe
			RandomSource random = level.getRandom();
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP,
					SoundSource.PLAYERS, 0.1F, (random.nextFloat() - random.nextFloat()) * 0.35F + 0.9F);
			stack.consume(count, player);
		}
		return InteractionResult.SUCCESS;
	}
}
