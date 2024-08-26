package io.github.dueris.originspaper.data.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;

public record HudRender(boolean shouldRender, ConditionTypeFactory<Entity> condition) {
	public static final HudRender DONT_RENDER = new HudRender(false, null);

	public static SerializableData DATA = SerializableData.serializableData()
		.add("should_render", SerializableDataTypes.BOOLEAN, true)
		.add("condition", ApoliDataTypes.ENTITY_CONDITION, null);
}
