package io.github.dueris.originspaper.condition.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.condition.meta.MetaConditions;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class FluidConditions {
	public static void registerAll() {
		MetaConditions.register(Registries.FLUID_CONDITION, FluidConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("empty"),
			SerializableData.serializableData(),
			(data, fluid) -> fluid.isEmpty()
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("still"),
			SerializableData.serializableData(),
			(data, fluid) -> {
				System.out.println(fluid.getType());
				System.out.println(fluid.isSource());
				return fluid.isSource();
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			SerializableData.serializableData()
				.add("tag", SerializableDataTypes.FLUID_TAG),
			(data, fluid) -> fluid.holder().is((TagKey<Fluid>) data.get("tag"))
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			SerializableData.serializableData()
				.add("fluid", SerializableDataTypes.FLUID),
			(data, fluid) -> fluid.getType() == data.get("fluid")
		));
	}

	public static void register(@NotNull ConditionFactory<FluidState> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory, factory.getSerializerId());
	}
}
