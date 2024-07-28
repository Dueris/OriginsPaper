package me.dueris.originspaper.factory.conditions.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class FluidConditions {
	public static void registerAll() {
		MetaConditions.register(Registries.FLUID_CONDITION, FluidConditions::register);
		/*register(new ConditionFactory(OriginsPaper.apoliIdentifier("empty"), (data, fluid) -> {
			return fluid.defaultFluidState().isEmpty();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("still"), (data, fluid) -> {
			return fluid.defaultFluidState().isSource();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, fluid) -> {
			return fluid.defaultFluidState().is(data.getTagKey("tag", net.minecraft.core.registries.Registries.FLUID));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fluid"), (data, fluid) -> {
			return fluid.defaultFluidState().getType() == BuiltInRegistries.FLUID.get(data.getResourceLocation("fluid"));
		}));*/
	}

	public static void register(@NotNull ConditionFactory<Fluid> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory, factory.getSerializerId());
	}
}
