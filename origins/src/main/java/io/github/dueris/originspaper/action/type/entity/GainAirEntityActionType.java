package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GainAirEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<GainAirEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("value", SerializableDataTypes.INT),
		data -> new GainAirEntityActionType(
			data.get("value")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("value", actionType.value)
	);

	private final int value;

	public GainAirEntityActionType(int value) {
		this.value = value;
	}

	@Override
	protected void execute(Entity entity) {

		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.setAirSupply(Math.min(livingEntity.getAirSupply() + value, livingEntity.getMaxAirSupply()));
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.GAIN_AIR;
	}

}
