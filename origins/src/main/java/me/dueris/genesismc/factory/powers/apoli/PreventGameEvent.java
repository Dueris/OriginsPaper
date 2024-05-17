package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

public class PreventGameEvent extends PowerType implements Listener {
	private final NamespacedKey event;

	public PreventGameEvent(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, NamespacedKey event) {
		super(name, description, hidden, condition, loading_priority);
		this.event = event;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_game_event"))
			.add("event", NamespacedKey.class, new RequiredInstance());
	}

	@EventHandler
	public void event(GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!getPlayers().contains(p)) return;
			if (isActive(p)) {
				if (e.getEvent().key().equals(event)) {
					e.setCancelled(true);
				}
			}
		}
	}

}
