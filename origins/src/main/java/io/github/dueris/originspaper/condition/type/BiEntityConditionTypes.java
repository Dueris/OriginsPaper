package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.bientity.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.function.BiPredicate;

public class BiEntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.BIENTITY_CONDITION, BiEntityConditionTypes::register);
		register(InvertConditionType.getFactory());
		register(ActorConditionType.getFactory());
		register(TargetConditionType.getFactory());
		register(EitherConditionType.getFactory());
		register(BothConditionType.getFactory());
		register(UndirectedConditionType.getFactory());

		register(DistanceConditionType.getFactory());
		register(CanSeeConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("owner"), OwnerConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("riding"), RidingConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("riding_root"), RidingRootConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("riding_recursive"), RidingRecursiveConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("attack_target"), AttackTargetConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("attacker"), AttackerConditionType::condition));
		register(RelativeRotationConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("equal"), Objects::equals));
		register(InEntitySetConditionType.getFactory());
	}

	public static ConditionTypeFactory<Tuple<Entity, Entity>> createSimpleFactory(ResourceLocation id, BiPredicate<Entity, Entity> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, actorAndTarget) -> condition.test(actorAndTarget.getA(), actorAndTarget.getB()));
	}

	public static <F extends ConditionTypeFactory<Tuple<Entity, Entity>>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.BIENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
