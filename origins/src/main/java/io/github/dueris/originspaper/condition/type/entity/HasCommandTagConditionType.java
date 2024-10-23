package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HasCommandTagConditionType {

	public static boolean condition(Entity entity, Collection<String> specifiedCommandTags) {
		Set<String> commandTags = entity.getTags();
		return specifiedCommandTags.isEmpty()
			? !commandTags.isEmpty()
			: !Collections.disjoint(commandTags, specifiedCommandTags);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("has_command_tag"),
			new SerializableData()
				.add("command_tag", SerializableDataTypes.STRING, null)
				.add("command_tags", SerializableDataTypes.STRINGS, null),
			(data, entity) -> {

				Collection<String> commandTags = new HashSet<>();

				data.ifPresent("command_tag", commandTags::add);
				data.ifPresent("command_tags", commandTags::addAll);

				return condition(entity, commandTags);

			}
		);
	}

}
