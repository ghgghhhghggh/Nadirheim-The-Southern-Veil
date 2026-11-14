package ghgghhhghggh.nadirheim.block;

import com.mojang.serialization.MapCodec;
import ghgghhhghggh.nadirheim.block.entity.ModBlockEntities;
import ghgghhhghggh.nadirheim.block.entity.SieveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

// Bloc du tamis : ouvre sa fenêtre au clic droit ; la logique est dans SieveBlockEntity
public class SieveBlock extends BaseEntityBlock {
	public static final MapCodec<SieveBlock> CODEC = simpleCodec(SieveBlock::new);

	public SieveBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SieveBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level instanceof ServerLevel serverLevel
				? createTickerHelper(type, ModBlockEntities.SIEVE, (l, pos, s, sieve) -> SieveBlockEntity.serverTick(serverLevel, pos, s, sieve))
				: null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MenuProvider sieve) {
			player.openMenu(sieve);
		}
		return InteractionResult.SUCCESS;
	}

	// Le contenu tombe au sol quand on casse le tamis (géré par le block entity) ; on prévient les comparateurs
	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		Containers.updateNeighboursAfterDestroy(state, level, pos);
	}
}
