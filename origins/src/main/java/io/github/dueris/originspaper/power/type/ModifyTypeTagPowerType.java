package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.mixin.TagEntryAccessor;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
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
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyTypeTagPowerType extends PowerType {

	private static final Map<ResourceLocation, Collection<ResourceLocation>> ENTITY_TYPE_SUB_TAGS = new ConcurrentHashMap<>();
	private static final String ENTITY_TYPE_TAG_PATH = Registries.tagsDirPath(Registries.ENTITY_TYPE);

	private final TagKey<EntityType<?>> tag;

	public ModifyTypeTagPowerType(Power power, LivingEntity entity, TagKey<EntityType<?>> tag) {
		super(power, entity);
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
			dependencyTracker.orderByDependencies((id, dependencies) -> dependencies.entries()
				.stream()
				.map(TagLoader.EntryWithSource::entry)
				.filter(entry -> entry.build(valueGetter, value -> {
				}))
				.map(TagEntryAccessor.class::cast)
				.filter(TagEntryAccessor::isTag)
				.forEach(entry -> ENTITY_TYPE_SUB_TAGS
					.computeIfAbsent(id, k -> new ObjectArraySet<>())
					.add(entry.getId())));
		}

	}

	@ApiStatus.Internal
	public static void resetTagCache(MinecraftServer server, CloseableResourceManager resourceManager) {
		ENTITY_TYPE_SUB_TAGS.clear();
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_type_tag"),
			new SerializableData()
				.add("tag", SerializableDataTypes.ENTITY_TAG),
			data -> (power, entity) -> new ModifyTypeTagPowerType(power, entity,
				data.get("tag")
			)
		).allowCondition();
	}

	public boolean doesApply(TagKey<EntityType<?>> typeTag) {
		return Objects.equals(typeTag, tag) || ENTITY_TYPE_SUB_TAGS.getOrDefault(typeTag.location(), new ObjectArrayList<>())
			.stream()
			.map(id -> TagKey.create(Registries.ENTITY_TYPE, id))
			.anyMatch(this::doesApply);
	}

}
