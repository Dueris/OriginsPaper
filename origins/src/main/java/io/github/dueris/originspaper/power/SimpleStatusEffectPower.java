package io.github.dueris.originspaper.power;

import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleStatusEffectPower extends StatusEffectPower {
	public SimpleStatusEffectPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}
}
