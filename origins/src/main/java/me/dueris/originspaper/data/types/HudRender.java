package me.dueris.originspaper.data.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;

public record HudRender(boolean shouldRender, ConditionFactory<Entity> condition) {
	public static final HudRender DONT_RENDER = new HudRender(false, null);

	public static InstanceDefiner DATA = InstanceDefiner.instanceDefiner()
		.add("should_render", SerializableDataTypes.BOOLEAN, true)
		.add("condition", ApoliDataTypes.ENTITY_CONDITION, null);
}
