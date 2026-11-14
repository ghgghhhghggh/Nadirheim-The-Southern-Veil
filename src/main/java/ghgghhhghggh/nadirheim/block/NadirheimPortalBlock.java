package ghgghhhghggh.nadirheim.block;

import com.mojang.serialization.MapCodec;
import ghgghhhghggh.nadirheim.block.entity.NadirheimPortalBlockEntity;
import ghgghhhghggh.nadirheim.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/*
 * Portail de Nadirheim. Le bloc lui-même est invisible ; tout le rendu est fait par client/renderer/NadirheimPortalRenderer
 * avec le shader du portail. Il apparaît au centre des 4 piliers (voir portal/PortalOpening).
 * Téléportation :
 * - depuis l'Overworld (ou ailleurs) : on arrive dans Nadirheim haut dans le ciel, à la même position x/z, et on
 *   descend doucement (Chute lente) pour découvrir le monde d'en haut ;
 * - depuis Nadirheim : retour PROVISOIRE à la surface de l'Overworld, à la même position (le vrai retour sera décidé
 *   plus tard).
 */
public class NadirheimPortalBlock extends BaseEntityBlock implements Portal {
	public static final MapCodec<NadirheimPortalBlock> CODEC = simpleCodec(NadirheimPortalBlock::new);
	private static final int ARRIVAL_Y = 160;                 // hauteur d'arrivée dans Nadirheim
	private static final int SLOW_FALLING_TICKS = 2 * 60 * 20; // largement de quoi atteindre le sol

	public NadirheimPortalBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<NadirheimPortalBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NadirheimPortalBlockEntity(pos, state);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		if (entity.canUsePortal(false)) {
			entity.setAsInsidePortal(this, pos);
		}
	}

	@Override
	public @Nullable TeleportTransition getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos portalEntryPos) {
		boolean toNadirheim = currentLevel.dimension() != ModDimensions.NADIRHEIM;
		ServerLevel target = currentLevel.getServer().getLevel(toNadirheim ? ModDimensions.NADIRHEIM : Level.OVERWORLD);
		if (target == null) {
			return null;
		}
		double x = entity.getX(), z = entity.getZ();
		BlockPos clamped = target.getWorldBorder().clampToBounds(x, entity.getY(), z);
		if (toNadirheim) {
			Vec3 sky = new Vec3(clamped.getX() + 0.5, ARRIVAL_Y, clamped.getZ() + 0.5);
			return new TeleportTransition(target, sky, Vec3.ZERO, entity.getYRot(), entity.getXRot(),
					TeleportTransition.PLAY_PORTAL_SOUND.then(NadirheimPortalBlock::slowFall));
		}
		int surface = target.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clamped.getX(), clamped.getZ());
		Vec3 ground = new Vec3(clamped.getX() + 0.5, surface, clamped.getZ() + 0.5);
		return new TeleportTransition(target, ground, Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.PLAY_PORTAL_SOUND);
	}

	private static void slowFall(Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, SLOW_FALLING_TICKS, 0, false, false, true));
		}
	}

	// Étincelles autour du portail (il est dessiné 2,25 blocs de large : voir NadirheimPortalRenderer)
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(3) == 0) {
			level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.4, pos.getY() + random.nextDouble(),
					pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.4, 0.0, 0.01, 0.0);
		}
	}

	// L'eau et la lave ne le remplacent pas (comme le portail de l'End)
	@Override
	protected boolean canBeReplaced(BlockState state, Fluid fluid) {
		return false;
	}
}
