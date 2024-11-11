package io.github.dueris.originspaper.action;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.item.meta.AndItemActionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public final class ItemAction extends AbstractAction<ItemActionContext, ItemActionType> {

	public static final SerializableDataType<ItemAction> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.actions("type", ItemActionTypes.DATA_TYPE, AndItemActionType::new, ItemAction::new));

	public ItemAction(ItemActionType actionType) {
		super(actionType);
	}

	public void execute(Level world, SlotAccess stackReference) {
		accept(new ItemActionContext(world, stackReference));
	}

}
