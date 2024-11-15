package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetOnFireEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<SetOnFireEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("duration", SerializableDataTypes.POSITIVE_FLOAT),
		data -> new SetOnFireEntityActionType(
			data.get("duration")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("duration", actionType.duration)
	);

	private final float duration;

	public SetOnFireEntityActionType(float duration) {
		this.duration = duration;
	}

	@Override
	protected void execute(Entity entity) {
		entity.igniteForSeconds(duration);
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SET_ON_FIRE;
	}

}
