package ghgghhhghggh.nadirheim.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Aucune donnée : sert seulement à dessiner l'Inner Star au-dessus du chapiteau (voir PillarCapitalRenderer)
public class PillarCapitalBlockEntity extends BlockEntity {
	public PillarCapitalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PILLAR_CAPITAL, pos, state);
	}
}
