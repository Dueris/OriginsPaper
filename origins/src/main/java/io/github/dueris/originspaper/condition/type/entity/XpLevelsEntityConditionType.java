package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class XpLevelsEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<XpLevelsEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new XpLevelsEntityConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final int compareTo;

	public XpLevelsEntityConditionType(Comparison comparison, int compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {
		return entity instanceof Player player
			&& comparison.compare(player.experienceLevel, compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.XP_LEVELS;
	}

}
