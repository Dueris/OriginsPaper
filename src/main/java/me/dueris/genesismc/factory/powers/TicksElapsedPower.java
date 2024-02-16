package me.dueris.genesismc.factory.powers;

import org.bukkit.entity.Player;

import java.util.HashMap;

public interface TicksElapsedPower {
    public void run(Player p, HashMap<Player, Integer> ticksEMap);
}
