package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class IgnoreWater extends PowerType {

	public IgnoreWater(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("ignore_water"));
	}

	@Override
	public void tick(Player p) {
		if (p.isInWaterOrBubbleColumn() && getPlayers().contains(p)) {
			p.setSprinting(false);
			p.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(0.628);
		} else p.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(0.1);
	}

	@EventHandler
	public void jumpVelocity(PlayerJumpEvent e) {
		Player p = e.getPlayer();
		if (p.isInWaterOrBubbleColumn() && getPlayers().contains(p)) {
			p.setVelocity(p.getVelocity().add(new Vector(0, 24, 0)));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (p.getVelocity().getY() < 0) {
						if (p.isOnGround()) cancel();
						else p.setVelocity(p.getVelocity().setY(p.getVelocity().getY() + -0.01));
					}
				}
			}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
		}
	}

}
