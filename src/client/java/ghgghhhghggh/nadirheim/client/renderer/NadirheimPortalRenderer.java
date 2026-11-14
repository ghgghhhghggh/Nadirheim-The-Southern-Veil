package ghgghhhghggh.nadirheim.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import ghgghhhghggh.nadirheim.block.entity.NadirheimPortalBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

// Dessine le portail de Nadirheim : une boîte dont les faces visibles utilisent le shader du portail
// (voir ModRenderTypes et assets/nadirheim/shaders/core/nadirheim_portal.fsh). Comme le motif est accroché à
// l'écran, des morceaux de portail de tailles différentes se raccordent sans couture.
public class NadirheimPortalRenderer extends AbstractEndPortalRenderer<NadirheimPortalBlockEntity, EndPortalRenderState> {
	// TEST de taille : chaque bloc dessine le portail SIDE_EXTENSION de bloc plus loin sur ses 4 côtés
	// (10 pixels sur 16). Avec 2 blocs empilés : un portail de 2,25 x 2,25 de large et 2 de haut.
	private static final float SIDE_EXTENSION = 10.0F / 16.0F;

	@Override
	public EndPortalRenderState createRenderState() {
		return new EndPortalRenderState();
	}

	@Override
	public void submit(EndPortalRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(-SIDE_EXTENSION, 0.0F, -SIDE_EXTENSION);
		poseStack.scale(1.0F + 2.0F * SIDE_EXTENSION, 1.0F, 1.0F + 2.0F * SIDE_EXTENSION);
		submitCube(state.facesToShow, ModRenderTypes.NADIRHEIM_PORTAL, poseStack, submitNodeCollector);
		poseStack.popPose();
	}

	// Le portail dépasse de son bloc : on le dessine même quand le bloc lui-même est hors de l'écran
	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
