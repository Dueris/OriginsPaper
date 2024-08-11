package me.dueris.originspaper.condition.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.condition.Conditions;
import me.dueris.originspaper.condition.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.ITEM_CONDITION, ItemConditions::register);
		Conditions.registerPackage(ItemConditions::register, "me.dueris.originspaper.factory.condition.types.item");
	}

	public static void register(@NotNull ConditionFactory<Tuple<Level, ItemStack>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory, factory.getSerializerId());
	}

}
