package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public class InTagDamageConditionType extends DamageConditionType {

    public static final TypedDataObjectFactory<InTagDamageConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("tag", SerializableDataType.tagKey(Registries.DAMAGE_TYPE)),
        data -> new InTagDamageConditionType(
            data.get("tag")
        ),
        (t, serializableData) -> serializableData.instance()
            .set("tag", t.tag)
    );

    private final TagKey<DamageType> tag;

    public InTagDamageConditionType(TagKey<DamageType> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(DamageSource source, float amount) {
        return source.is(tag);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return DamageConditionTypes.IN_TAG;
    }

}
