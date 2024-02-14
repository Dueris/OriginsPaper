package me.dueris.genesismc.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.util.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.storage.OriginDataContainer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiExtension extends PlaceholderExpansion {
    private final GenesisMC plugin;

    public PlaceholderApiExtension(GenesisMC plugin) {
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
        return GenesisMC.pluginVersion;
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return "GenesisMC";
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
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
                done = done + origin.getTag() + "//";
            }
            return done;
        }
        if (params.equalsIgnoreCase("player_layer")) {
            String done = "";
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
                done = done + origin.getLayerTag() + "//";
            }
            return done;
        }
        if (params.equalsIgnoreCase("player_origin_data")) {
            return OriginDataContainer.getLayer(player);
        }
        if (params.equalsIgnoreCase("all_origins")) {
            return CraftApoli.getOrigins().toString();
        }
        if (params.equalsIgnoreCase("all_layers")) {
            return CraftApoli.getLayers().toString();
        }

        return "wtf"; // Unkown placeholder
    }
}
