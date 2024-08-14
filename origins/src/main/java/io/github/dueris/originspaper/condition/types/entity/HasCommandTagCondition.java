package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HasCommandTagCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, @NotNull Entity entity) {

		Set<String> commandTags = entity.getTags();
		Set<String> specifiedCommandTags = new HashSet<>();

		data.ifPresent("tag", specifiedCommandTags::add);
		data.ifPresent("tags", specifiedCommandTags::addAll);
		data.ifPresent("command_tag", specifiedCommandTags::add);
		data.ifPresent("command_tags", specifiedCommandTags::addAll);

		return specifiedCommandTags.isEmpty()
			? !commandTags.isEmpty()
			: !Collections.disjoint(commandTags, specifiedCommandTags);

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("has_command_tag"),
			InstanceDefiner.instanceDefiner()
				.add("tag", SerializableDataTypes.STRING, null)
				.add("command_tag", SerializableDataTypes.STRING, null)
				.add("tags", SerializableDataTypes.list(SerializableDataTypes.STRING), null)
				.add("commands_tags", SerializableDataTypes.list(SerializableDataTypes.STRING), null),
			HasCommandTagCondition::condition
		);

	}
}
