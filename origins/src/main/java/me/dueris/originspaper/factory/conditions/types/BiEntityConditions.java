package me.dueris.originspaper.factory.conditions.types;

import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.Conditions;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BiEntityConditions implements Listener {
	public static void registerAll() {
		MetaConditions.register(Registries.BIENTITY_CONDITION, BiEntityConditions::register);
		Conditions.registerPackage(BiEntityConditions::register, "bientity");
	}

	public static void register(@NotNull ConditionFactory<Tuple<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory, factory.getSerializerId());
	}

}
