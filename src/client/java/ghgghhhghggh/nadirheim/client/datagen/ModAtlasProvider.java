package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/*
 * Les ornements ne sont pas dessinés couleur par couleur : Minecraft prend chaque motif (en niveaux de gris)
 * et le recolore avec la palette de chaque matériau pour créer les textures dans un "atlas".
 * On ajoute donc nos palettes violinite aux deux atlas qui s'en servent (même principe que AtlasProvider de vanilla) :
 * - minecraft:armor_trims : les ornements sur l'armure portée ;
 * - minecraft:items       : les ornements sur les icônes d'armure dans l'inventaire.
 * Les fichiers d'atlas de plusieurs mods/packs s'additionnent, on n'écrase donc pas ceux de vanilla.
 */
public class ModAtlasProvider implements DataProvider {
	private static final Identifier TRIM_PALETTE_KEY = Identifier.withDefaultNamespace("trims/color_palettes/trim_palette");

	// Nom utilisé dans les textures -> notre palette (textures/trims/color_palettes/<nom>.png)
	private static final Map<String, Identifier> VIOLINITE_PALETTES = Map.of(
			ModMaterials.VIOLINITE_TRIM_ASSETS.base().suffix(), Nadirheim.id("trims/color_palettes/violinite"),
			ModMaterials.VIOLINITE_TRIM_ASSETS.assetId(ModMaterials.VIOLINITE_EQUIPMENT_ASSET).suffix(), Nadirheim.id("trims/color_palettes/violinite_darker"));

	// Les 18 motifs vanilla (même liste que AtlasProvider.VANILLA_PATTERNS)
	private static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS = List.of(
			TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD,
			TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB,
			TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE,
			TrimPatterns.RAISER, TrimPatterns.HOST, TrimPatterns.FLOW, TrimPatterns.BOLT);

	private final PackOutput.PathProvider pathProvider;

	public ModAtlasProvider(FabricPackOutput output) {
		this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
	}

	// Textures des motifs sur l'armure portée : trims/entity/humanoid/<motif> et trims/entity/humanoid_leggings/<motif>
	private static List<Identifier> patternTextures() {
		List<Identifier> result = new ArrayList<>();
		for (ResourceKey<TrimPattern> pattern : VANILLA_PATTERNS) {
			Identifier assetId = TrimPatterns.defaultAssetId(pattern);
			for (EquipmentClientInfo.LayerType layer : List.of(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)) {
				result.add(assetId.withPath(path -> layer.trimAssetPrefix() + "/" + path));
			}
		}
		return result;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		List<Identifier> itemTrimTextures = List.of(
				ItemModelGenerators.TRIM_PREFIX_HELMET, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE,
				ItemModelGenerators.TRIM_PREFIX_LEGGINGS, ItemModelGenerators.TRIM_PREFIX_BOOTS);

		return CompletableFuture.allOf(
				storeAtlas(cache, AtlasIds.ARMOR_TRIMS, List.of(new PalettedPermutations(patternTextures(), TRIM_PALETTE_KEY, VIOLINITE_PALETTES))),
				storeAtlas(cache, AtlasIds.ITEMS, List.of(new PalettedPermutations(itemTrimTextures, TRIM_PALETTE_KEY, VIOLINITE_PALETTES))));
	}

	private CompletableFuture<?> storeAtlas(CachedOutput cache, Identifier atlasId, List<SpriteSource> contents) {
		return DataProvider.saveStable(cache, SpriteSources.FILE_CODEC, contents, pathProvider.json(atlasId));
	}

	@Override
	public String getName() {
		return "Atlas Definitions";
	}
}
