package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public class TypeDamageConditionType extends DamageConditionType {

	public static final TypedDataObjectFactory<TypeDamageConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
		data -> new TypeDamageConditionType(
			data.get("damage_type")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("damage_type", conditionType.damageType)
	);

	private final ResourceKey<DamageType> damageType;

	public TypeDamageConditionType(ResourceKey<DamageType> damageType) {
		this.damageType = damageType;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		return source.is(damageType);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.TYPE;
	}

}
