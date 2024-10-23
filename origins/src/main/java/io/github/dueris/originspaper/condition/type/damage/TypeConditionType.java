package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class TypeConditionType {

	public static boolean condition(DamageSource damageSource, ResourceKey<DamageType> damageTypeKey) {
		return damageSource.is(damageTypeKey);
	}

	public static ConditionTypeFactory<Tuple<DamageSource, Float>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("type"),
			new SerializableData()
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(),
				data.get("damage_type"))
		);
	}

}
