package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CooldownPower extends PowerType implements ResourceInterface {
	final int cooldown;
	private final HudRender hudRender;
	private final Map<ScoreHolder, ResourcePower.BarRender> holder2Render = new ConcurrentHashMap<>();
	private final List<ScoreHolder> tickingCooldowns = new CopyOnWriteArrayList<>();

	public CooldownPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						 HudRender hudRender, int cooldown) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.hudRender = hudRender;
		this.cooldown = cooldown;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("cooldown"))
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 0);
	}

	public ResourcePower.BarRender getRender(ScoreHolder holder) {
		if (holder instanceof net.minecraft.world.entity.player.Player player) {
			cleanup(player);
		}
		return holder2Render.get(holder);
	}

	private void cleanup(ScoreHolder player) {
		if (!holder2Render.containsKey(player)) {
			holder2Render.put(player, new ResourcePower.BarRender(player, this, this.cooldown, cooldown, 0, null, null));
		}
	}

	@Override
	public void tick() {
		for (ScoreHolder holder : tickingCooldowns) {
			if (holder instanceof ServerPlayer player && player.hasDisconnected()) continue;
			cleanup(holder);
			ResourcePower.BarRender render = holder2Render.get(holder);
			render.setRendering(shouldRender(holder) && (holder instanceof Entity entity && isActive(entity)));
			render.setValue(render.getValue() - 1);

			if (render.getValue() <= 0) {
				render.destroy();
				tickingCooldowns.remove(holder);
				holder2Render.remove(holder);
			}
		}
	}

	public boolean shouldRender(ScoreHolder entity) {
		return entity instanceof Entity && hudRender.shouldRender((Entity) entity);
	}

	public boolean canUse(ScoreHolder holder) {
		return !tickingCooldowns.contains(holder);
	}

	public void use(ScoreHolder holder) {
		tickingCooldowns.add(holder);
	}

	public int getRemainingTicks(ScoreHolder holder) {
		return getRender(holder).getValue();
	}

	public void setCooldown(ScoreHolder holder, int newValue) {
		getRender(holder).setValue(newValue);
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	public int getCooldown() {
		return cooldown;
	}
}
