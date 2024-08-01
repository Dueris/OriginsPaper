package me.dueris.originspaper.factory.actions.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.actions.meta.*;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public class ItemActions {

	public static void register(ActionFactory<Pair<Level, SlotAccess>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.ITEM_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION,
			worldItemStackPair -> new Pair<>(worldItemStackPair.getLeft(), worldItemStackPair.getRight().get())));
		register(ChoiceAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION,
			worldItemStackPair -> new Pair<>(worldItemStackPair.getLeft(), worldItemStackPair.getRight().get())));
		register(DelayAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.ITEM_ACTION, worldAndStack -> !worldAndStack.getLeft().isClientSide));
	}

}
