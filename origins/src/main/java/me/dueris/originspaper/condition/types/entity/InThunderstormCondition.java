package me.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InThunderstormCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Entity entity) {

		BlockPos downBlockPos = entity.blockPosition();
		BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getBoundingBox().maxY, downBlockPos.getX());

		return Util.inThunderstorm(entity.level(), downBlockPos, upBlockPos);

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_thunderstorm"),
			InstanceDefiner.instanceDefiner(),
			InThunderstormCondition::condition
		);
	}
}
