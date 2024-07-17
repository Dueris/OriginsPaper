package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiPredicate;

public class FluidConditions {
	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("empty"), (data, fluid) -> {
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
		}));
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, Fluid> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, Fluid> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, net.minecraft.world.level.material.Fluid tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}
}
