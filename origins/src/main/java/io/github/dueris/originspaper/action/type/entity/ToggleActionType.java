package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.power.type.TogglePower;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleActionType {

	public static void action(Entity entity, @NotNull PowerReference power) {

		if (power.getType() instanceof TogglePower toggle && entity.getBukkitEntity() instanceof Player) {
			toggle.onKey(new KeybindTriggerEvent((Player) entity.getBukkitEntity(), toggle.getKeybind().key()));
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("toggle"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE),
			(data, entity) -> action(entity,
				data.get("power")
			)
		);
	}

}
