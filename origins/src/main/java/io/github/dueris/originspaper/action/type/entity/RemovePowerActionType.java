package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemovePowerActionType {

	public static void action(@NotNull Entity entity, PowerReference power) {
		if (!(entity instanceof Player)) return;

		for (ResourceLocation source : PowerHolderComponent.getSources(power.getType(), entity.getBukkitEntity())) {
			PowerUtils.removePower(entity.getBukkitEntity(), power.getType(), entity.getBukkitEntity(), source, true);
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
