package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ExposedToSunEntityConditionType extends EntityConditionType {

    private static final InRainEntityConditionType IN_RAIN = new InRainEntityConditionType();
    private static final BrightnessEntityConditionType BRIGHTNESS = new BrightnessEntityConditionType(Comparison.GREATER_THAN, 0.5F);
    private static final ExposedToSkyEntityConditionType EXPOSED_TO_SKY = new ExposedToSkyEntityConditionType();

    @Override
    public boolean test(Entity entity) {
        Level world = entity.level();
        return world.isDay()
            && !IN_RAIN.test(entity)
            && BRIGHTNESS.test(entity)
            && EXPOSED_TO_SKY.test(entity);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.EXPOSED_TO_SUN;
    }

}
