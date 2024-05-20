package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PreventGameEvent extends PowerType implements Listener {
	private final Set<String> events;

	public PreventGameEvent(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String event, FactoryJsonArray events) {
		super(name, description, hidden, condition, loading_priority);
		this.events = events == null ? Arrays.stream(new String[]{event}).collect(Collectors.toSet()) : events.asList.stream().map(FactoryElement::getString).collect(Collectors.toSet());
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_game_event"))
			.add("event", String.class, new OptionalInstance())
			.add("events", FactoryJsonArray.class, new OptionalInstance());
	}

	@EventHandler
	public void event(GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!getPlayers().contains(p)) return;
			if (isActive(p)) {
				if (events.contains(e.getEvent().key().asString())) {
					e.setCancelled(true);
				}
			}
		}
	}

}
