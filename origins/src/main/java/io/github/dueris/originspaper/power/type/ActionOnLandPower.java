package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
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

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_land"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null));
	}

	public void executeAction(Entity entity) {
		if (isActive(entity)) {
			entityAction.accept(entity);
		}
	}
}
