package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GameEventListenerPowerType extends CooldownPowerType implements VibrationSystem {

	public static final TypedDataObjectFactory<GameEventListenerPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("block_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("event", SerializableDataTypes.GAME_EVENT_ENTRY.optional(), Optional.empty())
			.add("events", SerializableDataTypes.GAME_EVENT_ENTRIES.optional(), Optional.empty())
			.add("event_tag", SerializableDataTypes.GAME_EVENT_TAG.optional(), Optional.empty())
			.add("trigger_order", SerializableDataType.enumValue(GameEventListener.DeliveryMode.class), GameEventListener.DeliveryMode.UNSPECIFIED)
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.POSITIVE_INT, 1)
			.add("show_particle", SerializableDataTypes.BOOLEAN, true)
			.add("range", SerializableDataTypes.POSITIVE_INT, 16),
		(data, condition) -> new GameEventListenerPowerType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			data.get("block_action"),
			data.get("block_condition"),
			data.get("event"),
			data.get("events"),
			data.get("event_tag"),
			data.get("trigger_order"),
			data.get("hud_render"),
			data.get("cooldown"),
			data.get("show_particle"),
			data.get("range"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("block_action", powerType.blockAction)
			.set("block_condition", powerType.blockCondition)
			.set("event", powerType.gameEvent)
			.set("events", powerType.gameEvents)
			.set("event_tag", powerType.gameEventTag)
			.set("trigger_order", powerType.triggerOrder)
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
			.set("show_particle", powerType.showParticle)
			.set("range", powerType.range)
	);

	private final Optional<BiEntityAction> biEntityAction;
	private final Optional<BlockAction> blockAction;

	private final Optional<BiEntityCondition> biEntityCondition;
	private final Optional<BlockCondition> blockCondition;

	private final Optional<Holder<GameEvent>> gameEvent;
	private final Optional<List<Holder<GameEvent>>> gameEvents;

	private final ObjectOpenHashSet<Holder<GameEvent>> allGameEvents;
	private final Optional<TagKey<GameEvent>> gameEventTag;

	private final GameEventListener.DeliveryMode triggerOrder;

	private final ListenerData vibrationListenerData;
	private final Callback vibrationCallback;

	private final boolean showParticle;
	private final int range;

	private DynamicGameEventListener<net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Listener> gameEventHandler;

	public GameEventListenerPowerType(Optional<BiEntityAction> biEntityAction, Optional<BlockAction> blockAction, Optional<BiEntityCondition> biEntityCondition, Optional<BlockCondition> blockCondition, Optional<Holder<GameEvent>> gameEvent, Optional<List<Holder<GameEvent>>> gameEvents, Optional<TagKey<GameEvent>> gameEventTag, GameEventListener.DeliveryMode triggerOrder, HudRender hudRender, int cooldownDuration, boolean showParticle, int range, Optional<EntityCondition> condition) {
		super(cooldownDuration, hudRender, condition);

		this.biEntityAction = biEntityAction;
		this.blockAction = blockAction;

		this.biEntityCondition = biEntityCondition;
		this.blockCondition = blockCondition;

		this.gameEvent = gameEvent;
		this.gameEvents = gameEvents;

		this.allGameEvents = new ObjectOpenHashSet<>();
		this.gameEventTag = gameEventTag;

		this.gameEvent.ifPresent(this.allGameEvents::add);
		this.gameEvents.ifPresent(this.allGameEvents::addAll);

		this.allGameEvents.trim();
		this.triggerOrder = triggerOrder;

		this.vibrationListenerData = new ListenerData();
		this.vibrationCallback = new Callback();

		this.showParticle = showParticle;
		this.range = range;

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.GAME_EVENT_LISTENER;
	}

	@Override
	public void onAdded() {

		if (getHolder().level() instanceof ServerLevel serverWorld) {
			getGameEventHandler().move(serverWorld);
		}

	}

	@Override
	public void onRemoved() {

		if (getHolder().level() instanceof ServerLevel serverWorld) {
			getGameEventHandler().remove(serverWorld);
		}

	}

	@Override
	public void serverTick() {

		if (canUse()) {
			Ticker.tick(getHolder().level(), getVibrationData(), getVibrationUser());
		}

	}

	@Override
	public boolean canUse() {
		return gameEventHandler != null && super.canUse();
	}

	@Override
	public @NotNull Data getVibrationData() {
		return vibrationListenerData;
	}

	@Override
	public Callback getVibrationUser() {
		return vibrationCallback;
	}

	public DynamicGameEventListener<VibrationSystem.Listener> getGameEventHandler() {

		if (gameEventHandler == null) {
			gameEventHandler = new DynamicGameEventListener<>(new GameEventListenerPowerType.Listener());
		}

		return gameEventHandler;

	}

	public boolean shouldShowParticle() {
		return showParticle;
	}

	public class Listener extends net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Listener {

		public Listener() {
			super(GameEventListenerPowerType.this);
		}

		@Override
		public DeliveryMode getDeliveryMode() {
			return triggerOrder;
		}

	}

	public class Callback implements VibrationSystem.User {

		@Override
		public int getListenerRadius() {
			return range;
		}

		@Override
		public PositionSource getPositionSource() {
			LivingEntity holder = getHolder();
			return new EntityPositionSource(holder, holder.getEyeHeight(holder.getPose()));
		}

		@Override
		public boolean canReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, GameEvent.Context emitter) {
			return blockCondition.map(condition -> condition.test(world, pos)).orElse(true)
				&& biEntityCondition.map(condition -> condition.test(emitter.sourceEntity(), getHolder())).orElse(true);
		}

		@Override
		public void onReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {

			GameEventListenerPowerType.this.use();

			blockAction.ifPresent(action -> action.execute(world, pos, Optional.empty()));
			biEntityAction.ifPresent(action -> action.execute(sourceEntity, getHolder()));

		}

		@Override
		public TagKey<GameEvent> getListenableEvents() {
			return gameEventTag.orElse(VibrationSystem.User.super.getListenableEvents());
		}

		public boolean shouldAccept(Holder<GameEvent> gameEvent) {
			return GameEventListenerPowerType.this.canUse()
				&& this.isAccepted(gameEvent);
		}

		public boolean isAccepted(Holder<GameEvent> gameEvent) {
			return gameEventTag.map(gameEvent::is).orElse(true)
				&& (allGameEvents.isEmpty() || allGameEvents.contains(gameEvent));
		}

	}

	public class ListenerData extends VibrationSystem.Data {

		public boolean shouldShowParticle() {
			return GameEventListenerPowerType.this.shouldShowParticle();
		}

	}

}
