package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;

public class RemovePowerActionType {

	public static void action(Entity entity, PowerReference power) {

		List<ResourceLocation> sources = PowerHolderComponent.KEY.maybeGet(entity)
			.stream()
			.map(component -> component.getSources(power))
			.flatMap(Collection::stream)
			.toList();

		if (!sources.isEmpty()) {
			PowerHolderComponent.revokeAllPowersFromAllSources(entity, sources, true);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("remove_power"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE),
			(data, entity) -> action(entity,
				data.get("power")
			)
		);
	}

}
