package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class PreventGameEventPowerType extends PowerType {

	private final List<Holder<GameEvent>> events;
	private final TagKey<GameEvent> eventTag;

	private final Consumer<Entity> entityAction;

	public PreventGameEventPowerType(Power power, LivingEntity entity, Holder<GameEvent> event, List<Holder<GameEvent>> events, TagKey<GameEvent> eventTag, Consumer<Entity> entityAction) {
		super(power, entity);

		this.events = new LinkedList<>();
		this.eventTag = eventTag;
		this.entityAction = entityAction;

		if (event != null) {
			this.events.add(event);
		}

		if (events != null) {
			this.events.addAll(events);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_game_event"),
			new SerializableData()
				.add("event", SerializableDataTypes.GAME_EVENT_ENTRY, null)
				.add("events", SerializableDataTypes.GAME_EVENT_ENTRIES, null)
				.add("tag", SerializableDataTypes.GAME_EVENT_TAG, null)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new PreventGameEventPowerType(power, entity,
				data.get("event"),
				data.get("events"),
				data.get("tag"),
				data.get("entity_action")
			)
		).allowCondition();
	}

	public void executeAction() {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

	public boolean doesPrevent(Holder<GameEvent> event) {
		return (eventTag != null && event.is(eventTag))
			|| (events != null && events.contains(event));
	}
}
