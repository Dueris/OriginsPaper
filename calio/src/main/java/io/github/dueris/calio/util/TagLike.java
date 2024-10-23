package io.github.dueris.calio.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.dueris.calio.data.exceptions.DataException;
import io.github.dueris.calio.mixin.HolderSetListBackedAccessor;
import io.github.dueris.calio.mixin.TagEntryAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TagLike<T> {

	private final ResourceKey<? extends Registry<T>> registryRef;
	private final ImmutableSet<TagEntry> tagEntries;

	private final ImmutableMap<ResourceKey<T>, T> elementsByKey;
	private final ImmutableMap<TagKey<T>, Collection<T>> elementsByTag;

	protected TagLike(ResourceKey<? extends Registry<T>> registryRef, Map<ResourceKey<T>, T> elementsByKey, Map<TagKey<T>, Collection<T>> elementsByTag, Collection<TagEntry> tagEntries) {
		this.registryRef = registryRef;
		this.elementsByKey = ImmutableMap.copyOf(elementsByKey);
		this.elementsByTag = ImmutableMap.copyOf(elementsByTag);
		this.tagEntries = ImmutableSet.copyOf(tagEntries);
	}

	public static <T> @NotNull Builder<T> builder(ResourceKey<? extends Registry<T>> registryRef) {
		return new Builder<>(registryRef);
	}

	public static <T> @NotNull Builder<T> builder(ResourceKey<? extends Registry<T>> registryRef, Collection<TagEntry> tagEntries) {
		return new Builder<>(registryRef, tagEntries);
	}

	public ImmutableSet<TagEntry> entries() {
		return tagEntries;
	}

	public boolean contains(@NotNull T element) {
		return elementsByKey.containsValue(element)
			|| elementsByTag.values().stream().flatMap(Collection::stream).anyMatch(element::equals);
	}

	public static class Builder<E> {

		private final ResourceKey<? extends Registry<E>> registryRef;
		private final Set<TagEntry> tagEntries;

		public Builder(ResourceKey<? extends Registry<E>> registryRef) {
			this.registryRef = registryRef;
			this.tagEntries = new ObjectOpenHashSet<>();
		}

		public Builder(@NotNull TagLike<E> tagLike) {
			this.registryRef = tagLike.registryRef;
			this.tagEntries = new ObjectOpenHashSet<>(tagLike.entries());
		}

		public Builder(ResourceKey<? extends Registry<E>> registryRef, Collection<TagEntry> tagEntries) {
			this.registryRef = registryRef;
			this.tagEntries = new ObjectOpenHashSet<>(tagEntries);
		}

		public Builder<E> add(TagEntry tagEntry) {
			this.tagEntries.add(tagEntry);
			return this;
		}

		public Builder<E> addAll(Collection<TagEntry> tagEntries) {
			this.tagEntries.addAll(tagEntries);
			return this;
		}

		public Builder<E> addAll(@NotNull Builder<E> other) {
			return this.addAll(other.tagEntries);
		}

		public Builder<E> clear() {
			this.tagEntries.clear();
			return this;
		}

		@SuppressWarnings("unchecked")
		public TagLike<E> build(@NotNull HolderGetter<E> entryLookup) {

			Map<ResourceKey<E>, E> elementsByKey = new HashMap<>();
			Map<TagKey<E>, Collection<E>> elementsByTag = new HashMap<>();

			Set<TagEntry> tagEntries = new HashSet<>();
			for (TagEntry tagEntry : this.tagEntries) {

				TagEntryAccessor tagEntryAccess = (TagEntryAccessor) tagEntry;

				ResourceLocation id = tagEntryAccess.getId();
				int index = tagEntries.size();

				boolean isTag = tagEntryAccess.isTag();
				boolean required = tagEntryAccess.isRequired();

				if (isTag) {

					TagKey<E> tag = TagKey.create(registryRef, id);
					Optional<HolderSet.Named<E>> entries = entryLookup.get(tag);

					if (required && entries.isEmpty()) {
						throw new DataException(DataException.Phase.READING, index, "Tag \"" + id + "\" for registry \"" + registryRef.location() + "\" doesn't exist!");
					} else if (entries.isPresent()) {
						Collection<E> elements = elementsByTag.computeIfAbsent(tag, k -> new ObjectOpenHashSet<>());
						entries.stream()
							.map(registryEntries -> ((HolderSetListBackedAccessor<E>) registryEntries).callContents())
							.flatMap(Collection::stream)
							.map(Holder::value)
							.forEach(elements::add);
					}

				} else {

					ResourceKey<E> key = ResourceKey.create(registryRef, id);
					Optional<Holder.Reference<E>> entry = entryLookup.get(key);

					if (required && entry.isEmpty()) {
						throw new DataException(DataException.Phase.READING, index, "Type \"" + id + "\" is not registered in registry \"" + registryRef.location() + "\"!");
					} else {
						entry.ifPresent(reference -> elementsByKey.put(key, reference.value()));
					}

				}

				tagEntries.add(tagEntry);

			}

			return new TagLike<>(registryRef, elementsByKey, elementsByTag, tagEntries);

		}

	}

}

