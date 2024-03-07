package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface TicksElapsedPower {
	public void run(Player p, HashMap<Player, HashMap<Power, Integer>> ticksEMap);
}
