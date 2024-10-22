package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class InTagConditionType {

	public static boolean condition(DamageSource damageSource, TagKey<DamageType> damageTypeTag) {
		return damageSource.is(damageTypeTag);
	}

	public static ConditionTypeFactory<Tuple<DamageSource, Float>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			new SerializableData()
				.add("tag", SerializableDataType.tag(Registries.DAMAGE_TYPE)),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(),
				data.get("tag")
			)
		);
	}

	public static ConditionTypeFactory<Tuple<DamageSource, Float>> createFactory(ResourceLocation id, TagKey<DamageType> damageTypeTag) {
		return new ConditionTypeFactory<>(id,
			new SerializableData(),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(), damageTypeTag)
		);
	}

}
