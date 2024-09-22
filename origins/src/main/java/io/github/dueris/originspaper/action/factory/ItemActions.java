package io.github.dueris.originspaper.action.factory;

import io.github.dueris.originspaper.action.type.item.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemActions {

	public static void register() {

		MetaActions.register(ApoliDataTypes.ITEM_ACTION, ApoliDataTypes.ITEM_CONDITION, worldAndStackRef -> new Tuple<>(worldAndStackRef.getA(), worldAndStackRef.getB().get()), ItemActions::register);

		register(ConsumeActionType.getFactory());
		register(ModifyActionType.getFactory());
		register(DamageActionType.getFactory());
		register(MergeCustomDataActionType.getFactory());
		register(RemoveEnchantmentActionType.getFactory());

	}

	public static @NotNull ActionTypeFactory<Tuple<Level, SlotAccess>> register(ActionTypeFactory<Tuple<Level, SlotAccess>> actionFactory) {
		return Registry.register(ApoliRegistries.ITEM_ACTION, actionFactory.getSerializerId(), actionFactory);
	}

}
