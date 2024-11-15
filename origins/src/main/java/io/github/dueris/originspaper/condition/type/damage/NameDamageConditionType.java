package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class NameDamageConditionType extends DamageConditionType {

	public static final TypedDataObjectFactory<NameDamageConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("name", SerializableDataTypes.STRING),
		data -> new NameDamageConditionType(
			data.get("name")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("name", conditionType.name)
	);

	private final String name;

	public NameDamageConditionType(String name) {
		this.name = name;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		return source.getMsgId().equals(name);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.NAME;
	}

}
