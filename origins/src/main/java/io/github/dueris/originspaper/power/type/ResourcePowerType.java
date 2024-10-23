package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ResourcePowerType extends HudRenderedVariableIntPowerType {

	private final Consumer<Entity> actionOnMin;
	private final Consumer<Entity> actionOnMax;

	public ResourcePowerType(Power power, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax) {
		super(power, entity, hudRender, startValue, min, max);
		this.actionOnMin = actionOnMin;
		this.actionOnMax = actionOnMax;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("resource"),
			new SerializableData()
				.add("min", SerializableDataTypes.INT)
				.add("max", SerializableDataTypes.INT)
				.addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("max_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new ResourcePowerType(power, entity,
				data.get("hud_render"),
				data.getInt("start_value"),
				data.getInt("min"),
				data.getInt("max"),
				data.get("min_action"),
				data.get("max_action")
			)
		).allowCondition();
	}

	@Override
	public int setValue(int newValue) {
		int oldValue = currentValue;
		int actualNewValue = super.setValue(newValue);
		if (oldValue != actualNewValue) {
			if (actionOnMin != null && actualNewValue == min) {
				actionOnMin.accept(entity);
			}
			if (actionOnMax != null && actualNewValue == max) {
				actionOnMax.accept(entity);
			}
		}
		return actualNewValue;
	}

}

