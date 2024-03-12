package me.dueris.genesismc.factory.conditions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.Comparison;
import me.dueris.calio.data.RotationType;
import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.apoli.EntitySetPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class BiEntityConditions implements Listener {

	public void prep() {
		// Meta conditions, shouldnt execute
		// Meta conditions are added in each file to ensure they dont error and skip them when running
		// a meta condition inside another meta condition
		register(new ConditionFactory(GenesisMC.apoliIdentifier("and"), (condition, obj) -> {
			throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("or"), (condition, obj) -> {
			throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("chance"), (condition, obj) -> {
			throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("constant"), (condition, obj) -> {
			throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
		}));
		// Meta conditions end
		register(new ConditionFactory(GenesisMC.apoliIdentifier("both"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second())); // target

			return a.get() && t.get();
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("either"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second())); // target

			return a.get() || t.get();
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("invert"), (condition, pair) -> {
			return ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.second(), pair.first());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("undirected"), (condition, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true); // Not swapped
			AtomicBoolean b = new AtomicBoolean(true); // Swapped

			a.set(ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.first(), pair.second())); // actor, target
			b.set(ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.second(), pair.first())); // target, actor

			return a.get() || b.get();
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("actor_condition"), (condition, pair) -> {
			return ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("target_condition"), (condition, pair) -> {
			return ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_rotation"), (condition, pair) -> {
			net.minecraft.world.entity.Entity nmsActor = pair.first().getHandle();
			net.minecraft.world.entity.Entity nmsTarget = pair.second().getHandle();

			RotationType actorRotationType = RotationType.getRotationType(condition.get("actor_rotation").toString());
			RotationType targetRotationType = RotationType.getRotationType(condition.get("target_rotation").toString());

			Vec3 actorRotation = actorRotationType.getRotation(nmsActor);
			Vec3 targetRotation = targetRotationType.getRotation(nmsTarget);

			ArrayList<String> strings = new ArrayList<>();
			if (condition.containsKey("axes")) {
				for (Object object : ((JSONArray) condition.get("axes"))) {
					strings.add(object.toString());
				}
			} else {
				ArrayList<String> deSt = new ArrayList<>();
				deSt.add("x");
				deSt.add("y");
				deSt.add("z");
				strings.addAll(deSt);
			}

			EnumSet<Direction.Axis> axes = EnumSet.noneOf(Direction.Axis.class);
			strings.forEach(axis -> axes.add(Direction.Axis.valueOf(axis)));

			actorRotation = RotationType.reduceAxes(actorRotation, axes);
			targetRotation = RotationType.reduceAxes(targetRotation, axes);
			String comparison = condition.get("comparison").toString();
			double compare_to = Double.parseDouble(condition.get("compare_to").toString());

			return Comparison.getFromString(comparison).compare(RotationType.getAngleBetween(actorRotation, targetRotation), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("attack_target"), (condition, pair) -> {
			net.minecraft.world.entity.Entity craftActor = pair.first().getHandle();
			net.minecraft.world.entity.Entity craftTarget = pair.second().getHandle();

			return (craftActor instanceof Mob mobActor && craftTarget.equals(mobActor.getTarget())) || (craftActor instanceof NeutralMob angerableActor && craftTarget.equals(angerableActor.getTarget()));
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("attacker"), (condition, pair) -> {
			net.minecraft.world.entity.Entity craftActor = pair.first().getHandle();
			net.minecraft.world.entity.Entity craftTarget = pair.second().getHandle();

			return craftTarget instanceof LivingEntity livingEntity && craftActor.equals(livingEntity.lastHurtByMob);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("distance"), (condition, pair) -> {
			String comparison = condition.get("comparison").toString();
			double compare_to = Double.parseDouble(condition.get("compare_to").toString());
			return Comparison.getFromString(comparison).compare(pair.first().getHandle().position().distanceToSqr(pair.second().getHandle().position()), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_set"), (condition, pair) -> {
			return EntitySetPower.isInEntitySet(pair.second(), condition.get("set").toString());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("can_see"), (condition, pair) -> {
			if (pair.first() instanceof Player pl) {
				return pl.canSee(pair.second());
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("owner"), (condition, pair) -> {
			if (pair.second() instanceof Tameable tameable) {
				return tameable.getOwner().equals(pair.first());
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, pair) -> {
			return pair.first().getPassengers().contains(pair.second());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_root"), (condition, pair) -> {
			for (int i = 0; i < pair.first().getPassengers().toArray().length; i++) {
				if (pair.first().getPassengers().isEmpty()) return false;
				if (pair.first().getPassengers().get(i) != null) {
					return i == pair.first().getPassengers().toArray().length;
				} else {
					return false;
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, pair) -> {
			return pair.second().getPassengers().contains(pair.first());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("equals"), (condition, pair) -> {
			return pair.first() == pair.second();
		}));
	}

	private void register(ConditionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registerable {
		NamespacedKey key;
		BiPredicate<JSONObject, Pair<CraftEntity, CraftEntity>> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, Pair<CraftEntity, CraftEntity>> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(JSONObject condition, Pair<CraftEntity, CraftEntity> tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey getKey() {
			return key;
		}
	}
}
