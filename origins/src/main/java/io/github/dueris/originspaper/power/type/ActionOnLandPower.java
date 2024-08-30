package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ActionOnLandPower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;

	public ActionOnLandPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority, ActionTypeFactory<Entity> entityAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_land"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	public void executeAction(Entity entity) {
		if (isActive(entity)) {
			entityAction.accept(entity);
		}
	}
}
