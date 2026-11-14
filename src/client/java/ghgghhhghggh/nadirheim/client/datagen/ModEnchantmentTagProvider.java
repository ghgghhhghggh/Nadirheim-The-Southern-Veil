package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagProvider extends FabricTagsProvider<Enchantment> {
	public ModEnchantmentTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, Registries.ENCHANTMENT, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// "Non trésor" = enchantement normal : table d'enchantement, livres des bibliothécaires,
		// butin aléatoire et équipement des mobs (comme Puissance ou Perforation)
		builder(EnchantmentTags.NON_TREASURE).add(ModEnchantments.TORPEDO);
	}
}
