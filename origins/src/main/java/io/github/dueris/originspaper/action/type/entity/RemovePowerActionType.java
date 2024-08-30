package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.storage.OriginComponent;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemovePowerActionType {

	public static void action(@NotNull Entity entity, PowerReference power) {
		if (!(entity instanceof Player)) return;

		for (OriginLayer layer : OriginComponent.getLayers(entity.getBukkitEntity())) {
			PowerUtils.removePower(entity.getBukkitEntity(), power.getType(), (Player) entity.getBukkitEntity(), layer, true);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
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
