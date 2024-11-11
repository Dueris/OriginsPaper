package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplodeBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<ExplodeBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("destructible", BlockCondition.DATA_TYPE, null)
            .add("indestructible", BlockCondition.DATA_TYPE, null)
            .add("destruction_type", SerializableDataTypes.DESTRUCTION_TYPE, Explosion.BlockInteraction.DESTROY)
            .add("create_fire", SerializableDataTypes.BOOLEAN, false)
            .add("power", SerializableDataTypes.NON_NEGATIVE_FLOAT)
            .add("indestructible_resistance", SerializableDataTypes.NON_NEGATIVE_FLOAT, 10.0F),
        data -> new ExplodeBlockActionType(
            data.get("destructible"),
            data.get("indestructible"),
            data.get("destruction_type"),
            data.get("create_fire"),
            data.get("power"),
            data.get("indestructible_resistance")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("destructible", actionType.destructibleCondition)
            .set("indestructible", actionType.indestructibleCondition)
            .set("destruction_type", actionType.destructionType)
            .set("create_fire", actionType.createFire)
            .set("power", actionType.power)
            .set("indestructible_resistance", actionType.indestructibleResistance)
    );

    private final BlockCondition destructibleCondition;
    private final BlockCondition indestructibleCondition;

    private final Explosion.BlockInteraction destructionType;
    private final boolean createFire;

    private final float power;
    private final float indestructibleResistance;

    public ExplodeBlockActionType(BlockCondition destructibleCondition, BlockCondition indestructibleCondition, Explosion.BlockInteraction destructionType, boolean createFire, float power, float indestructibleResistance) {
        this.destructibleCondition = destructibleCondition;
        this.indestructibleCondition = indestructibleCondition;
        this.destructionType = destructionType;
        this.createFire = createFire;
        this.power = power;
        this.indestructibleResistance = indestructibleResistance;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

        if (world.isClientSide()) {
            return;
        }

        Predicate<BlockConditionContext> behaviorCondition = indestructibleCondition;
        if (destructibleCondition != null) {
            behaviorCondition = Util.combineOr(destructibleCondition.negate(), behaviorCondition);
        }

        Util.createExplosion(
            world,
            pos.getCenter(),
            power,
            createFire,
            destructionType,
            Util.createExplosionBehavior(behaviorCondition, indestructibleResistance)
        );

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.EXPLODE;
    }

}
