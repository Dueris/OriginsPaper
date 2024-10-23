package io.github.dueris.originspaper.action.type;


import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.type.bientity.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class BiEntityActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {

		MetaActionTypes.register(ApoliDataTypes.BIENTITY_ACTION, ApoliDataTypes.BIENTITY_CONDITION, Function.identity(), BiEntityActionTypes::register);

		register(InvertActionType.getFactory());
		register(ActorActionType.getFactory());
		register(TargetActionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("mount"), MountActionType::action));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("set_in_love"), SetInLoveActionType::action));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("tame"), TameActionType::action));
		register(AddVelocityActionType.getFactory());
		register(DamageActionType.getFactory());
		register(AddToEntitySetActionType.getFactory());
		register(RemoveFromEntitySetActionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("leash"), LeashActionType::action));

	}

	public static ActionTypeFactory<Tuple<Entity, Entity>> createSimpleFactory(ResourceLocation id, BiConsumer<Entity, Entity> action) {
		return new ActionTypeFactory<>(id, new SerializableData(), (data, actorAndTarget) -> action.accept(actorAndTarget.getA(), actorAndTarget.getB()));
	}

	public static <F extends ActionTypeFactory<Tuple<Entity, Entity>>> F register(F actionFactory) {
		return Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
	}

}
