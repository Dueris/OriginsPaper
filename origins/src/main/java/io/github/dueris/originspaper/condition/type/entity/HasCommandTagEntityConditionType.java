package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.entity.Entity;

public class HasCommandTagEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<HasCommandTagEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("command_tag", SerializableDataTypes.STRING.optional(), Optional.empty())
            .add("command_tags", SerializableDataTypes.STRINGS.optional(), Optional.empty()),
        data -> new HasCommandTagEntityConditionType(
            data.get("command_tag"),
            data.get("command_tags")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("command_tag", conditionType.commandTag)
            .set("command_tags", conditionType.commandTags)
    );

    private final Optional<String> commandTag;
    private final Optional<List<String>> commandTags;

    private final Set<String> specifiedCommandTags;

    public HasCommandTagEntityConditionType(Optional<String> commandTag, Optional<List<String>> commandTags) {

        this.commandTag = commandTag;
        this.commandTags = commandTags;

        this.specifiedCommandTags = new ObjectOpenHashSet<>();

        this.commandTag.ifPresent(this.specifiedCommandTags::add);
        this.commandTags.ifPresent(this.specifiedCommandTags::addAll);

    }

    @Override
    public boolean test(Entity entity) {
        Set<String> commandTags = entity.getTags();
        return specifiedCommandTags.isEmpty()
            ? !commandTags.isEmpty()
            : !Collections.disjoint(commandTags, specifiedCommandTags);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.HAS_COMMAND_TAG;
    }

}
