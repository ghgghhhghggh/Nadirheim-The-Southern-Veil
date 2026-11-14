package ghgghhhghggh.nadirheim.worldgen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.mixin.StructureTemplatePoolAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * Ajoute la maison du nitwit dans les villages vanilla.
 *
 * Un village est construit morceau par morceau : chaque rue qui ne peut plus s'étendre se termine par un petit
 * morceau pioché dans la liste "village/<type>/terminators" (les fins de chemin). On ajoute notre maison dans
 * cette liste : elle ne peut donc apparaître qu'au bout d'un chemin, en bordure du village.
 * Son bloc jigsaw (réglé comme celui des fins de rue vanilla) la raccorde au chemin.
 *
 * Fabric n'a pas d'outil pour modifier ces listes : on les complète au démarrage du serveur,
 * avant que le monde ne soit généré (voir StructureTemplatePoolAccessor).
 * Structures : data/nadirheim/structure/nitwit_house_<type>.nbt
 *
 * Processeurs (règles appliquées au moment de poser les blocs) :
 * - ceux des rues de son village : le seuil en chemin de terre devant la porte devient un ponton en planches
 *   (chêne, acacia ou sapin) s'il tombe sur de l'eau, comme les rues vanilla ;
 * - NitwitHouseProcessor : butin des tonneaux et livre Torpille dans une bibliothèque sculptée.
 *
 * IGNORE_WATERLOGGING : par défaut, un bloc posé là où il y avait de l'eau garde l'eau s'il peut être immergé
 * (échelle, trappe, lanterne...). Une maison posée au bord de l'eau aurait alors sa cave inondée.
 */
public class ModVillageStructures {
	// Chance qu'une fin de chemin devienne la maison du nitwit (si elle a la place, sinon une fin de rue normale).
	// Un village a environ 10 fins de chemin : 0.007 (0,7 %) donne une maison dans ~1 village sur 15.
	public static final double CHANCE_PER_STREET_END = 0.007;

	// Type de village -> liste de processeurs de ses rues (le désert n'en a pas)
	private static final Map<String, Optional<String>> VILLAGE_TYPES = Map.of(
			"plains", Optional.of("street_plains"),
			"desert", Optional.empty(),
			"savanna", Optional.of("street_savanna"),
			"taiga", Optional.of("street_snowy_or_taiga"),
			"snowy", Optional.of("street_snowy_or_taiga"));

	public static void initialize() {
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Nadirheim.id("nitwit_house"), NitwitHouseProcessor.CODEC);
		ServerLifecycleEvents.SERVER_STARTING.register(ModVillageStructures::addNitwitHouses);
	}

	private static void addNitwitHouses(MinecraftServer server) {
		Registry<StructureTemplatePool> pools = server.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
		Registry<StructureProcessorList> processorLists = server.registryAccess().lookupOrThrow(Registries.PROCESSOR_LIST);

		VILLAGE_TYPES.forEach((type, streetProcessors) -> {
			ResourceKey<StructureTemplatePool> key = ResourceKey.create(Registries.TEMPLATE_POOL,
					Identifier.withDefaultNamespace("village/" + type + "/terminators"));
			pools.get(key).ifPresentOrElse(pool -> {
				List<StructureProcessor> processors = new ArrayList<>();
				streetProcessors
						.flatMap(name -> processorLists.get(ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace(name))))
						.ifPresent(street -> processors.addAll(street.value().list()));
				processors.add(NitwitHouseProcessor.INSTANCE);

				StructurePoolElement house = StructurePoolElement.single(Nadirheim.id("nitwit_house_" + type).toString(),
								Holder.direct(new StructureProcessorList(processors)), LiquidSettings.IGNORE_WATERLOGGING)
						.apply(StructureTemplatePool.Projection.RIGID);
				addWithChance(((StructureTemplatePoolAccessor) pool.value()).nadirheim$getTemplates(), house, CHANCE_PER_STREET_END);
			}, () -> Nadirheim.LOGGER.warn("Pool de village introuvable : {}", key.identifier()));
		});
	}

	// Le jeu choisit un élément au hasard dans la liste : pour régler la chance, on répète
	// soit les éléments vanilla (maison rare), soit la maison (maison fréquente).
	private static void addWithChance(ObjectArrayList<StructurePoolElement> templates, StructurePoolElement house, double chance) {
		List<StructurePoolElement> vanilla = new ArrayList<>(templates);
		int vanillaCount = vanilla.size();
		if (chance >= 1.0 / (vanillaCount + 1)) {
			long copies = Math.max(1, Math.round(chance * vanillaCount / (1.0 - chance)));
			for (int i = 0; i < copies; i++) {
				templates.add(house);
			}
		} else {
			long vanillaRepeats = Math.max(1, Math.round((1.0 - chance) / (chance * vanillaCount)));
			for (int i = 1; i < vanillaRepeats; i++) {
				templates.addAll(vanilla);
			}
			templates.add(house);
		}
	}
}
