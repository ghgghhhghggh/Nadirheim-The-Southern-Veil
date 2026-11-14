package ghgghhhghggh.nadirheim.fluid;

import ghgghhhghggh.nadirheim.Nadirheim;
import net.fabricmc.fabric.api.registry.fluid.EntityFluidInteractionRegistry;
import net.fabricmc.fabric.api.registry.fluid.FluidBehavior;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

// Liquides du mod. Chaque liquide a deux formes : la source (bloc plein) et l'écoulement.
// Son bloc est dans ModBlocks, son seau dans ModItems, son rendu (textures, couleur) côté client dans NadirheimClient.
public class ModFluids {
	public static final FlowingFluid FLOWING_ADRENOCHROME = register("flowing_adrenochrome", new AdrenochromeFluid.Flowing());
	public static final FlowingFluid ADRENOCHROME = register("adrenochrome", new AdrenochromeFluid.Source());

	// Tag "nadirheim:adrenochrome" (les deux formes, écrit par la datagen) : sert à donner la nage aux créatures
	public static final TagKey<Fluid> ADRENOCHROME_TAG = TagKey.create(Registries.FLUID, Nadirheim.id("adrenochrome"));

	private static <T extends Fluid> T register(String name, T fluid) {
		return Registry.register(BuiltInRegistries.FLUID, Nadirheim.id(name), fluid);
	}

	public static void initialize() {
		// On nage dans l'adrénochrome comme dans l'eau (mais on ne s'y noie pas : ce n'est pas de l'eau)
		EntityFluidInteractionRegistry.register(ADRENOCHROME_TAG, FluidBehavior.WATER_LIKE);
	}
}
