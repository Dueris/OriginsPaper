package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyTypeTagPowerType extends PowerType {

	public static final TypedDataObjectFactory<ModifyTypeTagPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("tag", SerializableDataTypes.ENTITY_TAG),
		(data, condition) -> new ModifyTypeTagPowerType(
			data.get("tag"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("tag", powerType.tag)
	);
	private static final Map<ResourceLocation, Collection<ResourceLocation>> ENTITY_TYPE_SUB_TAGS = new ConcurrentHashMap<>();
	private static final String ENTITY_TYPE_TAG_PATH = Registries.tagsDirPath(Registries.ENTITY_TYPE);
	protected final TagKey<EntityType<?>> tag;

	public ModifyTypeTagPowerType(TagKey<EntityType<?>> tag, Optional<EntityCondition> condition) {
		super(condition);
		this.tag = tag;
	}

	public static boolean doesApply(Entity entity, TagKey<EntityType<?>> typeTag) {
		return PowerHolderComponent.hasPowerType(entity, ModifyTypeTagPowerType.class, type -> type.doesApply(typeTag));
	}

	public static boolean doesApply(Entity entity, HolderSet<EntityType<?>> entryList) {
		return entryList.unwrapKey()
			.map(tagKey -> doesApply(entity, tagKey))
			.orElse(false);
	}

	@ApiStatus.Internal
	public static <T> void setTagCache(String directory, TagEntry.Lookup<T> valueGetter, DependencySorter<ResourceLocation, TagLoader.SortingEntry> dependencyTracker) {

		if (ENTITY_TYPE_TAG_PATH.equals(directory)) {
//            dependencyTracker.traverse((id, dependencies) -> dependencies.entries()
//                .stream()
//                .map(TagGroupLoader.TrackedEntry::entry)
//                .filter(entry -> entry.resolve(valueGetter, value -> {}))
//                .map(TagEntryAccessor.class::cast)
//                .filter(TagEntryAccessor::isTag)
//                .forEach(entry -> ENTITY_TYPE_SUB_TAGS
//                    .computeIfAbsent(id, k -> new ObjectArraySet<>())
//                    .add(entry.getId())));
		}

	}

	@ApiStatus.Internal
	public static void resetTagCache(MinecraftServer server, CloseableResourceManager resourceManager) {
		ENTITY_TYPE_SUB_TAGS.clear();
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_TYPE_TAG;
	}

	public boolean doesApply(TagKey<EntityType<?>> typeTag) {
		return Objects.equals(typeTag, tag) || ENTITY_TYPE_SUB_TAGS.getOrDefault(typeTag.location(), new ObjectArrayList<>())
			.stream()
			.map(id -> TagKey.create(Registries.ENTITY_TYPE, id))
			.anyMatch(this::doesApply);
	}

}
