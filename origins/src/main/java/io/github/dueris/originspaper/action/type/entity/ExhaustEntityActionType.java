package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ExhaustEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("amount", SerializableDataTypes.FLOAT),
		data -> new ExhaustEntityActionType(
			data.get("amount")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("amount", actionType.amount)
	);

	private final float amount;

	public ExhaustEntityActionType(float amount) {
		this.amount = amount;
	}

	@Override
	protected void execute(Entity entity) {

		if (entity instanceof Player player) {
			player.causeFoodExhaustion(amount);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.EXHAUST;
	}

}
