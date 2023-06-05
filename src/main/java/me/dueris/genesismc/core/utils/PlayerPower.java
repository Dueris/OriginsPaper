package me.dueris.genesismc.core.utils;

import org.bukkit.entity.Player;

public class PlayerPower {

    Player player;
    PowerContainer power;

    public PlayerPower(Player player, PowerContainer power) {
        this.player = player;
        this.power = power;
    }

    public Player getPlayer() {
        return player;
    }

    public PowerContainer getPower() {
        return power;
    }
}
