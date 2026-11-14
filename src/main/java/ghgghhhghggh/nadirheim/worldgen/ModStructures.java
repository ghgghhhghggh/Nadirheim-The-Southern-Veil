package ghgghhhghggh.nadirheim.worldgen;

import com.mojang.datafixers.util.Pair;
import ghgghhhghggh.nadirheim.Nadirheim;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

/*
 * Structures autonomes du mod (hors villages). Une structure = 3 éléments de données, générés par la datagen :
 * - template_pool : quel(s) fichier(s) .nbt poser (data/nadirheim/structure/...) et avec quels processeurs ;
 * - structure     : dans quels biomes, à quelle hauteur, comment s'adapter au terrain ;
 * - structure_set : à quelle fréquence (espacement en chunks).
 * Les biomes sont dans un tag (nadirheim:has_structure/<nom>), rempli par ModBiomeTagProvider.
 */
public class ModStructures {
	// Fontaine en ruine : simple décor posé au sol
	public static final ResourceKey<StructureTemplatePool> FONTAINE_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, Nadirheim.id("fontaine"));
	public static final ResourceKey<Structure> FONTAINE = ResourceKey.create(Registries.STRUCTURE, Nadirheim.id("fontaine"));
	public static final ResourceKey<StructureSet> FONTAINE_SET = ResourceKey.create(Registries.STRUCTURE_SET, Nadirheim.id("fontaine"));
	public static final TagKey<Biome> HAS_FONTAINE = TagKey.create(Registries.BIOME, Nadirheim.id("has_structure/fontaine"));

	public static void initialize() {
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Nadirheim.id("arid_biome_drying"), AridBiomeDryingProcessor.CODEC);
	}

	public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
		Holder<StructureTemplatePool> empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
		context.register(FONTAINE_POOL, new StructureTemplatePool(empty, List.of(
				Pair.of(StructurePoolElement.single(Nadirheim.id("fontaine").toString(),
						// Dans le désert et les badlands, la fontaine est à sec
						Holder.direct(new StructureProcessorList(List.of(AridBiomeDryingProcessor.INSTANCE)))), 1)
		), StructureTemplatePool.Projection.RIGID));
	}

	public static void bootstrapStructures(BootstrapContext<Structure> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
		context.register(FONTAINE, new JigsawStructure(
				new Structure.StructureSettings.Builder(biomes.getOrThrow(HAS_FONTAINE))
						.terrainAdapation(TerrainAdjustment.BEARD_THIN)   // comble le terrain sous la fontaine s'il est en pente
						.build(),
				pools.getOrThrow(FONTAINE_POOL),
				// Profondeur 1 minimum : avec 0, Minecraft n'ajoute même pas la pièce de départ (structure vide, jamais placée).
				// La fontaine n'a pas de bloc jigsaw, elle reste donc une seule pièce.
				1,
				// +1 : par défaut, la couche du bas de la structure REMPLACE le bloc de surface (comme le plancher des
				// maisons de village). La fontaine doit être posée SUR le sol, on la monte donc d'un bloc.
				ConstantHeight.of(VerticalAnchor.absolute(1)),
				false,
				Heightmap.Types.WORLD_SURFACE_WG));                   // posée à la surface du sol
	}

	public static void bootstrapStructureSets(BootstrapContext<StructureSet> context) {
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		// Une tentative tous les 45 chunks (au moins 15 chunks d'écart), seulement si le biome convient
		context.register(FONTAINE_SET, new StructureSet(structures.getOrThrow(FONTAINE),
				new RandomSpreadStructurePlacement(45, 15, RandomSpreadType.LINEAR, 20260926)));
	}
}
