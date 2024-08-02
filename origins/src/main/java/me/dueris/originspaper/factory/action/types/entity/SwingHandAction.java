package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SwingHandAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {
		if (entity instanceof LivingEntity living) {
			living.swing(data.get("hand"), true);
		}
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("swing_hand"),
			InstanceDefiner.instanceDefiner()
				.add("hand", SerializableDataTypes.HAND, InteractionHand.MAIN_HAND),
			SwingHandAction::action
		);
	}
}
