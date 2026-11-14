package ghgghhhghggh.nadirheim.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

// Processeur : dans le désert et les badlands, retire l'eau des blocs immergés (fontaine à sec).
public class AridBiomeDryingProcessor implements StructureProcessor {
	public static final AridBiomeDryingProcessor INSTANCE = new AridBiomeDryingProcessor();
	public static final MapCodec<AridBiomeDryingProcessor> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public MapCodec<? extends StructureProcessor> codec() {
		return CODEC;
	}

	@Override
	public StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos,
			StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
		if (processedBlockInfo.state().hasProperty(BlockStateProperties.WATERLOGGED)
				&& processedBlockInfo.state().getValue(BlockStateProperties.WATERLOGGED)) {
			Holder<Biome> biome = level.getBiome(processedBlockInfo.pos());
			if (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_BADLANDS)) {
				return new StructureBlockInfo(processedBlockInfo.pos(),
						processedBlockInfo.state().setValue(BlockStateProperties.WATERLOGGED, false), processedBlockInfo.nbt());
			}
		}
		return processedBlockInfo;
	}
}
