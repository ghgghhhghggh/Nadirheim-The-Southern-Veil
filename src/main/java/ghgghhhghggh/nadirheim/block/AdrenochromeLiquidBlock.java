package ghgghhhghggh.nadirheim.block;

import ghgghhhghggh.nadirheim.alchemy.Adrenochrome;
import ghgghhhghggh.nadirheim.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

// Bloc d'adrénochrome posé dans le monde. Différences avec un liquide vanilla : une source déjà entamée par un bain
// ne se remet plus en seau, et une source fraîchement posée repart avec un compteur d'évaporation à zéro.
public class AdrenochromeLiquidBlock extends LiquidBlock {
	public AdrenochromeLiquidBlock(BlockBehaviour.Properties properties) {
		super(ModFluids.ADRENOCHROME, properties);
	}

	@Override
	public ItemStack pickupBlock(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state) {
		if (Adrenochrome.isUsed(level, pos)) {
			return ItemStack.EMPTY;
		}
		return super.pickupBlock(user, level, pos, state);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		boolean newSource = state.getValue(LEVEL) == 0 && !(oldState.is(this) && oldState.getValue(LEVEL) == 0);
		if (!level.isClientSide() && newSource) {
			Adrenochrome.resetSource(level, pos);
		}
	}
}
