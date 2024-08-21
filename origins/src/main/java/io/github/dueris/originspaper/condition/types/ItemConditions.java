package io.github.dueris.originspaper.condition.types;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.condition.Conditions;
import io.github.dueris.originspaper.condition.meta.MetaConditions;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.ITEM_CONDITION, ItemConditions::register);
		Conditions.registerPackage(ItemConditions::register, "io.github.dueris.originspaper.condition.types.item");
	}

	public static void register(@NotNull ConditionFactory<Tuple<Level, ItemStack>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory, factory.getSerializerId());
	}

}
