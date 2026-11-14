package ghgghhhghggh.nadirheim.worldgen;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Explosions en chaîne de la Charge des profondeurs. Ce ne sont PAS de vraies explosions Minecraft : on choisit
 * nous-mêmes ce qui est détruit, pour imiter la chasse aux débris antiques à la TNT dans le Nether :
 * - chaque charge creuse une boule irrégulière d'environ 3 blocs de rayon, sans rien lâcher (pas de butin) ;
 * - les minerais (tag c:ores), les blocs très résistants (obsidienne...), les blocs avec contenu (coffres,
 *   générateurs...) et les blocs incassables restent en place ;
 * - l'eau et la lave touchées (et juste autour) deviennent de l'obsidienne (la charge contient eau + lave) ;
 * - blessure légère (~1,5 cœur) et petit recul pour les créatures proches.
 */
public class DeepChargeBlasts {
	private static final int COUNT = 6;            // 6 charges...
	private static final double FIRST = 3.0;       // ...la 1re à 3 blocs (son cratère s'ouvre juste devant le joueur)...
	private static final double SPACING = 5.0;     // ...puis tous les 5 blocs = tunnel d'environ 30 blocs
	private static final int FUSE = 10;            // délai avant la 1re explosion (ticks)
	private static final int DELAY = 5;            // délai entre deux explosions (ticks)
	private static final double RADIUS = 3.0;
	private static final float DAMAGE = 3.0F;

	private record Blast(ServerLevel level, Vec3 center, @Nullable Entity source, int tick) {}

	private static final List<Blast> PENDING = new ArrayList<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int now = server.getTickCount();
			Iterator<Blast> it = PENDING.iterator();
			List<Blast> due = new ArrayList<>();
			while (it.hasNext()) {
				Blast blast = it.next();
				if (blast.tick() <= now) {
					due.add(blast);
					it.remove();
				}
			}
			due.forEach(DeepChargeBlasts::explode);
		});
		// En solo, quitter le monde arrête le serveur : on oublie les charges pas encore parties
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> PENDING.clear());
	}

	public static void start(ServerLevel level, Vec3 start, Vec3 direction, @Nullable Entity source) {
		int now = level.getServer().getTickCount();
		for (int i = 0; i < COUNT; i++) {
			PENDING.add(new Blast(level, start.add(direction.scale(FIRST + SPACING * i)), source, now + FUSE + DELAY * i));
		}
	}

	private static void explode(Blast blast) {
		ServerLevel level = blast.level();
		Vec3 c = blast.center();
		RandomSource random = level.getRandom();
		BlockPos centerPos = BlockPos.containing(c);
		int r = (int) Math.ceil(RADIUS) + 1;

		for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-r, -r, -r), centerPos.offset(r, r, r))) {
			double distance = Math.sqrt(pos.distToCenterSqr(c));
			double edge = RADIUS + (random.nextDouble() - 0.5);   // bords irréguliers
			BlockState state = level.getBlockState(pos);

			// Liquides dans le cratère ou juste autour -> obsidienne (évite aussi que le cratère se noie)
			if (distance <= edge + 1 && isLooseLiquid(state)) {
				level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
				continue;
			}
			if (distance <= edge && canBlastAway(state, level, pos)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
			}
		}

		level.playSound(null, centerPos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 3.0F,
				(1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.8F);
		level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, c.x, c.y, c.z, 1, 0, 0, 0, 0);
		level.sendParticles(ParticleTypes.LARGE_SMOKE, c.x, c.y, c.z, 20, 1.5, 1.5, 1.5, 0.02);

		// Blessure légère + recul pour les créatures proches
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(RADIUS + 0.5))) {
			Vec3 push = entity.position().subtract(c);
			if (push.length() <= RADIUS + 0.5) {
				entity.hurtServer(level, level.damageSources().explosion(blast.source(), blast.source()), DAMAGE);
				entity.push(push.normalize().scale(0.4));
			}
		}
	}

	// Eau / lave "libres" (source ou écoulement), plus les plantes aquatiques ; pas les blocs immergés (escaliers...)
	private static boolean isLooseLiquid(BlockState state) {
		if (state.getFluidState().isEmpty()) {
			return false;
		}
		return state.getBlock() instanceof LiquidBlock || !(state.getBlock() instanceof SimpleWaterloggedBlock);
	}

	private static boolean canBlastAway(BlockState state, ServerLevel level, BlockPos pos) {
		return !state.isAir()
				&& !state.is(ConventionalBlockTags.ORES)
				&& !state.hasBlockEntity()
				&& state.getDestroySpeed(level, pos) >= 0
				&& state.getBlock().getExplosionResistance() < 1200.0F;
	}
}
