package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class AreaOfEffectEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<AreaOfEffectEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE)
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
			.add("radius", SerializableDataTypes.POSITIVE_DOUBLE, 16.0D)
			.add("include_actor", SerializableDataTypes.BOOLEAN, false),
		data -> new AreaOfEffectEntityActionType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			data.get("shape"),
			data.get("radius"),
			data.get("include_actor")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("bientity_action", actionType.biEntityAction)
			.set("bientity_condition", actionType.biEntityCondition)
			.set("shape", actionType.shape)
			.set("radius", actionType.radius)
			.set("include_actor", actionType.includeActor)
	);

	private final BiEntityAction biEntityAction;
	private final Optional<BiEntityCondition> biEntityCondition;

	private final Shape shape;

	private final double radius;
	private final boolean includeActor;

	public AreaOfEffectEntityActionType(BiEntityAction biEntityAction, Optional<BiEntityCondition> biEntityCondition, Shape shape, double radius, boolean includeActor) {
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
		this.shape = shape;
		this.radius = radius;
		this.includeActor = includeActor;
	}

	@Override
	protected void execute(Entity entity) {

		Collection<Entity> targets = Shape.getEntities(shape, entity.level(), entity.getPosition(1.0F), radius);

		for (Entity target : targets) {

			if (!includeActor && target.equals(entity)) {
				continue;
			}

			BiEntityActionContext context = new BiEntityActionContext(entity, target);

			if (biEntityCondition.map(condition -> condition.test(context.forCondition())).orElse(true)) {
				biEntityAction.accept(context);
			}

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.AREA_OF_EFFECT;
	}

}
