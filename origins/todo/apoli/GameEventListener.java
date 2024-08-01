package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GameEventListener extends PowerType {

	private final Set<String> events;
	private final FactoryJsonObject entityAction;

	public GameEventListener(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, @NotNull String event, FactoryJsonArray events, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		if (event.contains(":")) {
			event = event.split(":")[1];
		}
		this.events = events == null ? Arrays.stream(new String[]{event}).collect(Collectors.toSet()) : events.asList.stream().map(FactoryElement::getString).collect(Collectors.toSet());
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("game_event_listener"))
			.add("event", String.class, new OptionalInstance())
			.add("events", FactoryJsonArray.class, new OptionalInstance())
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void event(@NotNull GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!this.getPlayers().contains(p)) return;
			if (isActive(p) && events.contains(e.getEvent().toString())) {
				Actions.executeEntity(e.getEntity(), entityAction);
			}
		}
	}

}
