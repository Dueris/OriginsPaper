package me.dueris.genesismc.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ErrorSystem {
    public void throwError(String error, String power, Player player, LayerContainer layer) {
        Bukkit.getLogger().warning("[Origins-GenesisMC] Generated custom-origin exception at Power:[%power%] with error of %error%".replace("%error%", error).replace("%power%", power));
        Bukkit.getLogger().warning("Player:[%player%], Layer:[%layer%]".replace("%player%", player.getName()).replace("%layer%", layer.getTag()));
    }
}
