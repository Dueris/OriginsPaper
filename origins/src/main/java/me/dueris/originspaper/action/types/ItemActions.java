package me.dueris.originspaper.action.types;

import io.github.dueris.calio.SerializableDataTypes;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.action.Actions;
import me.dueris.originspaper.action.meta.*;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public class ItemActions {

	public static void register(ActionFactory<Tuple<Level, SlotAccess>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.ITEM_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION,
			worldItemStackPair -> new Tuple<>(worldItemStackPair.getA(), worldItemStackPair.getB().get())));
		register(ChoiceAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION,
			worldItemStackPair -> new Tuple<>(worldItemStackPair.getA(), worldItemStackPair.getB().get())));
		register(DelayAction.getFactory(ApoliDataTypes.ITEM_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.ITEM_ACTION, worldAndStack -> !worldAndStack.getA().isClientSide));

		Actions.registerPackage(ItemActions::register, "me.dueris.originspaper.action.types.item");
	}

}
