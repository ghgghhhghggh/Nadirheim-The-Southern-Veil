package ghgghhhghggh.nadirheim.worldgen;

import com.mojang.serialization.MapCodec;
import ghgghhhghggh.nadirheim.Nadirheim;
import ghgghhhghggh.nadirheim.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

/*
 * "Processeur" appliqué aux maisons du nitwit au moment où elles sont posées dans un village :
 * - les tonneaux reçoivent une table de butin (contenu tiré au hasard à la première ouverture) ;
 * - une des bibliothèques sculptées, tirée au hasard, reçoit un livre Torpille I, à coup sûr.
 * finalizeProcessing voit toute la maison d'un coup, ce qui permet de choisir UNE bibliothèque parmi toutes.
 */
public class NitwitHouseProcessor implements StructureProcessor {
	public static final NitwitHouseProcessor INSTANCE = new NitwitHouseProcessor();
	public static final MapCodec<NitwitHouseProcessor> CODEC = MapCodec.unit(INSTANCE);

	public static final ResourceKey<LootTable> BARREL_LOOT =
			ResourceKey.create(Registries.LOOT_TABLE, Nadirheim.id("chests/nitwit_house_barrel"));

	@Override
	public MapCodec<? extends StructureProcessor> codec() {
		return CODEC;
	}

	@Override
	public List<StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level, BlockPos position, BlockPos referencePos,
			List<StructureBlockInfo> originalBlockInfoList, List<StructureBlockInfo> processedBlockInfoList, StructurePlaceSettings settings) {
		RandomSource random = settings.getRandom(position);
		List<StructureBlockInfo> result = new ArrayList<>(processedBlockInfoList);

		List<Integer> bookshelves = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			StructureBlockInfo info = result.get(i);
			if (info.state().is(Blocks.BARREL)) {
				CompoundTag nbt = info.nbt() == null ? new CompoundTag() : info.nbt().copy();
				nbt.putString("LootTable", BARREL_LOOT.identifier().toString());
				nbt.putLong("LootTableSeed", random.nextLong());
				result.set(i, new StructureBlockInfo(info.pos(), info.state(), nbt));
			} else if (info.state().is(Blocks.CHISELED_BOOKSHELF)) {
				bookshelves.add(i);
			}
		}

		if (!bookshelves.isEmpty()) {
			int index = bookshelves.get(random.nextInt(bookshelves.size()));
			result.set(index, withTorpedoBook(level, result.get(index), random));
		}
		return result;
	}

	// Range un livre Torpille I dans un emplacement au hasard de la bibliothèque (et affiche le livre sur le bloc)
	private static StructureBlockInfo withTorpedoBook(ServerLevelAccessor level, StructureBlockInfo info, RandomSource random) {
		Holder<Enchantment> torpedo = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.TORPEDO);
		ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(torpedo, 1));
		int slot = random.nextInt(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size());

		Tag bookTag = ItemStackWithSlot.CODEC
				.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), new ItemStackWithSlot(slot, book))
				.getOrThrow();
		ListTag items = new ListTag();
		items.add(bookTag);
		CompoundTag nbt = info.nbt() == null ? new CompoundTag() : info.nbt().copy();
		nbt.put("Items", items);

		BlockState state = info.state().setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot), true);
		return new StructureBlockInfo(info.pos(), state, nbt);
	}
}
