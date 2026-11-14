package ghgghhhghggh.nadirheim.fluid;

import ghgghhhghggh.nadirheim.alchemy.Adrenochrome;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

/*
 * Adrénochrome liquide. Comme la lave, il n'est PAS infini (deux sources côte à côte n'en créent pas d'autres) et ne
 * s'étale que sur quelques blocs. Le bain (effets, morts-vivants brûlés, évaporation) se passe dans les blocs de
 * SOURCE : voir alchemy/Adrenochrome. Il éteint le feu comme l'eau.
 */
public abstract class AdrenochromeFluid extends FlowingFluid {
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_ADRENOCHROME;
	}

	@Override
	public Fluid getSource() {
		return ModFluids.ADRENOCHROME;
	}

	@Override
	public Item getBucket() {
		return ModItems.ADRENOCHROME_BUCKET;
	}

	@Override
	protected boolean canConvertToSource(ServerLevel level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
		Block.dropResources(state, level, pos, blockEntity);
	}

	@Override
	protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
		effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
		if (level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living && level.getFluidState(pos).isSource()) {
			Adrenochrome.bathe(serverLevel, pos, living);
		}
	}

	@Override
	public int getSlopeFindDistance(LevelReader level) {
		return 2;
	}

	@Override
	public BlockState createLegacyBlock(FluidState fluidState) {
		return ModBlocks.ADRENOCHROME.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
	}

	@Override
	public boolean isSame(Fluid other) {
		return other == ModFluids.ADRENOCHROME || other == ModFluids.FLOWING_ADRENOCHROME;
	}

	// Perd 2 niveaux par bloc : s'étale sur 3 blocs environ (comme la lave dans l'Overworld)
	@Override
	public int getDropOff(LevelReader level) {
		return 2;
	}

	// Coule 2 fois moins vite que l'eau
	@Override
	public int getTickDelay(LevelReader level) {
		return 10;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) {
		return direction == Direction.DOWN && !isSame(other);
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL);
	}

	public static class Flowing extends AdrenochromeFluid {
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}
	}

	public static class Source extends AdrenochromeFluid {
		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}
	}
}
