package me.dueris.originspaper.condition.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.condition.Conditions;
import me.dueris.originspaper.condition.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BiEntityConditions implements Listener {
	public static void registerAll() {
		MetaConditions.register(Registries.BIENTITY_CONDITION, BiEntityConditions::register);
		Conditions.registerPackage(BiEntityConditions::register, "me.dueris.originspaper.condition.types.bientity");
	}

	public static void register(@NotNull ConditionFactory<Tuple<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory, factory.getSerializerId());
	}

}
