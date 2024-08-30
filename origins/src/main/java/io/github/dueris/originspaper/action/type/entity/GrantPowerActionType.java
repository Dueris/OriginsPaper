package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GrantPowerActionType {

	public static void action(@NotNull Entity entity, PowerReference power, ResourceLocation source) {
		if (entity.getBukkitEntity() instanceof Player) {
			PowerUtils.grantPower(entity.getBukkitEntity(), power.getType(), (Player) entity.getBukkitEntity(), OriginsPaper.getLayer(source), true);
		}
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("grant_power"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> action(entity,
				data.get("power"),
				data.get("source")
			)
		);
	}

}
