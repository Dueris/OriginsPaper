package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
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

public class PredicateCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return false;
		}

		ResourceKey<LootItemCondition> predicateKey = data.get("predicate");
		LootItemCondition predicate = serverWorld.getServer().reloadableRegistries()
			.get()
			.registryOrThrow(Registries.PREDICATE)
			.getOrThrow(predicateKey);

		LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
			.withParameter(LootContextParams.ORIGIN, entity.position())
			.withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
			.create(LootContextParamSets.COMMAND);
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet)
			.create(Optional.empty());

		return predicate.test(lootContext);

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("predicate"),
			InstanceDefiner.instanceDefiner()
				.add("predicate", SerializableDataTypes.PREDICATE),
			PredicateCondition::condition
		);
	}
}
