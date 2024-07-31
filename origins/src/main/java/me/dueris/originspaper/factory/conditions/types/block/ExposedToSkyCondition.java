package me.dueris.originspaper.factory.conditions.types.block;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class ExposedToSkyCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("exposed_to_sky"),
			InstanceDefiner.instanceDefiner(),
			(data, block) -> {
				return block.getLevel().canSeeSky(block.getPos());
			}
		);
	}
}
