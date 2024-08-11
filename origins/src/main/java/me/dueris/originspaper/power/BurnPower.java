package me.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
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

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("burn"))
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
