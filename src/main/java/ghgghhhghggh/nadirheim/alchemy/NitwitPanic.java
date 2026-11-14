package ghgghhhghggh.nadirheim.alchemy;

import com.mojang.serialization.Codec;
import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Adrénaline : un nitwit ADULTE enfermé dans un espace de 1x2 (les 4 côtés à hauteur des pieds et de la tête + le
 * dessus de la tête bloqués) panique. Après PANIC_SECONDS secondes enfermé :
 * - il lâche une fiole d'adrénaline, au plus une fois par jour Minecraft (ADRENALINE_COOLDOWN) ;
 * - il casse 2 blocs (pieds + tête, d'un même côté) pour s'enfuir : tout sauf les blocs incassables (bedrock,
 *   barrière, dalles et piliers du temple...). Il faut donc reconstruire la case à chaque fois : farmable, mais pas
 *   automatisable facilement.
 * Jamais les bébés villageois (ni, plus tard, le nitwit de la quête).
 */
public class NitwitPanic {
	private static final int CHECK_INTERVAL = 20;              // une vérification par seconde
	private static final int PANIC_SECONDS = 25;
	private static final long ADRENALINE_COOLDOWN = 24000L;    // 1 jour Minecraft

	// Secondes passées enfermé (pas sauvegardé : un nitwit rechargé recommence à zéro)
	private static final AttachmentType<Integer> PANIC = AttachmentRegistry.create(Nadirheim.id("nitwit_panic"));
	// Moment (temps du monde) de la dernière adrénaline lâchée (sauvegardé avec le villageois)
	private static final AttachmentType<Long> LAST_ADRENALINE = AttachmentRegistry.createPersistent(Nadirheim.id("nitwit_last_adrenaline"), Codec.LONG);

	public static void initialize() {
		ServerTickEvents.END_LEVEL_TICK.register(level -> {
			if (level.getGameTime() % CHECK_INTERVAL != 0) {
				return;
			}
			for (Villager villager : level.getEntities(EntityTypes.VILLAGER, NitwitPanic::isAdultNitwit)) {
				update(level, villager);
			}
		});
	}

	private static boolean isAdultNitwit(Villager villager) {
		return villager.isAlive() && !villager.isBaby() && !villager.isPassenger()
				&& villager.getVillagerData().profession().is(VillagerProfession.NITWIT);
	}

	private static void update(ServerLevel level, Villager villager) {
		BlockPos feet = villager.blockPosition();
		if (!isEnclosed(level, feet)) {
			villager.removeAttached(PANIC);
			return;
		}
		int panic = villager.getAttachedOrElse(PANIC, 0) + 1;
		// Signes de panique : nuage d'orage au-dessus de la tête, protestations régulières
		level.sendParticles(ParticleTypes.ANGRY_VILLAGER, villager.getX(), villager.getY() + 2.1, villager.getZ(), 1, 0.2, 0.1, 0.2, 0.0);
		if (panic % 5 == 0) {
			level.playSound(null, feet, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.3F);
		}
		if (panic < PANIC_SECONDS) {
			villager.setAttached(PANIC, panic);
			return;
		}

		villager.removeAttached(PANIC);
		Long last = villager.getAttached(LAST_ADRENALINE);
		if (last == null || level.getGameTime() - last >= ADRENALINE_COOLDOWN) {
			villager.spawnAtLocation(level, new ItemStack(ModItems.ADRENALINE));
			villager.setAttached(LAST_ADRENALINE, level.getGameTime());
		}
		breakOut(level, villager, feet);
	}

	private static boolean isEnclosed(ServerLevel level, BlockPos feet) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (!blocksMovement(level, feet.relative(direction)) || !blocksMovement(level, feet.above().relative(direction))) {
				return false;
			}
		}
		return blocksMovement(level, feet.above(2));
	}

	private static boolean blocksMovement(ServerLevel level, BlockPos pos) {
		return !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
	}

	// Casse les 2 blocs (pieds + tête) d'un côté choisi au hasard parmi ceux où les deux sont cassables
	private static void breakOut(ServerLevel level, Villager villager, BlockPos feet) {
		List<Direction> directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
		Collections.shuffle(directions);
		for (Direction direction : directions) {
			BlockPos low = feet.relative(direction), high = low.above();
			if (isBreakable(level, low) && isBreakable(level, high)) {
				level.destroyBlock(low, true, villager);
				level.destroyBlock(high, true, villager);
				level.playSound(null, feet, SoundEvents.VILLAGER_HURT, SoundSource.NEUTRAL, 1.0F, 1.2F);
				return;
			}
		}
	}

	private static boolean isBreakable(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getDestroySpeed(level, pos) >= 0;
	}
}
