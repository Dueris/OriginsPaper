package me.dueris.genesismc.core.factory.handlers;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
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

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static me.dueris.genesismc.core.factory.CraftApoli.nullOrigin;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;

public class CustomOriginExistCheck implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        customOriginExistCheck(e.getPlayer());
    }

    public static void customOriginExistCheck(Player p) {
        if(OriginPlayer.getOrigin(p) == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Error getting " + p.getName() + "'s PersistentOriginData!");
            String origintag = p.getPersistentDataContainer().get(new NamespacedKey(getPlugin(), "origintag"), PersistentDataType.STRING);
            if(CraftApoli.getOriginTags().contains(origintag)){
                if(p.getPersistentDataContainer().get(new NamespacedKey(getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY) == null) p.getPersistentDataContainer().set(new NamespacedKey(getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(CraftApoli.getOrigin(origintag)));
            }
        }else{
            if (OriginPlayer.getOrigin(p).getTag().equals(new CraftApoli().nullOrigin().getTag())) return;
            if (CraftApoli.getOriginTags().contains(OriginPlayer.getOrigin(p).getTag())) return;
        }
        p.getPersistentDataContainer().set(new NamespacedKey(getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(nullOrigin()));
        p.sendMessage(Component.text("Your origin has been removed! Please select a new one.").color(TextColor.fromHexString(RED)));
        p.sendMessage(Component.text("If you believe this is a mistake please contact your server admin(s).").color(TextColor.fromHexString(RED)));
    }
}
