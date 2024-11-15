package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InvertBiEntityActionType extends BiEntityActionType {

	public static final TypedDataObjectFactory<InvertBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("action", BiEntityAction.DATA_TYPE),
		data -> new InvertBiEntityActionType(
			data.get("action")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("action", actionType.action)
	);

	private final BiEntityAction action;

	public InvertBiEntityActionType(BiEntityAction action) {
		this.action = action;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		action.execute(target, actor);
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.INVERT;
	}

}
