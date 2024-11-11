package io.github.dueris.originspaper.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GainedPowerCriterion extends SimpleCriterionTrigger<GainedPowerCriterion.Conditions> {

	public static final GainedPowerCriterion INSTANCE = new GainedPowerCriterion();
	public static final ResourceLocation ID = OriginsPaper.apoliIdentifier("gained_power");

	@Override
	public @NotNull Codec<Conditions> codec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayer player, Power power) {
		this.trigger(player, conditions -> conditions.matches(power));
	}

	public record Conditions(Optional<ContextAwarePredicate> player,
							 ResourceLocation powerId) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
			ResourceLocation.CODEC.fieldOf("power").forGetter(Conditions::powerId)
		).apply(instance, Conditions::new));

		@Override
		public @NotNull Optional<ContextAwarePredicate> player() {
			return player;
		}

		public boolean matches(@NotNull Power power) {
			return power.getId().equals(powerId);
		}

	}

}
