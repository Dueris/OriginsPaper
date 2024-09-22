package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustOverTimePower extends PowerType {
	private final int exhaustInterval;
	private final float exhaustion;

	public ExhaustOverTimePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								int exhaustInterval, float exhaustion) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.exhaustInterval = exhaustInterval;
		this.exhaustion = exhaustion;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("exhaust"), PowerType.getFactory().getSerializableData()
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.add("exhaustion", SerializableDataTypes.FLOAT));
	}

	@Override
	public void tick(@NotNull Player player) {
		if (player.tickCount % exhaustInterval == 0 && isActive(player)) {
			player.causeFoodExhaustion(exhaustion);
		}
	}
}
