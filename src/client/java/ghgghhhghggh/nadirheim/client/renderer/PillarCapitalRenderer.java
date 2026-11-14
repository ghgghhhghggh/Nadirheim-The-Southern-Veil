package ghgghhhghggh.nadirheim.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ghgghhhghggh.nadirheim.block.PillarCapitalBlock;
import ghgghhhghggh.nadirheim.block.entity.PillarCapitalBlockEntity;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/*
 * Dessine l'Inner Star au-dessus d'un chapiteau qui en porte un : l'astre flotte (monte et descend doucement)
 * et tourne sur lui-même, en pleine lumière (il brille même dans le noir).
 * Fonctionne en deux temps, comme tous les rendus 26.x : extractRenderState copie ce qu'il faut depuis le monde,
 * submit dessine à partir de ces données.
 */
public class PillarCapitalRenderer implements BlockEntityRenderer<PillarCapitalBlockEntity, PillarCapitalRenderer.State> {
	// Taille de l'astre : le modèle "bloc" est déjà réduit de moitié par l'affichage FIXED, donc 1.125 -> 9 pixels
	private static final float SIZE = 1.125F;
	private static final float HEIGHT = 1.5F;            // hauteur de son centre au-dessus du bas du chapiteau
	private static final float BOB_AMPLITUDE = 0.08F;    // amplitude de la flottaison
	private static final float DEGREES_PER_TICK = 2.0F;  // vitesse de rotation (un tour en 9 secondes)

	private final ItemModelResolver itemModelResolver;

	public PillarCapitalRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	public static class State extends BlockEntityRenderState {
		public final ItemStackRenderState star = new ItemStackRenderState();
		public boolean hasStar;
		public float time;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(PillarCapitalBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.hasStar = blockEntity.getBlockState().getValue(PillarCapitalBlock.HAS_STAR);
		if (state.hasStar) {
			// Décalage selon la position : les 4 astres de la salle ne bougent pas tous en même temps
			long offset = blockEntity.getBlockPos().asLong() % 100;
			state.time = blockEntity.getLevel() == null ? 0 : (blockEntity.getLevel().getGameTime() + offset) + partialTicks;
			itemModelResolver.updateForTopItem(state.star, new ItemStack(ModItems.INNER_STAR), ItemDisplayContext.FIXED,
					blockEntity.getLevel(), null, 0);
		}
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (!state.hasStar || state.star.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, HEIGHT + Mth.sin(state.time * 0.08F) * BOB_AMPLITUDE, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(state.time * DEGREES_PER_TICK));
		poseStack.scale(SIZE, SIZE, SIZE);
		state.star.submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	// L'astre dépasse du bloc : il doit rester visible même si seul le haut de l'astre est à l'écran
	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}
}
