package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class NameConditionType {

	public static boolean condition(@NotNull DamageSource damageSource, String name) {
		return damageSource.getMsgId().equals(name);
	}

	public static @NotNull ConditionTypeFactory<Tuple<DamageSource, Float>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("name"),
			new SerializableData()
				.add("name", SerializableDataTypes.STRING),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(),
				data.get("name")
			)
		);
	}

}
