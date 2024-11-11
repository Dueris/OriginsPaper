package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AbilityEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<AbilityEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("ability", SerializableDataTypes.IDENTIFIER),
        data -> new AbilityEntityConditionType(
            data.getId("ability")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("ability", conditionType.ability)
    );

    private final ResourceLocation ability;

    public AbilityEntityConditionType(ResourceLocation ability) {
        this.ability = ability;
    }

    @Override
    public boolean test(Entity entity) {
		return (entity instanceof Player player && !entity.level().isClientSide) && switch (ability.toString()) {
			case "minecraft:flying" -> player.getAbilities().flying;
			case "minecraft:instabuild" -> player.getAbilities().instabuild;
			case "minecraft:invulnerable" -> player.getAbilities().invulnerable;
			case "minecraft:mayBuild" -> player.getAbilities().mayBuild;
			case "minecraft:mayfly" -> player.getAbilities().mayfly;
			default -> throw new IllegalStateException("Unexpected value: " + ability);
		};
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.ABILITY;
    }

}
