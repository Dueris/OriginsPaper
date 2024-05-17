package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.GenericGameEvent;

public class GameEventListener extends PowerType {

	private final String event;
	private final FactoryJsonObject entityAction;

	public GameEventListener(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String event, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		if (event.contains(":")) {
			event = event.split(":")[1];
		}
		this.event = event;
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("game_event_listener"))
			.add("event", String.class, new RequiredInstance())
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void event(GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!this.getPlayers().contains(p)) return;
			if (isActive(p) && e.getEvent().toString().equals(event)) {
				Actions.executeEntity(e.getEntity(), entityAction);
			}
		}
	}

}
