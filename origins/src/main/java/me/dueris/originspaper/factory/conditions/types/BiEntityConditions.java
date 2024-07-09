package me.dueris.originspaper.factory.conditions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.apoli.EntitySetPower;
import me.dueris.originspaper.factory.powers.apoli.PreventEntityRender;
import me.dueris.originspaper.registry.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class BiEntityConditions implements Listener {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("both"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())); // target

			return a.get() && t.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("either"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())); // target

			return a.get() || t.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("invert"), (condition, pair) -> ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.second(), pair.first())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("undirected"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true); // Not swapped
			AtomicBoolean b = new AtomicBoolean(true); // Swapped

			a.set(ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.first(), pair.second())); // actor, target
			b.set(ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.second(), pair.first())); // target, actor

			return a.get() || b.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("actor_condition"), (condition, pair) -> ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("target_condition"), (condition, pair) -> ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("equal"), (condition, pair) -> pair.first() == pair.second()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_entity_set"), (condition, pair) -> EntitySetPower.isInEntitySet(pair.second(), condition.getString("set"))));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("can_see"), (condition, pair) -> PreventEntityRender.canSeeEntity(pair.first(), pair.second(), condition)));

	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, Pair<CraftEntity, CraftEntity> tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
