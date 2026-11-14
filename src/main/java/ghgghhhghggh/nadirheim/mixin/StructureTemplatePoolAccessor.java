package ghgghhhghggh.nadirheim.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Donne accès à la liste privée des éléments d'un pool de structures (utilisé par ModVillageStructures)
@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccessor {
	@Accessor("templates")
	ObjectArrayList<StructurePoolElement> nadirheim$getTemplates();
}
