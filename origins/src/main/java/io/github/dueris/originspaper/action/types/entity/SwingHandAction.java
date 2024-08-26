package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SwingHandAction {

	public static void action(SerializableData.Instance data, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.swing(data.get("hand"), true);
		}
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("swing_hand"),
			SerializableData.serializableData()
				.add("hand", SerializableDataTypes.HAND, InteractionHand.MAIN_HAND),
			SwingHandAction::action
		);
	}
}
