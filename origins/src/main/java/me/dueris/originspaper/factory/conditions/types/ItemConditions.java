package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;

public class ItemConditions {

	public void registerConditions() {

	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, ItemStack> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, ItemStack> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, ItemStack tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}

}
