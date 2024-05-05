package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.factory.powers.genesismc.GravityPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class LikeWater extends CraftPower implements Listener, PowerProvider {
	private static final GravityPower gravityHook = new GravityPower();
	public static ArrayList<Player> likeWaterPlayers = new ArrayList<>();
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("like_water");

	@Override
	public void run(Player p, Power power) {
		if (this.getPlayersWithPower().contains(p))
			p.setGravity(!p.isInWaterOrBubbleColumn() && !p.isSwimming() && !p.isSneaking());
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return likeWaterPlayers;
	}

}
