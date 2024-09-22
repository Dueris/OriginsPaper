package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.LangFile;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.ScoreHolder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcePower extends PowerType implements ResourceInterface {
	protected final int min;
	protected final int max;
	protected final int startValue;
	protected final ActionTypeFactory<Entity> minAction;
	protected final ActionTypeFactory<Entity> maxAction;
	protected final HudRender hudRender;

	private final Map<ScoreHolder, BarRender> players2Render = new ConcurrentHashMap<>();

	public ResourcePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						 int min, int max, @NotNull Optional<Integer> startValue, ActionTypeFactory<Entity> minAction, ActionTypeFactory<Entity> maxAction, HudRender hudRender) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.min = min;
		this.max = max;
		this.startValue = startValue.orElse(min);
		this.minAction = minAction;
		this.maxAction = maxAction;
		this.hudRender = hudRender;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("resource"), PowerType.getFactory().getSerializableData()
			.add("min", SerializableDataTypes.INT)
			.add("max", SerializableDataTypes.INT)
			.add("start_value", SerializableDataTypes.optional(SerializableDataTypes.INT), Optional.empty())
			.add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("max_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER));
	}

	@Override
	public void tick(Player player) {
		cleanup(player);
		BarRender render = players2Render.get(player);
		render.setRendering(shouldRender(player) && isActive(player));
	}

	public BarRender getRender(ScoreHolder holder) {
		if (holder instanceof Player player) {
			cleanup(player);
		}
		return players2Render.get(holder);
	}

	private void cleanup(Player player) {
		if (!players2Render.containsKey(player)) {
			players2Render.put(player, new BarRender(player, this));
		}
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	public boolean shouldRender(Entity entity) {
		return hudRender.shouldRender(entity);
	}

	public int getValue(ScoreHolder holder) {
		return getRender(holder).getValue();
	}

	public void setValue(ScoreHolder holder, int newValue) {
		getRender(holder).setValue(newValue);
	}

	public static class BarRender {
		private final ScoreHolder holder;
		private final int maxValue;
		private final int minValue;
		private @Nullable
		final ActionTypeFactory<Entity> minAction;
		private @Nullable
		final ActionTypeFactory<Entity> maxAction;
		private boolean isRendering = false;
		private @Nullable KeyedBossBar backboard;
		private int currentTicks;

		BarRender(ScoreHolder holder, ResourcePower power) {
			this(holder, power, power.max, power.startValue, power.min, power.minAction, power.maxAction);
		}

		BarRender(ScoreHolder holder, @NotNull ResourceInterface resourceInterface, int maxValue, int startValue, int minValue, @Nullable ActionTypeFactory<Entity> minAction, @Nullable ActionTypeFactory<Entity> maxAction) {
			this.holder = holder;
			this.maxValue = maxValue;
			this.minValue = minValue;
			this.minAction = minAction;
			this.maxAction = maxAction;
			this.currentTicks = startValue;

			if (resourceInterface.getHudRender() != null && resourceInterface.getHudRender().shouldRender()) {
				this.backboard = Bukkit.createBossBar(
					Objects.requireNonNull(NamespacedKey.fromString(resourceInterface.getTag())), LangFile.translatable(PlainTextComponentSerializer.plainText().serialize(resourceInterface.name())).getString(), resourceInterface.getHudRender().render().renderColor(), BarStyle.SEGMENTED_6
				);
				this.backboard.setProgress(0);
			}
		}

		public boolean isRendering() {
			return isRendering;
		}

		public void setRendering(boolean render) {
			isRendering = render;

			if (backboard != null) {
				if (holder instanceof ServerPlayer player) {
					backboard.addPlayer(player.getBukkitEntity());
				}
				backboard.setVisible(isRendering);
			}
		}

		public int getValue() {
			return currentTicks;
		}

		/**
		 * @param value IS IN TICKS, DO NOT USE RENDERED PROGRESS FOR INTERACTING WITH API
		 */
		public void setValue(int value) {
			this.currentTicks = value;
			double d = ((double) currentTicks / maxValue);
			if (getBackboard() != null) {
				getBackboard().setProgress((d > 1) ? 1 : ((d < 0) ? 0 : d));
			}

			if (currentTicks >= maxValue) {
				currentTicks = maxValue;
				if (holder instanceof Entity entity && maxAction != null) {
					maxAction.accept(entity);
				}
			}

			if (currentTicks <= minValue) {
				currentTicks = minValue;
				if (holder instanceof Entity entity && minAction != null) {
					minAction.accept(entity);
				}
			}
		}

		@Nullable
		KeyedBossBar getBackboard() {
			return backboard;
		}

		public void destroy() {
			this.setRendering(false);
			if (getBackboard() != null) {
				for (org.bukkit.entity.Player player : getBackboard().getPlayers()) {
					getBackboard().removePlayer(player);
				}

				Bukkit.removeBossBar(getBackboard().getKey());
			}
		}
	}

}
