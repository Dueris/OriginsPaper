package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DisableRegeneration extends PowerType implements Listener {

	@Register
	public DisableRegeneration(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("disable_regen"));
	}

	@EventHandler
	public void disable(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (isActive(p)) {
					if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
						e.setAmount(0);
						e.setCancelled(true);
					}
				}
			}
		}
	}

}
