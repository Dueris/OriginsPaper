package me.dueris.originspaper.factory.conditions.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class FluidConditions {
	public static void registerAll() {
		MetaConditions.register(Registries.FLUID_CONDITION, FluidConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("empty"),
			InstanceDefiner.instanceDefiner(),
			(data, fluid) -> {
				return fluid.defaultFluidState().isEmpty();
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("still"),
			InstanceDefiner.instanceDefiner(),
			(data, fluid) -> {
				return fluid.defaultFluidState().isSource();
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			InstanceDefiner.instanceDefiner()
				.add("tag", SerializableDataTypes.FLUID_TAG),
			(data, fluid) -> {
				return fluid.defaultFluidState().holder().is((TagKey<Fluid>) data.get("tag"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fluid"),
			InstanceDefiner.instanceDefiner()
				.add("fluid", SerializableDataTypes.FLUID),
			(data, fluid) -> {
				return fluid.defaultFluidState().getType() == data.get("fluid");
			}
		));
	}

	public static void register(@NotNull ConditionFactory<Fluid> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory, factory.getSerializerId());
	}
}
