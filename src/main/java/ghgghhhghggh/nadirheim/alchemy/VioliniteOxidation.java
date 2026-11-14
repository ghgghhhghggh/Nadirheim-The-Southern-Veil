package ghgghhhghggh.nadirheim.alchemy;

import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/*
 * Oxyde de violinite (catalyseur de l'alchimie du mod) : clic droit avec de la violinite sur un bloc de cuivre
 * TOTALEMENT oxydé (et non ciré). La violinite se charge de la patine : le bloc redescend d'un stade d'oxydation
 * (comme gratté à la hache, même son et mêmes particules), 1 violinite devient 1 oxyde de violinite.
 * Il faut ensuite attendre que le cuivre s'oxyde à nouveau.
 */
public class VioliniteOxidation {
	public static void initialize() {
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (!stack.is(ModItems.VIOLINITE) || player.isSpectator()) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);
			// Les blocs cirés ne sont pas des WeatheringCopper : ils sont exclus d'office. Portes et coffres (2 blocs) aussi.
			if (!(state.getBlock() instanceof WeatheringCopper copper) || copper.getAge() != WeatheringCopper.WeatherState.OXIDIZED
					|| state.getBlock() instanceof DoorBlock || state.getBlock() instanceof ChestBlock) {
				return InteractionResult.PASS;
			}
			Optional<BlockState> scraped = WeatheringCopper.getPrevious(state);
			if (scraped.isEmpty()) {
				return InteractionResult.PASS;
			}
			if (!level.isClientSide()) {
				level.setBlock(pos, scraped.get(), Block.UPDATE_ALL_IMMEDIATE);
				level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.levelEvent(null, 3005, pos, 0);   // particules de grattage du cuivre (comme la hache)
				stack.consume(1, player);
				player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.VIOLINITE_OXIDE));
			}
			return InteractionResult.SUCCESS;
		});
	}
}
