package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.power.type.ModifyTypeTagPower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin<T> {

	@Shadow
	@Final
	private String directory;

	@Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
	private void apoli$rebuildTagsInTags(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> cir, @Local TagEntry.Lookup<T> valueGetter, @Local DependencySorter<ResourceLocation, DependencySorter.Entry<ResourceLocation>> dependencyTracker) {
		ModifyTypeTagPower.setTagCache(directory, valueGetter, dependencyTracker);
	}

}
