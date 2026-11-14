package ghgghhhghggh.nadirheim.client;

import ghgghhhghggh.nadirheim.block.entity.ModBlockEntities;
import ghgghhhghggh.nadirheim.client.color.XpOrbTint;
import ghgghhhghggh.nadirheim.fluid.ModFluids;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import ghgghhhghggh.nadirheim.client.renderer.ModRenderTypes;
import ghgghhhghggh.nadirheim.client.renderer.NadirheimPortalRenderer;
import ghgghhhghggh.nadirheim.client.renderer.PillarCapitalRenderer;
import ghgghhhghggh.nadirheim.client.screen.SieveScreen;
import ghgghhhghggh.nadirheim.item.Mineral1Haste;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.menu.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;

public class NadirheimClient implements ClientModInitializer {
	// Couleur de l'adrénochrome (rose-rouge violacé, comme sa vraie solution) : liquide posé et fiole
	public static final int ADRENOCHROME_COLOR = 0xFFD4418B;

	@Override
	public void onInitializeClient() {
		// Teinte "xp" (vert <-> jaune) utilisée par la pièce d'xp
		XpOrbTint.register();

		// Rendu de l'Inner Star flottant au-dessus des chapiteaux
		BlockEntityRenderers.register(ModBlockEntities.PILLAR_CAPITAL, PillarCapitalRenderer::new);

		// Portail de Nadirheim : shader maison (ModRenderTypes) dessiné par NadirheimPortalRenderer
		ModRenderTypes.initialize();

		// Adrénochrome : les textures de l'eau du jeu (lues dans ses fichiers, pas recopiées), teintées en rose-violet
		FluidRenderingRegistry.register(ModFluids.ADRENOCHROME, ModFluids.FLOWING_ADRENOCHROME, new FluidModel.Unbaked(
				new Material(Identifier.withDefaultNamespace("block/water_still")),
				new Material(Identifier.withDefaultNamespace("block/water_flow")),
				new Material(Identifier.withDefaultNamespace("block/water_overlay")),
				BlockTintSources.constant(ADRENOCHROME_COLOR)));
		BlockEntityRenderers.register(ModBlockEntities.NADIRHEIM_PORTAL, context -> new NadirheimPortalRenderer());

		// Fenêtres des blocs utilitaires
		MenuScreens.register(ModMenus.SIEVE, SieveScreen::new);

		// Affiche les bonus de respiration (VioliniteArmorSetBonus, NautilusMixin) dans l'infobulle des armures
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			if (stack.is(ModItems.VIOLINITE_HELMET) || stack.is(ModItems.VIOLINITE_CHESTPLATE)
					|| stack.is(ModItems.VIOLINITE_LEGGINGS) || stack.is(ModItems.VIOLINITE_BOOTS)) {
				lines.add(Component.translatable("tooltip.nadirheim.violinite_armor_set_bonus").withStyle(ChatFormatting.BLUE));
			} else if (stack.is(ModItems.VIOLINITE_HORSE_ARMOR)) {
				lines.add(Component.translatable("tooltip.nadirheim.violinite_horse_armor_bonus").withStyle(ChatFormatting.BLUE));
			} else if (stack.is(ModItems.VIOLINITE_NAUTILUS_ARMOR)) {
				lines.add(Component.translatable("tooltip.nadirheim.violinite_nautilus_armor_bonus").withStyle(ChatFormatting.BLUE));
			} else if (Mineral1Haste.isMineral1Tool(stack)) {
				lines.add(Component.translatable("tooltip.nadirheim.mineral1_tool_haste").withStyle(ChatFormatting.BLUE));
			}
		});
	}
}
