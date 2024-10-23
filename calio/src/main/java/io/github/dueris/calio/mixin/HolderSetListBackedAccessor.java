package io.github.dueris.calio.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin({HolderSet.ListBacked.class})
public interface HolderSetListBackedAccessor<E> {
	@Invoker
	List<Holder<E>> callContents();
}
