package ghgghhhghggh.nadirheim.block.entity;

import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

// Types de "block entities" du mod (blocs qui ont besoin de données ou d'un rendu spécial)
public class ModBlockEntities {
	public static final BlockEntityType<PillarCapitalBlockEntity> PILLAR_CAPITAL = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Nadirheim.id("pillar_capital"),
			FabricBlockEntityTypeBuilder.create(PillarCapitalBlockEntity::new, ModBlocks.PILLAR_CAPITAL).build());

	public static final BlockEntityType<SieveBlockEntity> SIEVE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Nadirheim.id("sieve"),
			FabricBlockEntityTypeBuilder.create(SieveBlockEntity::new, ModBlocks.SIEVE).build());

	public static final BlockEntityType<NadirheimPortalBlockEntity> NADIRHEIM_PORTAL = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Nadirheim.id("nadirheim_portal"),
			FabricBlockEntityTypeBuilder.create(NadirheimPortalBlockEntity::new, ModBlocks.NADIRHEIM_PORTAL).build());

	public static void initialize() {
		// Charge la classe (et donc enregistre les types ci-dessus)
	}
}
