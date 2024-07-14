package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.block.CraftBlock;

import java.util.function.BiPredicate;

public class BlockConditions {

	public void registerConditions() {

	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, CraftBlock> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, CraftBlock> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, CraftBlock tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}
}
