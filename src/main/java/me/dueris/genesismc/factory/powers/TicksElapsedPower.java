package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.registry.PowerContainer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface TicksElapsedPower {
    public void run(Player p, HashMap<Player, HashMap<PowerContainer, Integer>> ticksEMap);
}
