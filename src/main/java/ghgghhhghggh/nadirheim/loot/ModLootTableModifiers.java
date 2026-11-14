package ghgghhhghggh.nadirheim.loot;

import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;

// Ajoute le modèle d'amélioration en violinite dans les coffres des structures souterraines.
// Pour comparaison : le modèle en netherite a 1 chance sur 10 dans les coffres de bastion.
public class ModLootTableModifiers {
	// Coffre -> 1 chance sur N de contenir un modèle
	private static final Map<ResourceKey<LootTable>, Integer> TEMPLATE_CHESTS = Map.ofEntries(
			Map.entry(BuiltInLootTables.ANCIENT_CITY, 5),                // cité antique (en deepslate, zone la plus dangereuse)
			Map.entry(BuiltInLootTables.SIMPLE_DUNGEON, 10),             // donjon à spawner
			Map.entry(BuiltInLootTables.STRONGHOLD_CORRIDOR, 10),        // fort de l'End
			Map.entry(BuiltInLootTables.STRONGHOLD_CROSSING, 10),
			Map.entry(BuiltInLootTables.STRONGHOLD_LIBRARY, 10),
			Map.entry(BuiltInLootTables.ABANDONED_MINESHAFT, 20),        // mine abandonnée (très nombreuses, donc plus rare)
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR, 20),    // chambres des épreuves (beaucoup de coffres)
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE, 20),
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION, 20),
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL, 20),
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY, 20));

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			Integer oneIn = TEMPLATE_CHESTS.get(key);
			// isBuiltin() : on ne touche pas aux coffres redéfinis par un datapack
			if (oneIn == null || !source.isBuiltin()) {
				return;
			}

			// Même forme que les pools vanilla : 1 tirage, le modèle a un poids de 1 contre (N - 1) pour "rien"
			tableBuilder.withPool(LootPool.lootPool()
					.setRolls(ConstantValue.exactly(1.0F))
					.add(EmptyLootItem.emptyItem().setWeight(oneIn - 1))
					.add(LootItem.lootTableItem(ModItems.VIOLINITE_UPGRADE_SMITHING_TEMPLATE).setWeight(1)));
		});
	}
}
