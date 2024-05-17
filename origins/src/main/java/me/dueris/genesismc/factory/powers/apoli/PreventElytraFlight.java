package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class PreventElytraFlight extends PowerType implements Listener {
	private final FactoryJsonObject entityAction;

	public PreventElytraFlight(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_elytra_flight"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void run(EntityToggleGlideEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (isActive(p)) {
					e.setCancelled(true);
					Actions.executeEntity(p, entityAction);
				}
			}
		}
	}

}
