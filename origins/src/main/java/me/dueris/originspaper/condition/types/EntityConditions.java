package me.dueris.originspaper.condition.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.condition.Conditions;
import me.dueris.originspaper.condition.meta.MetaConditions;
import me.dueris.originspaper.condition.types.multi.DistanceFromCoordinatesConditionRegistry;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.power.ResourcePower;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntityConditions {
	private static final Location[] prevLoca = new Location[100000];

	public static void registerAll() {
		MetaConditions.register(Registries.ENTITY_CONDITION, EntityConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("power_type"),
			InstanceDefiner.instanceDefiner()
				.add("power_type", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				ResourceLocation location = data.getId("power_type");
				for (PowerType powerType : PowerHolderComponent.getPowers(entity.getBukkitEntity())) {
					if (powerType.getType().equalsIgnoreCase(location.toString())) {
						return true;
					}
				}
				return false;
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("power"),
			InstanceDefiner.instanceDefiner()
				.add("power", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				ResourceLocation location = data.getId("power");
				return PowerHolderComponent.hasPower(entity.getBukkitEntity(), location.toString());
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("origin"),
			InstanceDefiner.instanceDefiner()
				.add("origin", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				ResourceLocation location = data.getId("origin");
				return entity.getBukkitEntity() instanceof org.bukkit.entity.Player p && PowerHolderComponent.hasOrigin(p, location.toString());
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("power_active"),
			InstanceDefiner.instanceDefiner()
				.add("power", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (!(entity instanceof Player player)) return false;
				PowerType found = PowerHolderComponent.getPower(entity.getBukkitEntity(), data.getId("power").toString());
				return found != null && found.isActive(player);
			}
		));
		DistanceFromCoordinatesConditionRegistry.registerEntityCondition(EntityConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("resource"),
			InstanceDefiner.instanceDefiner()
				.add("resource", SerializableDataTypes.IDENTIFIER)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> {
				String resource = data.getId("resource").toString();
				Optional<ResourcePower.Bar> bar = ResourcePower.getDisplayedBar(entity, resource);
				if (bar.isPresent()) {
					return bar.get().meetsComparison(data.get("comparison"), data.getInt("compare_to"));
				}
				// We do a manual check of this as a backup for when people check for a non-functioning/displaying resource
				// By checking the serverloaded bars(after we define that its not displayed) and seeing if the origin wants to check
				// if its value is 0, then it would be true in apoli.
				return ResourcePower.serverLoadedBars.containsKey(resource) && data.get("comparison").equals(Comparison.EQUAL) && data.getInt("compare_to") == 0;
			}
		));

		Conditions.registerPackage(EntityConditions::register, "me.dueris.originspaper.factory.condition.types.entity");
	}

	public static void register(@NotNull ConditionFactory<net.minecraft.world.entity.Entity> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION).register(factory, factory.getSerializerId());
	}

	public static boolean isEntityMovingHorizontal(@NotNull Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getX() != prevLocat.getX() || cuLo.getZ() != cuLo.getZ();
	}

	public static boolean isEntityMovingVertical(@NotNull Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getY() != prevLocat.getY();
	}
}
