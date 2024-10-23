package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GameEventListenerPowerType extends CooldownPowerType implements VibrationSystem {

	private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;
	private final Consumer<Tuple<Entity, Entity>> biEntityAction;

	private final Predicate<BlockInWorld> blockCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

	private final List<Holder<GameEvent>> acceptedGameEvents;
	private final Optional<TagKey<GameEvent>> acceptedGameEventTag;

	private final GameEventListener.DeliveryMode triggerOrder;
	private final ListenerData vibrationListenerData;
	private final Callback vibrationCallback;

	private final boolean showParticle;
	private final int range;

	private DynamicGameEventListener<VibrationSystem.Listener> gameEventHandler;

	public GameEventListenerPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> biEntityAction, Consumer<Triple<Level, BlockPos, Direction>> blockAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, Predicate<BlockInWorld> blockCondition, int cooldownDuration, HudRender hudRender, int range, @NotNull Optional<Holder<GameEvent>> acceptedGameEvent, @NotNull Optional<List<Holder<GameEvent>>> acceptedGameEvents, Optional<TagKey<GameEvent>> acceptedGameEventTag, boolean showParticle, GameEventListener.DeliveryMode triggerOrder) {
		super(power, entity, cooldownDuration, hudRender);

		this.blockAction = blockAction;
		this.biEntityAction = biEntityAction;
		this.blockCondition = blockCondition;
		this.biEntityCondition = biEntityCondition;
		this.range = range;

		this.acceptedGameEvents = new LinkedList<>();
		acceptedGameEvent.ifPresent(this.acceptedGameEvents::add);
		acceptedGameEvents.ifPresent(this.acceptedGameEvents::addAll);

		this.gameEventHandler = null;
		this.triggerOrder = triggerOrder;

		this.vibrationCallback = new Callback();
		this.vibrationListenerData = new ListenerData();
		this.vibrationListenerData.setReloadVibrationParticle(false);

		this.acceptedGameEventTag = acceptedGameEventTag;
		this.showParticle = showParticle;
		this.setTicking();

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("game_event_listener"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("cooldown", SerializableDataTypes.NON_NEGATIVE_INT, 1)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("range", SerializableDataTypes.POSITIVE_INT, 16)
				.add("event", SerializableDataTypes.GAME_EVENT_ENTRY.optional(), Optional.empty())
				.add("events", SerializableDataTypes.GAME_EVENT_ENTRIES.optional(), Optional.empty())
				.add("event_tag", SerializableDataTypes.GAME_EVENT_TAG.optional(), Optional.empty())
				.add("show_particle", SerializableDataTypes.BOOLEAN, true)
				.add("trigger_order", SerializableDataType.enumValue(GameEventListener.DeliveryMode.class), GameEventListener.DeliveryMode.UNSPECIFIED),
			data -> (power, entity) -> new GameEventListenerPowerType(power, entity,
				data.getOrElse("bientity_action", actorAndTarget -> {
				}),
				data.getOrElse("block_action", block -> {
				}),
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				data.getOrElse("block_condition", block -> true),
				data.get("cooldown"),
				data.get("hud_render"),
				data.get("range"),
				data.get("event"),
				data.get("events"),
				data.get("event_tag"),
				data.get("show_particle"),
				data.get("trigger_order")
			)
		).allowCondition();
	}

	@Override
	public void onAdded() {
		if (entity.level() instanceof ServerLevel serverWorld) {
			getGameEventHandler().move(serverWorld);
		}
	}

	@Override
	public void onRemoved() {
		if (entity.level() instanceof ServerLevel serverWorld) {
			getGameEventHandler().remove(serverWorld);
		}
	}

	@Override
	public void tick() {
		if (canUse()) {
			Ticker.tick(entity.level(), getVibrationData(), getVibrationUser());
		}
	}

	@Override
	public boolean canUse() {
		return gameEventHandler != null && super.canUse();
	}

	@Override
	public @NotNull ListenerData getVibrationData() {
		return vibrationListenerData;
	}

	@Override
	public @NotNull Callback getVibrationUser() {
		return vibrationCallback;
	}

	public DynamicGameEventListener<net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Listener> getGameEventHandler() {

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
		public @NotNull DeliveryMode getDeliveryMode() {
			return triggerOrder;
		}

	}

	public class Callback implements VibrationSystem.User {

		@Override
		public int getListenerRadius() {
			return range;
		}

		@Override
		public @NotNull PositionSource getPositionSource() {
			return new EntityPositionSource(entity, entity.getEyeHeight(entity.getPose()));
		}

		@Override
		public boolean canReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull Holder<GameEvent> event, GameEvent.@NotNull Context emitter) {
			return blockCondition.test(new BlockInWorld(world, pos, true))
				&& biEntityCondition.test(new Tuple<>(emitter.sourceEntity(), entity));
		}

		@Override
		public void onReceiveVibration(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {

			GameEventListenerPowerType.this.use();

			blockAction.accept(Triple.of(world, pos, Direction.UP));
			biEntityAction.accept(new Tuple<>(sourceEntity, GameEventListenerPowerType.this.entity));

		}

		@Override
		public @NotNull TagKey<GameEvent> getListenableEvents() {
			return acceptedGameEventTag.orElse(VibrationSystem.User.super.getListenableEvents());
		}

		public boolean shouldAccept(Holder<GameEvent> gameEvent) {
			return GameEventListenerPowerType.this.canUse()
				&& this.isAccepted(gameEvent);
		}

		public boolean isAccepted(@NotNull Holder<GameEvent> gameEvent) {
			return acceptedGameEventTag.map(gameEvent::is).orElse(true)
				&& (acceptedGameEvents.isEmpty() || acceptedGameEvents.contains(gameEvent));
		}

	}

	public class ListenerData extends VibrationSystem.Data {

		public boolean shouldShowParticle() {
			return GameEventListenerPowerType.this.shouldShowParticle();
		}

	}

}
