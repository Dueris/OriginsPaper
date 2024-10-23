package io.github.dueris.originspaper.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PowerLootCondition(LootContext.EntityTarget target, PowerReference power,
								 Optional<ResourceLocation> sourceId) implements LootItemCondition {

	public static final MapCodec<PowerLootCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.optionalFieldOf("entity", LootContext.EntityTarget.THIS).forGetter(PowerLootCondition::target),
		ApoliDataTypes.POWER_REFERENCE.codec().fieldOf("power").forGetter(PowerLootCondition::power),
		ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::sourceId)
	).apply(instance, PowerLootCondition::new));

	@Override
	public LootItemConditionType getType() {
		return ApoliLootConditionTypes.POWER;
	}

	@Override
	public boolean test(@NotNull LootContext lootContext) {
		Entity entity = lootContext.getParamOrNull(target().getParam());
		return PowerHolderComponent.KEY.maybeGet(entity)
			.map(this::hasPower)
			.orElse(false);
	}

	private boolean hasPower(PowerHolderComponent component) {
		return sourceId()
			.map(id -> component.hasPower(power(), id))
			.orElseGet(() -> component.hasPower(power()));
	}

}
