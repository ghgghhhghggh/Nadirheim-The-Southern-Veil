package ghgghhhghggh.nadirheim.client.datagen;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.item.ModMaterials;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

// Décrit l'apparence de l'armure portée (joueur, mobs, cheval, nautile) (même principe que EquipmentAssetProvider de vanilla).
// Textures (même disposition que l'armure netherite vanilla), dans textures/entity/equipment/ :
//   humanoid/violinite.png          (64x32)   -> casque, plastron, bottes
//   humanoid_leggings/violinite.png (64x32)   -> jambières
//   humanoid_baby/violinite.png     (64x64)   -> version bébé (bébés zombies...)
//   horse_body/violinite.png        (64x64)   -> armure de cheval
//   nautilus_body/violinite.png     (128x128) -> armure de nautile
public class ModEquipmentAssetProvider implements DataProvider {
	private final PackOutput.PathProvider pathProvider;

	public ModEquipmentAssetProvider(FabricPackOutput output) {
		this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> assets = Map.of(
				ModMaterials.VIOLINITE_EQUIPMENT_ASSET,
				EquipmentClientInfo.builder()
						.addHumanoidLayers(Nadirheim.id("violinite"))
						.addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Nadirheim.id("violinite"), false))
						.addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.leatherDyeable(Nadirheim.id("violinite"), false))
						.build());
		return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, pathProvider::json, assets);
	}

	@Override
	public String getName() {
		return "Equipment Asset Definitions";
	}
}
