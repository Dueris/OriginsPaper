package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleStatusEffectPower extends StatusEffectPower {
	public SimpleStatusEffectPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}
}
