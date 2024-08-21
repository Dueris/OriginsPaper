package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FreezePower extends PowerType {
	public FreezePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("freeze"));
	}

	@Override
	public void tick(Player player) {
		if (isActive(player)) {
			if (player.getTicksFrozen() >= 138) {
				player.setTicksFrozen(150);
			} else {
				player.setTicksFrozen(Math.min(player.getTicksRequiredToFreeze(), player.getTicksFrozen() + 3));
			}
		}
	}
}
