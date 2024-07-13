package me.dueris.originspaper.factory.conditions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.data.types.RotationType;
import me.dueris.originspaper.factory.powers.apoli.EntitySetPower;
import me.dueris.originspaper.factory.powers.apoli.PreventEntityRender;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

import static me.dueris.originspaper.factory.data.types.RotationType.*;

public class BiEntityConditions implements Listener {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("both"), (data, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.second())); // target

			return a.get() && t.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("either"), (data, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true);
			AtomicBoolean t = new AtomicBoolean(true);
			a.set(ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.first())); // actor
			t.set(ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.second())); // target

			return a.get() || t.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("invert"), (data, pair) -> {
			return ConditionExecutor.testBiEntity(data.getJsonObject("condition"), pair.second(), pair.first());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("undirected"), (data, pair) -> {
			AtomicBoolean a = new AtomicBoolean(true); // Not swapped
			AtomicBoolean b = new AtomicBoolean(true); // Swapped

			a.set(ConditionExecutor.testBiEntity(data.getJsonObject("condition"), pair.first(), pair.second())); // actor, target
			b.set(ConditionExecutor.testBiEntity(data.getJsonObject("condition"), pair.second(), pair.first())); // target, actor

			return a.get() || b.get();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("actor_condition"), (data, pair) -> ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.first())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("target_condition"), (data, pair) -> ConditionExecutor.testEntity(data.getJsonObject("condition"), pair.second())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("equal"), (data, pair) -> pair.first() == pair.second()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_entity_set"), (data, pair) -> EntitySetPower.isInEntitySet(pair.second(), data.getString("set"))));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("can_see"), (data, pair) -> PreventEntityRender.canSeeEntity(pair.first(), pair.second(), data)));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("distance"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if (actor == null || target == null) {
				return false;
			}

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			double compareTo = data.getNumber("compare_to").getDouble();

			compareTo *= compareTo;
			return comparison.compare(actor.position().distanceToSqr(target.position()), compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("owner"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();
			System.out.println(actor.getType());

			if (actor == null || target == null) {
				return false;
			}

			return (target instanceof OwnableEntity tamable && actor.equals(tamable.getOwner()))
				|| (target instanceof TraceableEntity ownable && actor.equals(ownable.getOwner()));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if (actor == null || target == null) {
				return false;
			}

			return actor.getVehicle() != null
				&& actor.getVehicle().equals(target);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding_root"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if ((actor == null || target == null) || !actor.isPassenger()) {
				return false;
			}

			return actor.getRootVehicle().equals(target);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding_recursive"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if ((actor == null || target == null) || !actor.isPassenger()) {
				return false;
			}

			Entity vehicle = actor.getVehicle();
			while (vehicle != null && !vehicle.equals(target)) {
				vehicle = vehicle.getVehicle();
			}

			return target.equals(vehicle);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attack_target"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if (actor == null || target == null) {
				return false;
			}

			return (actor instanceof Mob mobActor && target.equals(mobActor.getTarget()))
				|| (actor instanceof NeutralMob angerableActor && target.equals(angerableActor.getTarget()));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attacker"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if (actor == null || target == null) {
				return false;
			}

			return target instanceof LivingEntity livingTarget
				&& actor.equals(livingTarget.getLastHurtByMob());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("relative_rotation"), (data, pair) -> {
			Entity actor = pair.left().getHandle();
			Entity target = pair.right().getHandle();

			if (actor == null || target == null) {
				return false;
			}

			RotationType actorRotationType = data.getEnumValueOrDefault("actor_rotation", RotationType.class, HEAD);
			RotationType targetRotationType = data.getEnumValueOrDefault("target_rotation", RotationType.class, BODY);

			Vec3 actorRotation = actorRotationType.getRotation(actor);
			Vec3 targetRotation = targetRotationType.getRotation(target);

			List<Direction.Axis> axesList = new ArrayList<>();
			if (data.isPresent("axes")) {
				axesList.addAll(data.getJsonArray("axes").asList().stream().map(FactoryElement::getString).map(String::toUpperCase).map(Direction.Axis::valueOf).toList());
			}

			EnumSet<Direction.Axis> axes = axesList.isEmpty() ? EnumSet.copyOf(Arrays.stream(Direction.Axis.values()).toList()) : EnumSet.copyOf(axesList);

			actorRotation = reduceAxes(actorRotation, axes);
			targetRotation = reduceAxes(targetRotation, axes);

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			double compareTo = data.getNumber("compare_to").getDouble();
			double angle = getAngleBetween(actorRotation, targetRotation);

			return comparison.compare(angle, compareTo);
		}));
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
