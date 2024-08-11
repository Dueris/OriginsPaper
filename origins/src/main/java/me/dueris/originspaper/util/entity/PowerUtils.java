package me.dueris.originspaper.util.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.dueris.originspaper.CraftApoli;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PowerUtils {
	public static Gson GSON = new Gson();

	public static void removePower(CommandSender executor, PowerType poweR, Player p, OriginLayer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		if (PowerHolderComponent.playerPowerMapping.getOrDefault(p, new ConcurrentHashMap<>()) != null) {
			ArrayList<PowerType> powersToEdit = new ArrayList<>();
			powersToEdit.add(poweR);
			powersToEdit.addAll(CraftApoli.getNestedPowerTypes(poweR));
			for (PowerType power : powersToEdit) {
				try {
					if (PowerHolderComponent.playerPowerMapping.get(p).get(layer).contains(power)) {
						PowerHolderComponent.playerPowerMapping.get(p).get(layer).remove(power);
						PowerHolderComponent.removePower(p, power, suppress, true);
						if (!suppress) {
							executor.sendMessage("Entity %name% had the power %power% removed"
								.replace("%power%", PlainTextComponentSerializer.plainText().serialize(power.name()))
								.replace("%name%", p.getName())
							);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void grantPower(CommandSender executor, PowerType power, Player p, OriginLayer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		if (!PowerHolderComponent.playerPowerMapping.getOrDefault(p, new ConcurrentHashMap<>()).get(layer).contains(power)) {
			PowerHolderComponent.playerPowerMapping.get(p).get(layer).add(power);
			PowerHolderComponent.applyPower(p, power, suppress, true);
			if (!suppress) {
				executor.sendMessage("Entity %name% was granted the power %power%"
					.replace("%power%", PlainTextComponentSerializer.plainText().serialize(power.name()))
					.replace("%name%", p.getName())
				);
			}
		}
	}

	public static void markGained(@NotNull PowerType power, @NotNull Player player) {
		JsonArray gainedPowers = new JsonArray();
		String toAdd = power.getTag();
		if (player.getPersistentDataContainer().has(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("gained_powers")))) {
			gainedPowers = GSON.fromJson(
				player.getPersistentDataContainer().get(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("gained_powers")), PersistentDataType.STRING),
				JsonArray.class
			);
		}

		Set<String> set = new HashSet<>();
		for (JsonElement element : gainedPowers) {
			set.add(element.getAsString());
		}

		if (!set.contains(toAdd)) {
			power.onGained(((CraftPlayer) player).getHandle());
			gainedPowers.add(new JsonPrimitive(toAdd));
		}
		player.getPersistentDataContainer().set(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("gained_powers")), PersistentDataType.STRING, gainedPowers.toString());
	}

	public static void markBlacklist(@NotNull PowerType power, @NotNull Player player) {
		JsonArray markedBlacklisted = new JsonArray();
		String toAdd = power.getTag();
		if (player.getPersistentDataContainer().has(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("blacklisted_powers")))) {
			markedBlacklisted = GSON.fromJson(
				player.getPersistentDataContainer().get(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("blacklisted_powers")), PersistentDataType.STRING),
				JsonArray.class
			);
		}

		Set<String> set = new HashSet<>();
		for (JsonElement element : markedBlacklisted) {
			set.add(element.getAsString());
		}

		if (!set.contains(toAdd)) {
			power.onLost(((CraftPlayer) player).getHandle());
			markedBlacklisted.add(new JsonPrimitive(toAdd));
		}
		player.getPersistentDataContainer().set(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("blacklisted_powers")), PersistentDataType.STRING, markedBlacklisted.toString());
	}

}
