package io.github.dueris.originspaper.condition;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.context.BiomeConditionContext;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public final class BiomeCondition extends AbstractCondition<BiomeConditionContext, BiomeConditionType> {

	public static final SerializableDataType<BiomeCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", BiomeConditionTypes.DATA_TYPE, BiomeCondition::new));

	public BiomeCondition(BiomeConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public BiomeCondition(BiomeConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return test(new BiomeConditionContext(pos, biomeEntry));
	}

}
