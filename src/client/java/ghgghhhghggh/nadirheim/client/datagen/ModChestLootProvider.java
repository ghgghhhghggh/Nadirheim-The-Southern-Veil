package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.block.ModBlocks;
import ghgghhhghggh.nadirheim.item.ModItems;
import ghgghhhghggh.nadirheim.worldgen.NitwitHouseProcessor;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

// Butin des coffres/tonneaux des structures du mod (le butin ajouté aux coffres vanilla, lui, est dans ModLootTableModifiers)
public class ModChestLootProvider extends SimpleFabricLootTableSubProvider {
	public ModChestLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup, LootContextParamSets.CHEST);
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		// Tonneaux du sous-sol de la maison du nitwit (x2 par maison). Chances données par tonneau.
		output.accept(NitwitHouseProcessor.BARREL_LOOT, LootTable.lootTable()
				// Objets courants : 3 à 5 tirages
				.withPool(LootPool.lootPool()
						.setRolls(UniformGenerator.between(3.0F, 5.0F))
						.add(LootItem.lootTableItem(Items.CHARCOAL).setWeight(10)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F))))
						.add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(8)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
						.add(LootItem.lootTableItem(Items.PAPER).setWeight(8)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
						.add(LootItem.lootTableItem(ModBlocks.UNDERWATER_TORCH_ITEM).setWeight(6)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))))
				.withPool(chance(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE, 6))   // 1 sur 6 (~17 %) : peu commun
				.withPool(chance(Items.DIAMOND, 10))                                  // 1 sur 10 (10 %) : rare
				.withPool(chance(ModItems.VIOLINITE, 20)));                           // 1 sur 20 (5 %) : très rare
	}

	// Un tirage : l'objet a 1 chance sur "oneIn" de sortir (sinon rien)
	private static LootPool.Builder chance(ItemLike item, int oneIn) {
		return LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(EmptyLootItem.emptyItem().setWeight(oneIn - 1))
				.add(LootItem.lootTableItem(item).setWeight(1));
	}
}
