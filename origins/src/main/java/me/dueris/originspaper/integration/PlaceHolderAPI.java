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

	@NotNull
	public String getIdentifier() {
		return "origins";
	}

	@NotNull
	public String getAuthor() {
		return "dueris";
	}

	@NotNull
	public String getVersion() {
		return OriginsPaper.pluginVersion;
	}

	@Nullable
	public String getRequiredPlugin() {
		return "OriginsPaper";
	}

	public boolean persist() {
		return true;
	}

	public boolean canRegister() {
		return true;
	}

	@Nullable
	public String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
		return this.onPlaceholderRequest(player.getPlayer(), params);
	}

	@Nullable
	public String onPlaceholderRequest(Player player, @NotNull String params) {
		if (params.equalsIgnoreCase("player_origin")) {
			String done = "";
			StringBuilder builder = new StringBuilder(done);

			for (Origin origin : PowerHolderComponent.getOrigin(player).values()) {
				builder.append(origin.getTag() + "//");
			}

			return done;
		} else if (!params.equalsIgnoreCase("player_layer")) {
			if (params.equalsIgnoreCase("player_origin_data")) {
				return OriginDataContainer.getLayer(player);
			} else if (params.equalsIgnoreCase("all_origins")) {
				return CraftApoli.getOriginsFromRegistry().toString();
			} else {
				return params.equalsIgnoreCase("all_layers") ? CraftApoli.getLayersFromRegistry().toString() : "wtf";
			}
		} else {
			String done = "";
			StringBuilder builder = new StringBuilder(done);

			for (Origin origin : PowerHolderComponent.getOrigin(player).values()) {
				builder.append(origin.getTag() + "//");
			}

			return done;
		}
	}
}
