package io.github.dueris.calio.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({TagEntry.class})
public interface TagEntryAccessor {
	@Invoker
	ExtraCodecs.TagOrElementLocation callElementOrTag();

	@Accessor
	ResourceLocation getId();

	@Accessor
	boolean isTag();

	@Accessor
	boolean isRequired();
}
