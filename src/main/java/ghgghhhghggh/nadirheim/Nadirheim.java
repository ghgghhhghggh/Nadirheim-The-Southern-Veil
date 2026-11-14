package ghgghhhghggh.nadirheim;

import ghgghhhghggh.nadirheim.advancement.ModCriteria;
import ghgghhhghggh.nadirheim.alchemy.Adrenochrome;
import ghgghhhghggh.nadirheim.alchemy.NitwitPanic;
import ghgghhhghggh.nadirheim.alchemy.VioliniteOxidation;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.block.entity.ModBlockEntities;
import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import ghgghhhghggh.nadirheim.fluid.ModFluids;
import ghgghhhghggh.nadirheim.item.ModCreativeTabs;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.item.Mineral1Haste;
import ghgghhhghggh.nadirheim.item.VioliniteArmorSetBonus;
import ghgghhhghggh.nadirheim.loot.ModLootTableModifiers;
import ghgghhhghggh.nadirheim.menu.ModMenus;
import ghgghhhghggh.nadirheim.portal.PortalOpening;
import ghgghhhghggh.nadirheim.worldgen.DeepChargeBlasts;
import ghgghhhghggh.nadirheim.worldgen.ModOreGeneration;
import ghgghhhghggh.nadirheim.worldgen.ModStructures;
import ghgghhhghggh.nadirheim.worldgen.ModVillageStructures;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nadirheim implements ModInitializer {
	public static final String MOD_ID = "nadirheim";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();
		VioliniteArmorSetBonus.initialize();
		Mineral1Haste.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModMenus.initialize();
		ModCreativeTabs.initialize();
		ModOreGeneration.initialize();
		ModVillageStructures.initialize();
		ModStructures.initialize();
		ModLootTableModifiers.initialize();
		ModEnchantments.initialize();
		ModCriteria.initialize();
		DeepChargeBlasts.initialize();
		VioliniteOxidation.initialize();
		NitwitPanic.initialize();
		ModFluids.initialize();
		Adrenochrome.initialize();
		PortalOpening.initialize();

		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
