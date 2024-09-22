package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ProjectileConditionType {

	public static boolean condition(@NotNull DamageSource damageSource, EntityType<?> projectileType, Predicate<Entity> projectileCondition) {

		Entity source = damageSource.getDirectEntity();

		return damageSource.is(DamageTypeTags.IS_PROJECTILE)
			&& source != null
			&& (projectileType == null || source.getType() == projectileType)
			&& projectileCondition.test(source);

	}

	public static @NotNull ConditionTypeFactory<Tuple<DamageSource, Float>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("projectile"),
			new SerializableData()
				.add("projectile", SerializableDataTypes.ENTITY_TYPE, null)
				.add("projectile_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(),
				data.get("projectile"),
				data.getOrElse("projectile_condition", e -> true)
			)
		);
	}

}
