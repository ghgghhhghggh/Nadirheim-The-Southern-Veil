package ghgghhhghggh.nadirheim.block;

import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/*
 * Champignon de glace : un champignon vanilla (propagation lente, poudre d'os -> champignon géant),
 * mais qui ne pousse que sur la neige et la glace, et sans condition de luminosité
 * (les champignons vanilla, eux, ont besoin de pénombre sauf sur mycélium/podzol).
 */
public class IceMushroomBlock extends MushroomBlock {
	// Blocs sur lesquels il peut être posé (rempli par la datagen : bloc de neige, glace, glace compactée, glace bleue)
	public static final TagKey<Block> PLACEABLE_ON = TagKey.create(Registries.BLOCK, Nadirheim.id("ice_mushroom_placeable_on"));

	// Champignon de glace géant, pour la poudre d'os : n'existe pas encore, la poudre d'os n'a donc aucun effet
	public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_ICE_MUSHROOM =
			ResourceKey.create(Registries.CONFIGURED_FEATURE, Nadirheim.id("huge_ice_mushroom"));

	public IceMushroomBlock(BlockBehaviour.Properties properties) {
		super(HUGE_ICE_MUSHROOM, properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(PLACEABLE_ON);
	}

	// Vérifié à la pose, quand un bloc voisin change (il casse si on retire la glace dessous) et pendant la propagation
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos belowPos = pos.below();
		return mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
	}
}
