package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Space;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class AddVelocityActionType {

	public static void action(Entity entity, Vector3f velocity, @NotNull Space space, boolean set) {

		TriConsumer<Float, Float, Float> method = set
			? entity::setDeltaMovement
			: entity::push;

		space.toGlobal(velocity, entity);
		method.accept(velocity.x(), velocity.y(), velocity.z());

		entity.hurtMarked = true;

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("add_velocity"),
			new SerializableData()
				.add("x", SerializableDataTypes.FLOAT, 0.0F)
				.add("y", SerializableDataTypes.FLOAT, 0.0F)
				.add("z", SerializableDataTypes.FLOAT, 0.0F)
				.add("space", ApoliDataTypes.SPACE, Space.WORLD)
				.add("set", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> action(entity,
				new Vector3f(data.get("x"), data.get("y"), data.get("z")),
				data.get("space"),
				data.get("set")
			)
		);
	}

}
