package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RevokeAllPowersActionType {

	public static void action(@NotNull Entity entity, ResourceLocation source) {
		OriginLayer layer = OriginsPaper.getLayer(source);

		for (PowerType type : PowerHolderComponent.getPowers(entity.getBukkitEntity(), layer)) {
			PowerUtils.removePower(entity.getBukkitEntity(), type, (Player) entity.getBukkitEntity(), layer, true);
		}
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("revoke_all_powers"),
			new SerializableData()
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> action(entity,
				data.get("source")
			)
		);
	}

}
