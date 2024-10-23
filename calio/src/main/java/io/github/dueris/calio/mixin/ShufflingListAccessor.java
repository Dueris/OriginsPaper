package io.github.dueris.calio.mixin;

import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShufflingList.class)
public interface ShufflingListAccessor<E> {

	@Final
	@Accessor
	List<ShufflingList.WeightedEntry<E>> getEntries();

}
