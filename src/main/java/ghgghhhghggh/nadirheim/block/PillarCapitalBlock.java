package ghgghhhghggh.nadirheim.block;

import com.mojang.serialization.MapCodec;
import ghgghhhghggh.nadirheim.block.entity.PillarCapitalBlockEntity;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.portal.PortalOpening;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/*
 * Chapiteau du pilier : on y pose un Inner Star (clic droit), comme l'œil de l'Ender sur un cadre de portail.
 * L'astre est alors dessiné au-dessus du pilier, flottant et tournant (PillarCapitalRenderer, côté client) ;
 * le bloc éclaire et lâche des particules. Une fois posé, l'astre ne peut plus être repris.
 * Le bloc a un "block entity" uniquement pour pouvoir être dessiné en plus de son modèle.
 */
public class PillarCapitalBlock extends BaseEntityBlock {
	public static final MapCodec<PillarCapitalBlock> CODEC = simpleCodec(PillarCapitalBlock::new);
	public static final BooleanProperty HAS_STAR = BooleanProperty.create("has_star");

	public PillarCapitalBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HAS_STAR, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HAS_STAR);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PillarCapitalBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!itemStack.is(ModItems.INNER_STAR) || state.getValue(HAS_STAR)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (level instanceof ServerLevel serverLevel) {
			level.setBlock(pos, state.setValue(HAS_STAR, true), Block.UPDATE_ALL);
			itemStack.consume(1, player);
			level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 0.8F);
			// Si c'était le 4e astre d'un carré de piliers, le portail s'ouvre au centre (voir portal/PortalOpening)
			PortalOpening.tryStart(serverLevel, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(HAS_STAR)) {
			return;
		}
		// Flammes qui jaillissent de la surface de l'astre (centré ~1,5 bloc au-dessus du bas du chapiteau, rayon ~0,3)
		for (int i = 0; i < 6; i++) {
			double dx = random.nextGaussian(), dy = random.nextGaussian(), dz = random.nextGaussian();
			double length = Math.max(Math.sqrt(dx * dx + dy * dy + dz * dz), 0.001);
			dx /= length;
			dy /= length;
			dz /= length;
			double x = pos.getX() + 0.5 + dx * 0.3, y = pos.getY() + 1.5 + dy * 0.3, z = pos.getZ() + 0.5 + dz * 0.3;
			// Légèrement projetées vers l'extérieur et vers le haut
			level.addParticle(random.nextInt(3) == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.FLAME,
					x, y, z, dx * 0.02, dy * 0.02 + 0.02, dz * 0.02);
		}
		// Quelques étincelles lumineuses
		if (random.nextInt(3) == 0) {
			level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + (random.nextDouble() - 0.5),
					pos.getY() + 1.5 + (random.nextDouble() - 0.5), pos.getZ() + 0.5 + (random.nextDouble() - 0.5), 0.0, 0.01, 0.0);
		}
	}
}
