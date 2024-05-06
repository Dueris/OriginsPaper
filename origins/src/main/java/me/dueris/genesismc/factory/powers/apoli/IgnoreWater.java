package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;

// TODO: lets do more work on this lol
public class IgnoreWater extends CraftPower implements Listener {

	@Override
	public void run(Player p, Power power) {
		if (p.isInWaterOrBubbleColumn() && getPlayersWithPower().contains(p)) {
			p.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(0.638);
		} else {
			p.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(0.1);
		}
	}

	@EventHandler
	public void jumpVelocity(PlayerJumpEvent e) {
		Player p = e.getPlayer();
		if (p.isInWaterOrBubbleColumn() && getPlayersWithPower().contains(p)) {
			p.setVelocity(p.getVelocity().add(new Vector(0, 13, 0)));
		}
	}

	@Override
	public String getType() {
		return "apoli:ignore_water";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return ignore_water;
	}
}
