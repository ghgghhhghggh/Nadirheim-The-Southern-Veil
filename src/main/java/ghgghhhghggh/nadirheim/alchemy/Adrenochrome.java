package ghgghhhghggh.nadirheim.alchemy;

import com.mojang.serialization.Codec;
import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.fluid.ModFluids;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Adrénochrome. Une source posée contient SOURCE_PARTS (4) parts ; une part = l'effet d'une fiole bue :
 * Régénération II 30 s + Boost de vie I 1 min 30 (de vrais cœurs en plus).
 * - Fiole vide sur la source : prend 1 part (4 fioles par source neuve, puis la source disparaît).
 * - Bain (une créature entre dans la source) : consomme D'UN COUP toutes les parts restantes et donne les effets
 *   multipliés par ce nombre (source neuve : Régénération II 2 min + Boost de vie I 6 min), puis la source disparaît.
 *   Les morts-vivants y prennent UNDEAD_DAMAGE_PER_PART dégâts par part (comme une potion de soin, qui les blesse).
 * - Les parts déjà prises sont gardées avec le tronçon de monde (chunk). Une source entamée ne se remet plus en seau
 *   (sinon on la remettrait à 4 parts en la reposant).
 */
public class Adrenochrome {
	public static final int REGENERATION_TICKS = 30 * 20;
	public static final int HEALTH_BOOST_TICKS = 90 * 20;
	public static final int SOURCE_PARTS = 4;
	private static final float UNDEAD_DAMAGE_PER_PART = 6.0F;

	// Parts déjà prises dans chaque source d'un chunk (clé = position en texte)
	private static final AttachmentType<Map<String, Integer>> PARTS_USED = AttachmentRegistry.createPersistent(
			Nadirheim.id("adrenochrome_parts_used"), Codec.unboundedMap(Codec.STRING, Codec.INT));

	// Effets d'une fiole
	public static List<MobEffectInstance> effects() {
		return effects(1);
	}

	// Effets de plusieurs parts : mêmes niveaux, durées multipliées
	public static List<MobEffectInstance> effects(int parts) {
		return List.of(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_TICKS * parts, 1),
				new MobEffectInstance(MobEffects.HEALTH_BOOST, HEALTH_BOOST_TICKS * parts, 0));
	}

	// Appelé quand une créature est dans un bloc de source (voir fluid/AdrenochromeFluid)
	public static void bathe(ServerLevel level, BlockPos pos, LivingEntity entity) {
		int parts = remainingParts(level, pos);
		if (entity.isInvertedHealAndHarm()) {
			entity.hurtServer(level, level.damageSources().magic(), UNDEAD_DAMAGE_PER_PART * parts);
		} else {
			for (MobEffectInstance effect : effects(parts)) {
				entity.addEffect(effect);
			}
		}
		level.playSound(null, pos, SoundEvents.GENERIC_DRINK.value(), SoundSource.NEUTRAL, 1.0F, 0.8F);
		removeSource(level, pos);
	}

	public static void initialize() {
		UseItemCallback.EVENT.register((player, level, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (!stack.is(Items.GLASS_BOTTLE) || player.isSpectator()) {
				return InteractionResult.PASS;
			}
			Vec3 eye = player.getEyePosition();
			Vec3 end = eye.add(player.getViewVector(1.0F).scale(player.blockInteractionRange()));
			BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
			if (hit.getType() != HitResult.Type.BLOCK) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			FluidState fluid = level.getFluidState(pos);
			if (!fluid.is(ModFluids.ADRENOCHROME) || !fluid.isSource() || !level.mayInteract(player, pos)) {
				return InteractionResult.PASS;
			}
			if (level instanceof ServerLevel serverLevel) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(ModItems.ADRENOCHROME)));
				takePart(serverLevel, pos);
			}
			return InteractionResult.SUCCESS;
		});
	}

	private static int partsUsed(LevelAccessor level, BlockPos pos) {
		return level.getChunk(pos) instanceof AttachmentTarget chunk
				? chunk.getAttachedOrElse(PARTS_USED, Map.<String, Integer>of()).getOrDefault(Long.toString(pos.asLong()), 0) : 0;
	}

	private static int remainingParts(LevelAccessor level, BlockPos pos) {
		return Math.max(1, SOURCE_PARTS - partsUsed(level, pos));
	}

	// Une fiole prend une part ; la dernière fait disparaître la source
	private static void takePart(ServerLevel level, BlockPos pos) {
		int used = partsUsed(level, pos) + 1;
		if (used >= SOURCE_PARTS) {
			removeSource(level, pos);
		} else {
			setPartsUsed(level, pos, used);
		}
	}

	private static void setPartsUsed(LevelAccessor level, BlockPos pos, int used) {
		ChunkAccess chunk = level.getChunk(pos);
		Map<String, Integer> uses = new HashMap<>(chunk.getAttachedOrElse(PARTS_USED, Map.of()));
		String posKey = Long.toString(pos.asLong());
		if (used <= 0) {
			uses.remove(posKey);
		} else {
			uses.put(posKey, used);
		}
		chunk.setAttached(PARTS_USED, uses.isEmpty() ? null : uses);
	}

	private static void removeSource(ServerLevel level, BlockPos pos) {
		setPartsUsed(level, pos, 0);
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.6F);
		level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.02);
	}

	// Source déjà entamée (au moins une fiole prise) ?
	public static boolean isUsed(LevelAccessor level, BlockPos pos) {
		return partsUsed(level, pos) > 0;
	}

	// Nouvelle source posée à cet endroit : elle repart avec ses 4 parts
	public static void resetSource(LevelAccessor level, BlockPos pos) {
		if (partsUsed(level, pos) > 0) {
			setPartsUsed(level, pos, 0);
		}
	}
}
