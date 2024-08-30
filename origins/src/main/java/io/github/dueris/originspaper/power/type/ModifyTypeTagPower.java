package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModifyTypeTagPower extends PowerType {
	private final TagKey<EntityType<?>> tag;

	public ModifyTypeTagPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  TagKey<EntityType<?>> tag) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.tag = tag;
	}

	public static boolean doesApply(@NotNull Entity entity, TagKey<EntityType<?>> entityTypeTag) {
		return PowerHolderComponent.doesHaveConditionedPower(entity.getBukkitEntity(), ModifyTypeTagPower.class, (p) -> Objects.equals(p.tag.location().toString(), entityTypeTag.location().toString()));
	}

	public static boolean doesApply(Entity entity, @NotNull HolderSet<EntityType<?>> entryList) {
		return entryList.unwrapKey()
			.map(tagKey -> doesApply(entity, tagKey))
			.orElse(false);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_type_tag"))
			.add("tag", SerializableDataTypes.ENTITY_TAG);
	}
}
