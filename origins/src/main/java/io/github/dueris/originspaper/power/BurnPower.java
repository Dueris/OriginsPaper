package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class BurnPower extends PowerType {
	private final int refreshInterval;
	private final int burnDuration;

	public BurnPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority, int interval, int burnDuration) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.refreshInterval = interval;
		this.burnDuration = burnDuration;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("burn"))
			.add("interval", SerializableDataTypes.POSITIVE_INT)
			.add("burn_duration", SerializableDataTypes.POSITIVE_INT);
	}

	@Override
	public void tick(@NotNull Player player) {
		if (isActive(player) && player.tickCount % refreshInterval == 0) {
			player.igniteForSeconds(burnDuration);
		}
	}
}
