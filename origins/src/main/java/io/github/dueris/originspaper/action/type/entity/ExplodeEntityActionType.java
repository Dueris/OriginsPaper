package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ExplodeEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ExplodeEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("destructible", BlockCondition.DATA_TYPE, null)
			.add("indestructible", BlockCondition.DATA_TYPE, null)
			.add("destruction_type", SerializableDataTypes.DESTRUCTION_TYPE, Explosion.BlockInteraction.DESTROY)
			.add("damage_self", SerializableDataTypes.BOOLEAN, true)
			.add("create_fire", SerializableDataTypes.BOOLEAN, false)
			.add("power", SerializableDataTypes.NON_NEGATIVE_FLOAT)
			.add("indestructible_resistance", SerializableDataTypes.NON_NEGATIVE_FLOAT, 10.0F),
		data -> new ExplodeEntityActionType(
			data.get("destructible"),
			data.get("indestructible"),
			data.get("destruction_type"),
			data.get("damage_self"),
			data.get("create_fire"),
			data.get("power"),
			data.get("indestructible_resistance")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("destructible", actionType.destructibleCondition)
			.set("indestructible", actionType.indestructibleCondition)
			.set("destruction_type", actionType.destructionType)
			.set("damage_self", actionType.damageSelf)
			.set("create_fire", actionType.createFire)
			.set("power", actionType.power)
			.set("indestructible_resistance", actionType.indestructibleResistance)
	);

	private final BlockCondition destructibleCondition;
	private final BlockCondition indestructibleCondition;

	private final Explosion.BlockInteraction destructionType;

	private final boolean damageSelf;
	private final boolean createFire;

	private final float power;
	private final float indestructibleResistance;

	public ExplodeEntityActionType(BlockCondition destructibleCondition, BlockCondition indestructibleCondition, Explosion.BlockInteraction destructionType, boolean damageSelf, boolean createFire, float power, float indestructibleResistance) {
		this.destructibleCondition = destructibleCondition;
		this.indestructibleCondition = indestructibleCondition;
		this.destructionType = destructionType;
		this.damageSelf = damageSelf;
		this.createFire = createFire;
		this.power = power;
		this.indestructibleResistance = indestructibleResistance;
	}

	@Override
	protected void execute(Entity entity) {

		if (entity.level().isClientSide()) {
			return;
		}

		Vec3 entityPos = entity.position();
		Predicate<BlockConditionContext> behaviorCondition = indestructibleCondition;

		if (destructibleCondition != null) {
			behaviorCondition = Util.combineOr(destructibleCondition.negate(), behaviorCondition);
		}

		Util.createExplosion(
			entity.level(),
			damageSelf ? null : entity,
			Explosion.getDefaultDamageSource(entity.level(), entity),
			entityPos.x(),
			entityPos.y(),
			entityPos.z(),
			power,
			createFire,
			destructionType,
			Util.createExplosionBehavior(behaviorCondition, indestructibleResistance)
		);

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.EXPLODE;
	}

}
