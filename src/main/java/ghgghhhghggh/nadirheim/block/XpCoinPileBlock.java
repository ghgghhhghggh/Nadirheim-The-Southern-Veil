package ghgghhhghggh.nadirheim.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

/*
 * Tas de pièces d'xp posées au sol : 1 à 4 pièces, une par quart de bloc (même principe que la litière de feuilles
 * et les pétales roses). Offrandes, marques de respect, cadeaux : les habitants de Nadirheim en posent partout.
 * - on pose / ajoute une pièce avec accroupi + clic droit (voir XpCoinItem) ;
 * - peut être sous l'eau (puits à souhaits), comme les cornichons de mer ;
 * - luit faiblement (lumière dans ModBlocks) ; se casse d'un coup et rend toutes ses pièces.
 */
public class XpCoinPileBlock extends Block implements SegmentableBlock, SimpleWaterloggedBlock {
	public static final MapCodec<XpCoinPileBlock> CODEC = simpleCodec(XpCoinPileBlock::new);
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private final Function<BlockState, VoxelShape> shapes;

	public XpCoinPileBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, 1).setValue(WATERLOGGED, false));
		shapes = getShapeForEachState(getShapeCalculator(FACING, AMOUNT));
	}

	@Override
	protected MapCodec<XpCoinPileBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	// On peut ajouter une pièce au tas tant qu'il en a moins de 4. Contrairement à la litière vanilla, on le fait
	// accroupi (le clic droit simple sert à absorber l'xp), donc pas de test "accroupi" ici.
	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return context.getItemInHand().is(asItem()) && state.getValue(AMOUNT) < MAX_SEGMENT || super.canBeReplaced(state, context);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos belowPos = pos.below();
		return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
			Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		if (!state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		if (state.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = getStateForPlacement(context, this, AMOUNT, FACING);
		// Nouveau tas : sous l'eau si on le pose dans de l'eau (un tas existant garde son état)
		if (!context.getLevel().getBlockState(context.getClickedPos()).is(this)) {
			state = state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
		}
		return state;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.apply(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, AMOUNT, WATERLOGGED);
	}
}
