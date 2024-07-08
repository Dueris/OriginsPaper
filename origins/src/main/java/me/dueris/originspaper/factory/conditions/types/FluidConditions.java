package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import java.util.function.BiPredicate;

public class FluidConditions {
	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("empty"), (condition, fluid) -> fluid.defaultFluidState().isEmpty()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (condition, fluid) -> {
			NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag"));
			TagKey key = TagKey.create(net.minecraft.core.registries.Registries.FLUID, CraftNamespacedKey.toMinecraft(tag));
			return fluid.is(key);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("still"), (condition, fluid) -> fluid.defaultFluidState().isSource()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fluid"), (condition, fluid) -> fluid.builtInRegistryHolder().key().location().equals(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(condition.getString("fluid"))))));
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, Fluid> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, Fluid> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, net.minecraft.world.level.material.Fluid tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
