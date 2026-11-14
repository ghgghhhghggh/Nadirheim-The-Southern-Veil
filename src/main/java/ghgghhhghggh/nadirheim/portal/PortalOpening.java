package ghgghhhghggh.nadirheim.portal;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.block.PillarCapitalBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Ouverture du portail de Nadirheim. La salle : 4 piliers complets (base, fût bas, fût haut, chapiteau) aux coins
 * d'un carré avec SPACING - 1 = 5 blocs entre eux. Quand le 4e Inner Star est posé (PillarCapitalBlock), une mise en
 * scène de 2 secondes commence, visible par tous les joueurs présents :
 * - 0 s : gerbe de flammes sur les 4 astres + grondement ;
 * - 0 à 1,5 s : traînées de flammes qui partent des astres et convergent vers une boule de lumière au centre ;
 * - 2 s : flash, son d'ouverture du portail de l'End, et les 2 blocs de portail apparaissent au centre du carré,
 *   flottant 1 bloc au-dessus du niveau de la base des piliers (à la hauteur des fûts).
 * File d'attente propre au mod, avancée à chaque tick du serveur (comme DeepChargeBlasts).
 */
public class PortalOpening {
	public static final int SPACING = 6;              // distance entre deux chapiteaux voisins (5 blocs entre eux)
	private static final int CONVERGE_TICKS = 30;     // durée des traînées (1,5 s)
	private static final int OPEN_TICK = 40;          // apparition du portail (2 s)

	private record Opening(ServerLevel level, BlockPos portalBottom, List<Vec3> stars, int startTick) {}

	private static final List<Opening> PENDING = new ArrayList<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int now = server.getTickCount();
			Iterator<Opening> it = PENDING.iterator();
			while (it.hasNext()) {
				Opening opening = it.next();
				int tick = now - opening.startTick();
				animate(opening, tick);
				if (tick >= OPEN_TICK) {
					open(opening);
					it.remove();
				}
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> PENDING.clear());
	}

	// Appelé quand un astre vient d'être posé sur le chapiteau "capital" : y a-t-il un carré complet dont il est un coin ?
	public static void tryStart(ServerLevel level, BlockPos capital) {
		for (int dx : new int[]{-SPACING, SPACING}) {
			for (int dz : new int[]{-SPACING, SPACING}) {
				BlockPos a = capital, b = capital.offset(dx, 0, 0), c = capital.offset(0, 0, dz), d = capital.offset(dx, 0, dz);
				if (!isLitPillar(level, a) || !isLitPillar(level, b) || !isLitPillar(level, c) || !isLitPillar(level, d)) {
					continue;
				}
				// Centre du carré, à hauteur du fût bas (= 1 bloc au-dessus de la base)
				BlockPos portalBottom = capital.offset(dx / 2, -2, dz / 2);
				if (level.getBlockState(portalBottom).is(ModBlocks.NADIRHEIM_PORTAL) || isPending(level, portalBottom)) {
					return;
				}
				List<Vec3> stars = List.of(starCenter(a), starCenter(b), starCenter(c), starCenter(d));
				PENDING.add(new Opening(level, portalBottom, stars, level.getServer().getTickCount()));
				return;
			}
		}
	}

	// Chapiteau avec astre + fût haut + fût bas + base, dans cet ordre en descendant
	private static boolean isLitPillar(ServerLevel level, BlockPos capital) {
		BlockState state = level.getBlockState(capital);
		return state.is(ModBlocks.PILLAR_CAPITAL) && state.getValue(PillarCapitalBlock.HAS_STAR)
				&& level.getBlockState(capital.below()).is(ModBlocks.PILLAR_SHAFT_UPPER)
				&& level.getBlockState(capital.below(2)).is(ModBlocks.PILLAR_SHAFT_LOWER)
				&& level.getBlockState(capital.below(3)).is(ModBlocks.PILLAR_BASE);
	}

	private static boolean isPending(ServerLevel level, BlockPos portalBottom) {
		return PENDING.stream().anyMatch(o -> o.level() == level && o.portalBottom().equals(portalBottom));
	}

	// L'astre flotte ~1,5 bloc au-dessus du bas du chapiteau (voir PillarCapitalRenderer)
	private static Vec3 starCenter(BlockPos capital) {
		return Vec3.atLowerCornerOf(capital).add(0.5, 1.5, 0.5);
	}

	private static void animate(Opening opening, int tick) {
		ServerLevel level = opening.level();
		RandomSource random = level.getRandom();
		Vec3 center = Vec3.atLowerCornerOf(opening.portalBottom()).add(0.5, 1.0, 0.5);   // milieu du futur portail (1x2)

		if (tick == 0) {
			// Les astres s'embrasent, grondement sourd
			for (Vec3 star : opening.stars()) {
				burst(level, ParticleTypes.FLAME, star, 40, 0.3, 0.08);
				burst(level, ParticleTypes.END_ROD, star, 10, 0.3, 0.05);
			}
			sound(level, center, SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.5F, 0.5F);
			sound(level, center, SoundEvents.GENERIC_EXPLODE.value(), 0.4F, 0.4F);
		}
		if (tick == 20) {
			sound(level, center, SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.5F, 0.8F);
		}
		if (tick <= CONVERGE_TICKS) {
			// Traînées de flammes : le front avance de chaque astre vers le centre, avec des flammèches derrière lui
			double front = (double) tick / CONVERGE_TICKS;
			for (Vec3 star : opening.stars()) {
				for (int i = 0; i < 4; i++) {
					double f = Math.max(0.0, front - random.nextDouble() * 0.15);
					Vec3 p = star.lerp(center, f).add(jitter(random, 0.12), jitter(random, 0.12), jitter(random, 0.12));
					Vec3 toward = center.subtract(star).normalize().scale(0.04);
					level.sendParticles(ParticleTypes.FLAME, true, true, p.x, p.y, p.z, 0, toward.x, toward.y, toward.z, 1.0);
				}
			}
			// Boule de lumière qui grossit au centre
			double radius = 0.15 + 0.6 * front;
			burst(level, ParticleTypes.END_ROD, center, 2 + tick / 4, radius, 0.0);
		} else {
			// Boule pleine qui vibre jusqu'à l'ouverture
			burst(level, ParticleTypes.END_ROD, center, 12, 0.75, 0.02);
			burst(level, ParticleTypes.SOUL_FIRE_FLAME, center, 6, 0.75, 0.02);
		}
	}

	private static void open(Opening opening) {
		ServerLevel level = opening.level();
		BlockPos bottom = opening.portalBottom();
		Vec3 center = Vec3.atLowerCornerOf(bottom).add(0.5, 1.0, 0.5);

		level.setBlock(bottom, ModBlocks.NADIRHEIM_PORTAL.defaultBlockState(), Block.UPDATE_ALL);
		level.setBlock(bottom.above(), ModBlocks.NADIRHEIM_PORTAL.defaultBlockState(), Block.UPDATE_ALL);

		// Flash blanc, son d'ouverture du portail de l'End, onde de choc légère
		level.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH, 0xFFFFFFFF), true, true, center.x, center.y, center.z, 1, 0, 0, 0, 0);
		burst(level, ParticleTypes.END_ROD, center, 80, 0.3, 0.25);
		burst(level, ParticleTypes.SOUL_FIRE_FLAME, center, 40, 0.3, 0.15);
		sound(level, center, SoundEvents.END_PORTAL_SPAWN, 1.0F, 1.0F);
		sound(level, center, SoundEvents.GENERIC_EXPLODE.value(), 0.6F, 0.5F);
	}

	private static void burst(ServerLevel level, ParticleOptions particle, Vec3 at, int count, double spread, double speed) {
		level.sendParticles(particle, true, true, at.x, at.y, at.z, count, spread, spread, spread, speed);
	}

	private static void sound(ServerLevel level, Vec3 at, SoundEvent sound, float volume, float pitch) {
		level.playSound(null, at.x, at.y, at.z, sound, SoundSource.BLOCKS, volume, pitch);
	}

	private static double jitter(RandomSource random, double amount) {
		return (random.nextDouble() - 0.5) * 2.0 * amount;
	}
}
