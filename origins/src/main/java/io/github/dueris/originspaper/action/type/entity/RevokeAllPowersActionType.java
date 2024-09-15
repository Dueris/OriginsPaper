package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RevokeAllPowersActionType {

	public static void action(@NotNull Entity entity, ResourceLocation source) {

		for (PowerType type : PowerHolderComponent.getPowers(entity.getBukkitEntity(), source)) {
			PowerUtils.removePower(entity.getBukkitEntity(), type, entity.getBukkitEntity(), source, true);
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
