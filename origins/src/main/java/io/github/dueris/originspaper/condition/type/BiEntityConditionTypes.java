package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.bientity.*;
import io.github.dueris.originspaper.condition.type.bientity.meta.*;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class BiEntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ConditionConfiguration<BiEntityConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.BIENTITY_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Bi-entity condition type \"" + id + "\" is undefined!");

	public static final ConditionConfiguration<AllOfBiEntityConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(BiEntityCondition.DATA_TYPE, AllOfBiEntityConditionType::new));
	public static final ConditionConfiguration<AnyOfBiEntityConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(BiEntityCondition.DATA_TYPE, AnyOfBiEntityConditionType::new));
	public static final ConditionConfiguration<ConstantBiEntityConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantBiEntityConditionType::new));
	public static final ConditionConfiguration<RandomChanceBiEntityConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceBiEntityConditionType::new));

	public static final ConditionConfiguration<ActorConditionBiEntityConditionType> ACTOR_CONDITION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("actor_condition"), ActorConditionBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BothBiEntityConditionType> BOTH = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("both"), BothBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EitherBiEntityConditionType> EITHER = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("either"), EitherBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InvertBiEntityConditionType> INVERT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("invert"), InvertBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<TargetConditionBiEntityConditionType> TARGET_CONDITION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("target_condition"), TargetConditionBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<UndirectedBiEntityConditionType> UNDIRECTED = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("undirected"), UndirectedBiEntityConditionType.DATA_FACTORY));

	public static final ConditionConfiguration<AttackTargetBiEntityConditionType> ATTACK_TARGET = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("attack_target"), AttackTargetBiEntityConditionType::new));
	public static final ConditionConfiguration<AttackerBiEntityConditionType> ATTACKER = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("attacker"), AttackerBiEntityConditionType::new));
	public static final ConditionConfiguration<CanSeeBiEntityConditionType> CAN_SEE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("can_see"), CanSeeBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<DistanceBiEntityConditionType> DISTANCE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("distance"), DistanceBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EqualBiEntityConditionType> EQUAL = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("equal"), EqualBiEntityConditionType::new));
	public static final ConditionConfiguration<InEntitySetBiEntityConditionType> IN_ENTITY_SET = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_entity_set"), InEntitySetBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<OwnerBiEntityConditionType> OWNER = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("owner"), OwnerBiEntityConditionType::new));
	public static final ConditionConfiguration<RelativeRotationBiEntityConditionType> RELATIVE_ROTATION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("relative_rotation"), RelativeRotationBiEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RidingBiEntityConditionType> RIDING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("riding"), RidingBiEntityConditionType::new));
	public static final ConditionConfiguration<RidingRecursiveBiEntityConditionType> RIDING_RECURSIVE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("riding_recursive"), RidingRecursiveBiEntityConditionType::new));
	public static final ConditionConfiguration<RidingRootBiEntityConditionType> RIDING_ROOT = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("riding_root"), RidingRootBiEntityConditionType::new));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <CT extends BiEntityConditionType> ConditionConfiguration<CT> register(ConditionConfiguration<CT> configuration) {

		ConditionConfiguration<BiEntityConditionType> casted = (ConditionConfiguration<BiEntityConditionType>) configuration;
		Registry.register(ApoliRegistries.BIENTITY_CONDITION_TYPE, casted.id(), casted);

		return configuration;

	}

}
