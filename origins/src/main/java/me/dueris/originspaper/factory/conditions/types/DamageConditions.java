package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.function.BiPredicate;

public class DamageConditions {

	public void registerConditions() {

	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, EntityDamageEvent> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, EntityDamageEvent> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, EntityDamageEvent tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}