package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PreventGameEventPowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventGameEventPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("event", SerializableDataTypes.GAME_EVENT_ENTRY, null)
			.addFunctionedDefault("events", SerializableDataTypes.GAME_EVENT_ENTRIES, data -> Util.singletonListOrEmpty(data.get("event")))
			.add("event_tag", SerializableDataTypes.GAME_EVENT_TAG.optional(), Optional.empty()),
		(data, condition) -> new PreventGameEventPowerType(
			data.get("entity_action"),
			data.get("events"),
			data.get("event_tag"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("events", powerType.events)
			.set("event_tag", powerType.eventTag)
	);

	private final Optional<EntityAction> entityAction;

	private final List<Holder<GameEvent>> events;
	private final Optional<TagKey<GameEvent>> eventTag;

	public PreventGameEventPowerType(Optional<EntityAction> entityAction, List<Holder<GameEvent>> events, Optional<TagKey<GameEvent>> eventTag, Optional<EntityCondition> condition) {
		super(condition);
		this.entityAction = entityAction;
		this.events = events
			.stream()
			.distinct()
			.collect(Collectors.toCollection(ObjectArrayList::new));
		this.eventTag = eventTag;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_GAME_EVENT;
	}

	public void executeAction() {
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

	public boolean doesPrevent(Holder<GameEvent> event) {
		return eventTag.map(event::is).orElse(false)
			|| (events.isEmpty() || events.contains(event));
	}

}
