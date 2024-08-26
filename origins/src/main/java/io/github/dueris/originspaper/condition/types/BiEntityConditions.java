package io.github.dueris.originspaper.condition.types;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypes;
import io.github.dueris.originspaper.condition.meta.MetaConditions;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BiEntityConditions implements Listener {
	public static void registerAll() {
		MetaConditions.register(Registries.BIENTITY_CONDITION, BiEntityConditions::register);
		ConditionTypes.registerPackage(BiEntityConditions::register, "io.github.dueris.originspaper.condition.types.bientity");
	}

	public static void register(@NotNull ConditionTypeFactory<Tuple<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory, factory.getSerializerId());
	}

}
