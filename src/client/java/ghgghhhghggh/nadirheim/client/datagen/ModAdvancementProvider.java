package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.advancement.ModCriteria;
import ghgghhhghggh.nadirheim.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/*
 * Progrès du mod, dans leur propre onglet. Pour réorganiser l'arbre, il suffit de changer les .parent(...) :
 * la progression des joueurs est liée à l'identifiant ("nadirheim:violinite"...), pas à la place dans l'arbre.
 * Ne pas renommer un identifiant existant, sinon les joueurs perdent ce progrès.
 * Textes : clés "advancements.nadirheim.<nom>.title/description" dans ModLanguageProvider.
 */
public class ModAdvancementProvider extends FabricAdvancementProvider {
	public ModAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateAdvancement(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
		// Racine de l'onglet : validée en silence au premier contact avec le mod (violinite ou Torpille sous l'eau),
		// ce qui fait apparaître l'onglet. Fond : la texture de la pierre glacée, répétée en mosaïque.
		AdvancementHolder root = Advancement.Builder.advancement()
				.display(ModItems.VIOLINITE, title("root"), description("root"),
						Nadirheim.id("block/icy_stone"),
						AdvancementType.TASK, false, false, false)
				.addCriterion("has_violinite", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VIOLINITE))
				.addCriterion("torpedo_arrow_hit", ModCriteria.torpedoArrowHit())
				.requirements(AdvancementRequirements.Strategy.OR)
				.save(consumer, "nadirheim:root");

		// Obtenir de la violinite pour la première fois
		Advancement.Builder.advancement()
				.parent(root)
				.display(ModItems.VIOLINITE, title("violinite"), description("violinite"), null,
						AdvancementType.TASK, true, true, false)
				.addCriterion("has_violinite", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VIOLINITE))
				.save(consumer, "nadirheim:violinite");

		// Toucher une créature avec une flèche Torpille sous l'eau
		Advancement.Builder.advancement()
				.parent(root)
				.display(Items.BOW, title("torpedo_arrow_hit"), description("torpedo_arrow_hit"), null,
						AdvancementType.TASK, true, true, false)
				.addCriterion("torpedo_arrow_hit", ModCriteria.torpedoArrowHit())
				.save(consumer, "nadirheim:torpedo_arrow_hit");
	}

	private static Component title(String name) {
		return Component.translatable("advancements.nadirheim." + name + ".title");
	}

	private static Component description(String name) {
		return Component.translatable("advancements.nadirheim." + name + ".description");
	}
}
