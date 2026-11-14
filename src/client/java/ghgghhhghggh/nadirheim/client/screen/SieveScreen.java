package ghgghhhghggh.nadirheim.client.screen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.menu.SieveMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/*
 * Affichage de la fenêtre du tamis : fond (textures/gui/container/sieve.png, 176x166 comme le four)
 * + flèche de progression blanche (textures/gui/sprites/container/sieve/sift_progress.png, 22x15),
 * de la même forme que la flèche grise du fond, qui se remplit de gauche à droite.
 */
public class SieveScreen extends AbstractContainerScreen<SieveMenu> {
	private static final Identifier TEXTURE = Nadirheim.id("textures/gui/container/sieve.png");
	private static final Identifier PROGRESS_SPRITE = Nadirheim.id("container/sieve/sift_progress");
	public static final int ARROW_X = 60, ARROW_Y = 35, ARROW_WIDTH = 22, ARROW_HEIGHT = 15;

	public SieveScreen(SieveMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void init() {
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
		int progressWidth = Mth.ceil(menu.getProgress() * ARROW_WIDTH);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE, ARROW_WIDTH, ARROW_HEIGHT, 0, 0,
				leftPos + ARROW_X, topPos + ARROW_Y, progressWidth, ARROW_HEIGHT);
	}
}
