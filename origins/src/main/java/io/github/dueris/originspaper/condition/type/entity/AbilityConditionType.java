package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AbilityConditionType {

	public static boolean condition(Entity entity, SerializableData.Instance data) {
		boolean enabled = false;
		if (entity instanceof Player player && !entity.level().isClientSide) {
			switch (data.getId("ability").toString()) {
				case "minecraft:flying":
					enabled = player.getAbilities().flying;
				case "minecraft:instabuild":
					enabled = player.getAbilities().instabuild;
				case "minecraft:invulnerable":
					enabled = player.getAbilities().invulnerable;
				case "minecraft:mayBuild":
					enabled = player.getAbilities().mayBuild;
				case "minecraft:mayfly":
					enabled = player.getAbilities().mayfly;
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + data.getId("ability").toString());
			}
		}
		return enabled;
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("ability"),
			new SerializableData()
				.add("ability", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> condition(entity,
				data
			)
		);
	}

}
