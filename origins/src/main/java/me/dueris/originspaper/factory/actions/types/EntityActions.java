package me.dueris.originspaper.factory.actions.types;

import io.github.dueris.calio.SerializableDataTypes;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.actions.meta.*;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.entity.Entity;

public class EntityActions {

	public static void register(ActionFactory<Entity> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.ENTITY_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.ENTITY_ACTION, ApoliDataTypes.ENTITY_CONDITION));
		register(ChoiceAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.ENTITY_ACTION, ApoliDataTypes.ENTITY_CONDITION));
		register(DelayAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.ENTITY_ACTION, entity -> !entity.level().isClientSide));
	}

}
