package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.type.damage.*;
import io.github.dueris.originspaper.condition.type.damage.meta.AllOfDamageConditionType;
import io.github.dueris.originspaper.condition.type.damage.meta.AnyOfDamageConditionType;
import io.github.dueris.originspaper.condition.type.damage.meta.ConstantDamageConditionType;
import io.github.dueris.originspaper.condition.type.damage.meta.RandomChanceDamageConditionType;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.core.Registry;

public class DamageConditionTypes {

    public static final IdentifierAlias ALIASES = new IdentifierAlias();
    public static final SerializableDataType<ConditionConfiguration<DamageConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.DAMAGE_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Damage condition type \"" + id + "\" is undefined!");

    public static final ConditionConfiguration<AllOfDamageConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(DamageCondition.DATA_TYPE, AllOfDamageConditionType::new));
    public static final ConditionConfiguration<AnyOfDamageConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(DamageCondition.DATA_TYPE, AnyOfDamageConditionType::new));
    public static final ConditionConfiguration<ConstantDamageConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantDamageConditionType::new));
    public static final ConditionConfiguration<RandomChanceDamageConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceDamageConditionType::new));

    public static final ConditionConfiguration<AmountDamageConditionType> AMOUNT  = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("amount"), AmountDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<AttackerDamageConditionType> ATTACKER = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("attacker"), AttackerDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<BypassesArmorDamageConditionType> BYPASSES_ARMOR = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("bypasses_armor"), BypassesArmorDamageConditionType::new));
    public static final ConditionConfiguration<ExplosiveDamageConditionType> EXPLOSIVE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("explosive"), ExplosiveDamageConditionType::new));
    public static final ConditionConfiguration<FireDamageConditionType> FIRE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("fire"), FireDamageConditionType::new));
    public static final ConditionConfiguration<FromFallingDamageConditionType> FROM_FALLING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("from_falling"), FromFallingDamageConditionType::new));
    public static final ConditionConfiguration<InTagDamageConditionType> IN_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_tag"), InTagDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<NameDamageConditionType> NAME = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("name"), NameDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<OutOfWorldDamageConditionType> OUT_OF_WORLD = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("out_of_world"), OutOfWorldDamageConditionType::new));
    public static final ConditionConfiguration<ProjectileDamageConditionType> PROJECTILE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("projectile"), ProjectileDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<TypeDamageConditionType> TYPE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("type"), TypeDamageConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<UnblockableDamageConditionType> UNBLOCKABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("unblockable"), UnblockableDamageConditionType::new));

    public static void register() {

    }

    @SuppressWarnings("unchecked")
	public static <CT extends DamageConditionType> ConditionConfiguration<CT> register(ConditionConfiguration<CT> configuration) {

        ConditionConfiguration<DamageConditionType> casted = (ConditionConfiguration<DamageConditionType>) configuration;
        Registry.register(ApoliRegistries.DAMAGE_CONDITION_TYPE, casted.id(), casted);

        return configuration;

    }

}
