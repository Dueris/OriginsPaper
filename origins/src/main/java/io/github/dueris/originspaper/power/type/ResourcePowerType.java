package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ResourcePowerType extends HudRenderedVariableIntPowerType {

	public static final TypedDataObjectFactory<ResourcePowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("min_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("max_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("min", SerializableDataTypes.INT)
			.add("max", SerializableDataTypes.INT)
			.addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.get("min")),
		data -> new ResourcePowerType(
			data.get("min_action"),
			data.get("max_action"),
			data.get("hud_render"),
			data.get("min"),
			data.get("max"),
			data.get("start_value")
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("min_action", powerType.minAction)
			.set("max_action", powerType.maxAction)
			.set("hud_render", powerType.getRenderSettings())
			.set("min", powerType.getMin())
			.set("max", powerType.getMax())
			.set("start_value", powerType.getStartValue())
	);

	private final Optional<EntityAction> minAction;
	private final Optional<EntityAction> maxAction;

	public ResourcePowerType(Optional<EntityAction> minAction, Optional<EntityAction> maxAction, HudRender hudRender, int min, int max, int startValue) {
		super(hudRender, min, max, startValue);
		this.minAction = minAction;
		this.maxAction = maxAction;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.RESOURCE;
	}

	@Override
	public int setValue(int newValue) {

		int oldValue = getValue();
		int actualNewValue = super.setValue(newValue);

		if (oldValue != actualNewValue) {
			minAction.filter(action -> actualNewValue == getMin()).ifPresent(action -> action.execute(getHolder()));
			maxAction.filter(action -> actualNewValue == getMax()).ifPresent(action -> action.execute(getHolder()));
		}

		return actualNewValue;

	}

}
