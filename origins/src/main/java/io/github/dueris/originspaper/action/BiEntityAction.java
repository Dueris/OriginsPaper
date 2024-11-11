package io.github.dueris.originspaper.action;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.bientity.meta.AndBiEntityActionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;

public final class BiEntityAction extends AbstractAction<BiEntityActionContext, BiEntityActionType> {

	public static final SerializableDataType<BiEntityAction> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.actions("type", BiEntityActionTypes.DATA_TYPE, AndBiEntityActionType::new, BiEntityAction::new));

	public BiEntityAction(BiEntityActionType actionType) {
		super(actionType);
	}

	public void execute(Entity actor, Entity target) {
		accept(new BiEntityActionContext(actor, target));
	}

}
