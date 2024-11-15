package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ProjectileDamageConditionType extends DamageConditionType {

	public static final TypedDataObjectFactory<ProjectileDamageConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("projectile", SerializableDataTypes.ENTITY_TYPE.optional(), Optional.empty())
			.add("projectile_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty()),
		data -> new ProjectileDamageConditionType(
			data.get("projectile"),
			data.get("projectile_condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("projectile", conditionType.projectile)
			.set("projectile_condition", conditionType.projectileCondition)
	);

	private final Optional<EntityType<?>> projectile;
	private final Optional<EntityCondition> projectileCondition;

	public ProjectileDamageConditionType(Optional<EntityType<?>> projectile, Optional<EntityCondition> projectileCondition) {
		this.projectile = projectile;
		this.projectileCondition = projectileCondition;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		Entity entitySource = source.getDirectEntity();
		return source.is(DamageTypeTags.IS_PROJECTILE)
			&& entitySource != null
			&& projectile.map(entitySource.getType()::equals).orElse(true)
			&& projectileCondition.map(entityCondition -> entityCondition.test(entitySource)).orElse(true);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.PROJECTILE;
	}

}
