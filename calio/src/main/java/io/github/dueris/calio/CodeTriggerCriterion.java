package io.github.dueris.calio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CodeTriggerCriterion extends SimpleCriterionTrigger<CodeTriggerCriterion.Conditions> {

	public static final CodeTriggerCriterion INSTANCE = new CodeTriggerCriterion();
	public static final ResourceLocation ID = ResourceLocation.parse("apacelib:code_trigger");

	@Override
	public @NotNull Codec<Conditions> codec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayer player, String triggerId) {
		super.trigger(player, conditions -> conditions.matches(triggerId));
	}

	public record Conditions(Optional<ContextAwarePredicate> playerPredicate,
							 Optional<String> triggerId) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
			Codec.STRING.optionalFieldOf("trigger_id").forGetter(Conditions::triggerId)
		).apply(instance, Conditions::new));

		@Override
		public @NotNull Optional<ContextAwarePredicate> player() {
			return playerPredicate;
		}

		public boolean matches(@NotNull String triggerId) {
			return this.triggerId
				.map(triggerId::equals)
				.orElse(true);
		}

	}

}
