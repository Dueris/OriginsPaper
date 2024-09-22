package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.mixin.TagEntryAccessor;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyTypeTagPower extends PowerType {
	private static final Map<ResourceLocation, Collection<ResourceLocation>> ENTITY_TYPE_SUB_TAGS = new ConcurrentHashMap<>();
	private static final String ENTITY_TYPE_TAG_PATH = Registries.tagsDirPath(Registries.ENTITY_TYPE);

	private final TagKey<EntityType<?>> tag;

	public ModifyTypeTagPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  TagKey<EntityType<?>> tag) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.tag = tag;
	}

	public static boolean doesApply(@NotNull Entity entity, TagKey<EntityType<?>> typeTag) {
		return PowerHolderComponent.doesHaveConditionedPower(entity.getBukkitEntity(), ModifyTypeTagPower.class, type -> type.doesApply(typeTag));
	}

	public static boolean doesApply(Entity entity, @NotNull HolderSet<EntityType<?>> entryList) {
		return entryList.unwrapKey()
			.map(tagKey -> doesApply(entity, tagKey))
			.orElse(false);
	}

	@SuppressWarnings("unchecked")
	@ApiStatus.Internal
	public static <T> void setTagCache(String directory, TagEntry.Lookup<T> valueGetter, DependencySorter<ResourceLocation, DependencySorter.Entry<ResourceLocation>> dependencyTracker) {

		if (ENTITY_TYPE_TAG_PATH.equals(directory)) {
			dependencyTracker.orderByDependencies((id, dependencies) -> {
				try {
					((List<TagLoader.EntryWithSource>) dependencies.getClass().getDeclaredMethod("entries").invoke(dependencyTracker))
						.stream()
						.map(TagLoader.EntryWithSource::entry)
						.filter(entry -> entry.build(valueGetter, value -> {
						}))
						.map(TagEntryAccessor.class::cast)
						.filter(TagEntryAccessor::isTag)
						.forEach(entry -> ENTITY_TYPE_SUB_TAGS
							.computeIfAbsent(id, k -> new ObjectArraySet<>())
							.add(entry.getId()));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});
		}

	}

	@ApiStatus.Internal
	public static void resetTagCache() {
		ENTITY_TYPE_SUB_TAGS.clear();
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_type_tag"), PowerType.getFactory().getSerializableData()
			.add("tag", SerializableDataTypes.ENTITY_TAG));
	}

	public boolean doesApply(TagKey<EntityType<?>> typeTag) {
		return Objects.equals(typeTag, tag) || ENTITY_TYPE_SUB_TAGS.getOrDefault(typeTag.location(), new ObjectArrayList<>())
			.stream()
			.map(id -> TagKey.create(Registries.ENTITY_TYPE, id))
			.anyMatch(this::doesApply);
	}
}
