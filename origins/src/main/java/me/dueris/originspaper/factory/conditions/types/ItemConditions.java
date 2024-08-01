package me.dueris.originspaper.factory.conditions.types;

import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.Conditions;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.ITEM_CONDITION, ItemConditions::register);
		Conditions.registerPackage(ItemConditions::register, "item");
	}

	public static void register(@NotNull ConditionFactory<Tuple<Level, ItemStack>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory, factory.getSerializerId());
	}

}
