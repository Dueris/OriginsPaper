package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class DisableRegeneration extends PowerType {

	public DisableRegeneration(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("disable_regen"));
	}

	@EventHandler
	public void disable(@NotNull EntityRegainHealthEvent e) {
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
