package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GrantPowerEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<GrantPowerEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("power", ApoliDataTypes.POWER_REFERENCE)
			.add("source", SerializableDataTypes.IDENTIFIER),
		data -> new GrantPowerEntityActionType(
			data.get("power"),
			data.get("source")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("power", actionType.power)
			.set("source", actionType.source)
	);

	private final PowerReference power;
	private final ResourceLocation source;

	public GrantPowerEntityActionType(PowerReference power, ResourceLocation source) {
		this.power = power;
		this.source = source;
	}

	@Override
	protected void execute(Entity entity) {
		PowerHolderComponent.grantPower(entity, power.getPower(), source, true);
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.GRANT_POWER;
	}

}
