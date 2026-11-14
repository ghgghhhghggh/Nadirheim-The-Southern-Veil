package ghgghhhghggh.nadirheim.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/*
 * Torche sous-marine posée au sol : une torche qui peut être dans l'eau ("waterlogged", comme une lanterne),
 * sans flamme ni fumée. Sa version accrochée au mur est UnderwaterWallTorchBlock.
 */
public class UnderwaterTorchBlock extends TorchBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public UnderwaterTorchBlock(BlockBehaviour.Properties properties) {
		super(ParticleTypes.GLOW, properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null
				: state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
			Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		// Comme tous les blocs immergés : l'eau autour continue de s'écouler normalement
		if (state.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		spawnParticles(state, level, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, random);
	}

	// Lueur de pierre lumineuse, plus des bulles quand la torche est sous l'eau
	static void spawnParticles(BlockState state, Level level, double x, double y, double z, RandomSource random) {
		level.addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 0.0, 0.0);
		if (state.getValue(WATERLOGGED) && random.nextInt(3) == 0) {
			level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0, 0.04, 0.0);
		}
	}
}
