package ghgghhhghggh.nadirheim.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

// Modes de rendu propres au mod (shader + textures)
public class ModRenderTypes {
	// Textures du portail : le fond et la couche de particules (points clairs sur fond noir)
	public static final Identifier PORTAL_SKY = Nadirheim.id("textures/environment/nadirheim_sky.png");
	public static final Identifier PORTAL_PARTICLES = Nadirheim.id("textures/entity/nadirheim_portal/nadirheim_portal.png");

	// Même base que le portail de l'End vanilla (sommets sans texture ni couleur, 2 textures, brouillard),
	// mais avec notre shader : assets/nadirheim/shaders/core/nadirheim_portal.vsh / .fsh
	public static final RenderPipeline NADIRHEIM_PORTAL_PIPELINE = RenderPipelines.register(
			RenderPipeline.builder(RenderPipelines.END_PORTAL_SNIPPET)
					.withLocation(Nadirheim.id("pipeline/nadirheim_portal"))
					.withVertexShader(Nadirheim.id("core/nadirheim_portal"))
					.withFragmentShader(Nadirheim.id("core/nadirheim_portal"))
					.withShaderDefine("PORTAL_LAYERS", 15)
					.build());

	public static final RenderType NADIRHEIM_PORTAL = RenderType.create("nadirheim_portal",
			RenderSetup.builder(NADIRHEIM_PORTAL_PIPELINE)
					.withTexture("Sampler0", PORTAL_SKY)
					.withTexture("Sampler1", PORTAL_PARTICLES)
					.createRenderSetup());

	public static void initialize() {
		// Charge la classe tôt (avant le chargement des ressources) pour que le shader soit compilé avec les autres
	}
}
