package io.github.dueris.originspaper.action.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.action.ActionTypes;
import io.github.dueris.originspaper.action.meta.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

// Left is the actor, right is the target.
public class BiEntityActions {

	public static void register(ActionTypeFactory<Tuple<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.BIENTITY_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, ApoliDataTypes.BIENTITY_CONDITION));
		register(ChoiceAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, ApoliDataTypes.BIENTITY_CONDITION));
		register(DelayAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, entities -> !entities.getA().level().isClientSide));

		ActionTypes.registerPackage(BiEntityActions::register, "io.github.dueris.originspaper.action.types.bientity");
	}

}
