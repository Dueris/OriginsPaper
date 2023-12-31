package me.dueris.genesismc.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ErrorSystem {
    public void throwError(String error, String power, Player player, OriginContainer origin, LayerContainer layer) {
        Bukkit.getLogger().warning("[Origins-GenesisMC] Generated custom-origin exception at power:[%power%] with error of %error%".replace("%error%", error).replace("%power%", power));
        Bukkit.getLogger().warning("Player:[%player%], Origin:[%origin%], Layer:[%layer%]".replace("%player%", player.getName()).replace("%origin%", origin.getTag()).replace("%layer%", layer.getTag()));
    }
}
