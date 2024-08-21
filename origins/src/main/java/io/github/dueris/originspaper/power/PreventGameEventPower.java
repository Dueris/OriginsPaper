package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class PreventGameEventPower extends PowerType {
	private final LinkedList<Holder<GameEvent>> events;
	private final ActionFactory<Entity> entityAction;
	private final TagKey<GameEvent> eventTag;

	public PreventGameEventPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								 Holder<GameEvent> event, List<Holder<GameEvent>> events, TagKey<GameEvent> eventTag, ActionFactory<Entity> entityAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);

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

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_game_event"))
			.add("event", SerializableDataTypes.GAME_EVENT_ENTRY, null)
			.add("events", SerializableDataBuilder.of(SerializableDataTypes.GAME_EVENT_ENTRY.listOf()), null)
			.add("tag", SerializableDataTypes.GAME_EVENT_TAG, null)
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	public void executeAction(Entity entity) {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

	public boolean doesPrevent(Holder<GameEvent> event) {
		return (eventTag != null && event.is(eventTag))
			|| (events != null && events.contains(event));
	}
}
