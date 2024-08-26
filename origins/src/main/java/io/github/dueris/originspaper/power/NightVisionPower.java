package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class NightVisionPower extends PowerType {
	private final float strength;

	public NightVisionPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							float strength) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.strength = strength;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("night_vision"))
			.add("strength", SerializableDataTypes.FLOAT, 1.0F);
	}

	@Override
	public void tick(Player player) {
		if (isActive(player)) {
			if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
				player.addEffect(
					new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false, false)
				);
			}
		} else {
			if (player.hasEffect(MobEffects.NIGHT_VISION)) {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}
}
