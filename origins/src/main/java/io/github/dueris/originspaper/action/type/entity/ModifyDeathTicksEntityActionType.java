package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ModifyDeathTicksEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ModifyDeathTicksEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("modifier", Modifier.DATA_TYPE),
		data -> new ModifyDeathTicksEntityActionType(
			data.get("modifier")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("modifier", actionType.modifier)
	);

	private final Modifier modifier;

	public ModifyDeathTicksEntityActionType(Modifier modifier) {
		this.modifier = modifier;
	}

	@Override
	protected void execute(Entity entity) {

		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.deathTime = (int) modifier.apply(entity, livingEntity.deathTime);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.MODIFY_DEATH_TICKS;
	}

}
