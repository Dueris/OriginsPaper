package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
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

public class PredicateEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<PredicateEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("predicate", SerializableDataTypes.PREDICATE),
        data -> new PredicateEntityConditionType(
            data.get("predicate")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("predicate", conditionType.predicate)
    );

    private final ResourceKey<LootItemCondition> predicate;

    public PredicateEntityConditionType(ResourceKey<LootItemCondition> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Entity entity) {

        if (!(entity.level() instanceof ServerLevel serverWorld)) {
            return false;
        }

        LootItemCondition lootCondition = serverWorld.getServer().reloadableRegistries()
            .get()
            .registryOrThrow(Registries.PREDICATE)
            .getOrThrow(predicate);
        LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
            .withParameter(LootContextParams.ORIGIN, entity.position())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
            .create(LootContextParamSets.COMMAND);

        return lootCondition.test(new LootContext.Builder(lootContextParameterSet).create(Optional.empty()));

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.PREDICATE;
    }

}
