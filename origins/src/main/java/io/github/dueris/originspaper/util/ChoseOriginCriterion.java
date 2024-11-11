package io.github.dueris.originspaper.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChoseOriginCriterion extends SimpleCriterionTrigger<ChoseOriginCriterion.Conditions> {

	public static final ResourceLocation ID = OriginsPaper.identifier("chose_origin");
	public static ChoseOriginCriterion INSTANCE = new ChoseOriginCriterion();

	@Override
	public @NotNull Codec<Conditions> codec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayer player, Origin origin) {
		this.trigger(player, conditions -> conditions.matches(origin));
	}

	public record Conditions(Optional<ContextAwarePredicate> player,
							 ResourceLocation originId) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
			ResourceLocation.CODEC.fieldOf("origin").forGetter(Conditions::originId)
		).apply(instance, Conditions::new));

		@Override
		public @NotNull Optional<ContextAwarePredicate> player() {
			return player;
		}

		public boolean matches(@NotNull Origin origin) {
			return origin.getId().equals(originId);
		}

	}

}
