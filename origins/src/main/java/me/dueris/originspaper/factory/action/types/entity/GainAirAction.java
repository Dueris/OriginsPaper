package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GainAirAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.identifier("gain_air"),
			InstanceDefiner.instanceDefiner()
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> {
				if (entity instanceof LivingEntity le) {
					le.setAirSupply(Math.min(le.getAirSupply() + data.getInt("value"), le.getMaxAirSupply()));
				}
			}
		);
	}
}
