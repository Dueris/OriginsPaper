package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.damage.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;

public class DamageConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final ConditionTypeFactory<Tuple<DamageSource, Float>> AMOUNT = register(AmountConditionType.getFactory());

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.DAMAGE_CONDITION, DamageConditionTypes::register);
		register(NameConditionType.getFactory());
		register(ProjectileConditionType.getFactory());
		register(AttackerConditionType.getFactory());

		//region  FIXME: These are deprecated. Remove them in the future -eggohito
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("fire"), DamageTypeTags.IS_FIRE));
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("bypasses_armor"), DamageTypeTags.BYPASSES_ARMOR));
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("explosive"), DamageTypeTags.IS_EXPLOSION));
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("from_falling"), DamageTypeTags.IS_FALL));
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("unblockable"), DamageTypeTags.BYPASSES_SHIELD));
		register(InTagConditionType.createFactory(OriginsPaper.apoliIdentifier("out_of_world"), DamageTypeTags.BYPASSES_INVULNERABILITY));
		//endregion

		register(InTagConditionType.getFactory());
		register(TypeConditionType.getFactory());

	}

	public static <F extends ConditionTypeFactory<Tuple<DamageSource, Float>>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.DAMAGE_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
