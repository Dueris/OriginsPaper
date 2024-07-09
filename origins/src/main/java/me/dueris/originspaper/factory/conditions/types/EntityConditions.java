package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.powers.apoli.Resource;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiPredicate;

public class EntityConditions {
	private final Location[] prevLoca = new Location[100000];

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power_type"), (condition, entity) -> {
			for (PowerType c : PowerHolderComponent.getPowers(entity)) {
				if (c.getType().equals(condition.getString("power_type").replace("origins:", "apoli:"))) { // Apoli remapping
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power"), (condition, entity) -> {
			for (PowerType c : PowerHolderComponent.getPowers(entity)) {
				if (c.getTag().equals(condition.getString("power"))) {
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("origin"), (condition, entity) -> entity instanceof Player p && PowerHolderComponent.hasOrigin(p, condition.getString("origin"))));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power_active"), (condition, entity) -> {
			String power = condition.getString("power");
			PowerType found = PowerHolderComponent.getPower(entity, power);
			return found != null && found.isActive((Player) entity);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("resource"), (condition, entity) -> {
			Optional<Resource.Bar> bar = Resource.getDisplayedBar(entity, condition.getString("resource"));
			if (bar.isPresent()) {
				return bar.get().meetsComparison(Comparison.fromString(condition.getString("comparison")), condition.getNumber("compare_to").getInt());
			}
			// We do a manual check of this as a backup for when people check for a non-functioning/displaying resource
			// By checking the serverloaded bars(after we define that its not displayed) and seeing if the origin wants to check
			// if its value is 0, then it would be true in apoli.
			return Resource.serverLoadedBars.containsKey(condition.getString("resource")) && condition.getString("comparison").equalsIgnoreCase("==") && condition.getNumber("compare_to").getInt() == 0;
		}));
	}

	public void register(EntityConditions.ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION).register(factory);
	}

	public boolean isEntityMoving(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat != null) cuLo.setDirection(prevLocat.getDirection()); // Ignore direction changes

		return !cuLo.equals(prevLocat);
	}

	public boolean isEntityMovingHorizontal(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getX() != prevLocat.getX() || cuLo.getZ() != cuLo.getZ();
	}

	public boolean isEntityMovingVertical(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getY() != prevLocat.getY();
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, CraftEntity> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, CraftEntity> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, CraftEntity tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
