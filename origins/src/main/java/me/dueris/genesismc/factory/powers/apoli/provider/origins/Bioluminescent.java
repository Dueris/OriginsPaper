package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Bioluminescent extends CraftPower implements Listener, PowerProvider {
	public static ArrayList<Player> players = new ArrayList<>();
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("allay_sparkle_light");

	@Override
	public void run(Player p) {
		// WE ARE ALIVE
	}

	@Override
	public String getPowerFile() {
		return null;
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return players;
	}

}
