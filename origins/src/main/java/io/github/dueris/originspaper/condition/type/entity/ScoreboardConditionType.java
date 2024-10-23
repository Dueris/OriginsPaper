package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ScoreboardConditionType {

	public static boolean condition(Entity entity, @Nullable String holderName, String objectiveName, Comparison comparison, int compareTo) {

		ScoreHolder scoreHolder = ScoreHolder.forNameOnly(holderName != null ? holderName : entity.getScoreboardName());
		Scoreboard scoreboard = entity.level().getScoreboard();

		return Optional.ofNullable(scoreboard.getObjective(objectiveName))
			.map(objective -> scoreboard.getOrCreatePlayerScore(scoreHolder, objective))
			.map(scoreAccess -> comparison.compare(scoreAccess.get(), compareTo))
			.orElse(false);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("scoreboard"),
			new SerializableData()
				.add("name", SerializableDataTypes.STRING, null)
				.add("objective", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> condition(entity,
				data.get("name"),
				data.get("objective"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
