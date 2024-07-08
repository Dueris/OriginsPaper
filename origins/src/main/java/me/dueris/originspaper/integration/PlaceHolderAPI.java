package me.dueris.originspaper.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceHolderAPI extends PlaceholderExpansion {
	private final OriginsPaper plugin;

	public PlaceHolderAPI(OriginsPaper plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "origins";
	}

	@Override
	public @NotNull String getAuthor() {
		return "dueris";
	}

	@Override
	public @NotNull String getVersion() {
		return OriginsPaper.pluginVersion;
	}

	@Override
	public @Nullable String getRequiredPlugin() {
		return "OriginsPaper";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
		return onPlaceholderRequest(player.getPlayer(), params);
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
		if (params.equalsIgnoreCase("player_origin")) {
			String done = "";
			StringBuilder builder = new StringBuilder(done);
			for (Origin origin : PowerHolderComponent.getOrigin(player).values()) {
				builder.append(origin.getTag() + "//");
			}
			return done;
		}
		if (params.equalsIgnoreCase("player_layer")) {
			String done = "";
			StringBuilder builder = new StringBuilder(done);
			for (Origin origin : PowerHolderComponent.getOrigin(player).values()) {
				builder.append(origin.getTag() + "//");
			}
			return done;
		}
		if (params.equalsIgnoreCase("player_origin_data")) {
			return OriginDataContainer.getLayer(player);
		}
		if (params.equalsIgnoreCase("all_origins")) {
			return CraftApoli.getOriginsFromRegistry().toString();
		}
		if (params.equalsIgnoreCase("all_layers")) {
			return CraftApoli.getLayersFromRegistry().toString();
		}

		return "wtf"; // Unkown placeholder
	}
}
