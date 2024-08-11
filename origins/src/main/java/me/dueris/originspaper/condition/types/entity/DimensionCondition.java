package me.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DimensionCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("dimension"),
			InstanceDefiner.instanceDefiner()
				.add("dimension", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				return entity.level().dimension() == ResourceKey.create(Registries.DIMENSION, data.getId("dimension"));
			}
		);
	}
}
