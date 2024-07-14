package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiPredicate;

public class FluidConditions {
	public void registerConditions() {

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
