package me.dueris.genesismc.core.factory.handlers;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class CustomOriginExistCheck implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        customOriginExistCheck(e.getPlayer());
    }

    public static void customOriginExistCheck(Player p) {
        if (OriginPlayer.getOrigin(p) == new CraftApoliRewriten().nullOrigin()) return;
        if (CraftApoliRewriten.getOriginTags().contains(OriginPlayer.getOrigin(p).getTag())) return;
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoliRewriten.toByteArray(CraftApoliRewriten.nullOrigin()));
        p.sendMessage(Component.text(ChatColor.RED + "Your origin has been removed! Please select a new one."));
        p.sendMessage(Component.text(ChatColor.RED + "If you believe this is a mistake please contact your server admin(s)."));
    }
}
