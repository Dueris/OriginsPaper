package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AddXpEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<AddXpEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("points", SerializableDataTypes.INT, 0)
            .add("levels", SerializableDataTypes.INT, 0),
        data -> new AddXpEntityActionType(
            data.get("points"),
            data.get("levels")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("points", actionType.points)
            .set("levels", actionType.levels)
    );

    private final int points;
    private final int levels;

    public AddXpEntityActionType(int points, int levels) {
        this.points = points;
        this.levels = levels;
    }

    @Override
    protected void execute(Entity entity) {

        if (!(entity instanceof Player player)) {
            return;
        }

        player.giveExperiencePoints(points);
        player.giveExperienceLevels(levels);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.ADD_XP;
    }

}
