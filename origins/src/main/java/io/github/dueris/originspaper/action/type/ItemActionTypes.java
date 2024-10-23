package io.github.dueris.originspaper.action.type;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.type.item.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public class ItemActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {

		MetaActionTypes.register(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION, worldAndStackRef -> new Tuple<>(worldAndStackRef.getA(), worldAndStackRef.getB().get()), ItemActionTypes::register);

		register(ConsumeActionType.getFactory());
		register(ModifyActionType.getFactory());
		register(DamageActionType.getFactory());
		register(MergeCustomDataActionType.getFactory());
		register(RemoveEnchantmentActionType.getFactory());
		register(HolderActionType.getFactory());
		register(ModifyItemCooldownActionType.getFactory());

	}

	public static <F extends ActionTypeFactory<Tuple<Level, SlotAccess>>> F register(F actionFactory) {
		return Registry.register(ApoliRegistries.ITEM_ACTION, actionFactory.getSerializerId(), actionFactory);
	}

}
