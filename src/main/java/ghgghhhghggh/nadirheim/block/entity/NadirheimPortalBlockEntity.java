package ghgghhhghggh.nadirheim.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// Block entity du portail de Nadirheim : ne stocke rien, sert juste à faire dessiner le bloc par son renderer.
// On hérite de celui du portail de l'End pour réutiliser son renderer de base (AbstractEndPortalRenderer).
public class NadirheimPortalBlockEntity extends TheEndPortalBlockEntity {
	public NadirheimPortalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.NADIRHEIM_PORTAL, pos, state);
	}

	// Les 6 faces, sauf celles cachées par un bloc voisin (comme la passerelle de l'End)
	@Override
	public boolean shouldRenderFace(Direction direction) {
		return level == null || Block.shouldRenderFace(getBlockState(), level.getBlockState(getBlockPos().relative(direction)), direction);
	}
}
