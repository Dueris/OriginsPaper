package io.github.dueris.originspaper.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PowerLootCondition(LootContext.EntityTarget target, PowerReference power,
								 Optional<ResourceLocation> sourceId) implements LootItemCondition {

	public static final MapCodec<PowerLootCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.optionalFieldOf("entity", LootContext.EntityTarget.THIS).forGetter(PowerLootCondition::target),
		ApoliDataTypes.POWER_REFERENCE.fieldOf("power").forGetter(PowerLootCondition::power),
		ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::sourceId)
	).apply(instance, PowerLootCondition::new));

	@Override
	public @NotNull LootItemConditionType getType() {
		return ApoliLootConditionTypes.POWER;
	}

	@Override
	public boolean test(@NotNull LootContext lootContext) {
		Entity entity = lootContext.getParamOrNull(target().getParam());
		return entity instanceof Player player && hasPower(player);
	}

	private boolean hasPower(Player player) {
		return sourceId()
			.map(id -> PowerHolderComponent.hasPower(player.getBukkitEntity(), id.toString()))
			.orElse(false);
	}

}
