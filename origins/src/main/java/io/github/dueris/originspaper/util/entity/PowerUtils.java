package io.github.dueris.originspaper.util.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
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

public class PowerUtils {
	public static Gson GSON = new Gson();

	public static void removePower(CommandSender executor, PowerType powerType, Player p, OriginLayer layer, boolean suppress) {
		ArrayList<PowerType> powersToEdit = new ArrayList<>();
		powersToEdit.add(powerType);
		powersToEdit.addAll(PowerHolderComponent.getNestedPowerTypes(powerType));
		for (PowerType power : powersToEdit) {
			if (PowerHolderComponent.hasPower(p, power.getTag())) {
				PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) p).getHandle()).removePower(power, layer);
				PowerHolderComponent.unloadPower(p, power, layer, suppress, true);
				if (!suppress) {
					executor.sendMessage("Entity %name% had the power %power% removed"
						.replace("%power%", PlainTextComponentSerializer.plainText().serialize(power.name()))
						.replace("%name%", p.getName())
					);
				}
			}
		}
	}

	public static void grantPower(CommandSender executor, PowerType powerType, Player p, OriginLayer layer, boolean suppress) {
		ArrayList<PowerType> powersToEdit = new ArrayList<>();
		powersToEdit.add(powerType);
		powersToEdit.addAll(PowerHolderComponent.getNestedPowerTypes(powerType));
		for (PowerType power : powersToEdit) {
			if (!PowerHolderComponent.hasPower(p, power.getTag())) {
				PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) p).getHandle()).addPower(power, layer);
				PowerHolderComponent.loadPower(p, power, layer, suppress, true);
				if (!suppress) {
					executor.sendMessage("Entity %name% was granted the power %power%"
						.replace("%power%", PlainTextComponentSerializer.plainText().serialize(power.name()))
						.replace("%name%", p.getName())
					);
				}
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
