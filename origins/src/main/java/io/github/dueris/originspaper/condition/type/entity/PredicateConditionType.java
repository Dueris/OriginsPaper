package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PredicateConditionType {

	public static boolean condition(@NotNull Entity entity, ResourceKey<LootItemCondition> predicateKey) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return false;
		}

		LootItemCondition predicate = serverWorld.getServer().reloadableRegistries()
			.get()
			.registryOrThrow(Registries.PREDICATE)
			.getOrThrow(predicateKey);
		LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
			.withParameter(LootContextParams.ORIGIN, entity.position())
			.withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
			.create(LootContextParamSets.COMMAND);

		return predicate.test(new LootContext.Builder(lootContextParameterSet).create(Optional.empty()));

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("predicate"),
			new SerializableData()
				.add("predicate", SerializableDataTypes.PREDICATE),
			(data, entity) -> condition(entity,
				data.get("predicate")
			)
		);
	}

}
