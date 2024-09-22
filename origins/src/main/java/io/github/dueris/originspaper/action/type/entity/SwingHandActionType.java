package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SwingHandActionType {

	public static void action(Entity entity, InteractionHand hand) {

		if (entity instanceof LivingEntity living) {
			living.swing(hand, true);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("swing_hand"),
			new SerializableData()
				.add("hand", SerializableDataTypes.HAND, InteractionHand.MAIN_HAND),
			(data, entity) -> action(entity,
				data.get("hand")
			)
		);
	}
}
