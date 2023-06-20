package me.dueris.genesismc.core.factory.handlers;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static me.dueris.genesismc.core.factory.CraftApoli.nullOrigin;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;

public class CustomOriginExistCheck implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        customOriginExistCheck(e.getPlayer());
    }

    public static void customOriginExistCheck(Player p) {
        HashMap<String, OriginContainer> origins = OriginPlayer.getOrigin(p);
        for (OriginContainer origin : origins.values()){
            if (origin.getTag().equals(new CraftApoli().nullOrigin().getTag())) continue;
            if (CraftApoli.getOriginTags().contains(origin.getTag())) continue;
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "origins");
            HashMap<String, OriginContainer> playerOrigins = CraftApoli.toOriginContainer(p.getPersistentDataContainer().get(key, PersistentDataType.BYTE_ARRAY));
            playerOrigins.replace(OriginPlayer.getLayer(p, origin), CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(playerOrigins));
            p.sendMessage(Component.text("Your origin has been removed! Please select a new one.").color(TextColor.fromHexString(RED)));
            p.sendMessage(Component.text("If you believe this is a mistake please contact your server admin(s).").color(TextColor.fromHexString(RED)));
        }
    }
}
