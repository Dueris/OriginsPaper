package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class ScoreboardCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Entity entity) {

		ScoreHolder scoreHolder = ScoreHolder.forNameOnly(entity.getScoreboardName());
		Scoreboard scoreboard = entity.level().getScoreboard();

		Objective scoreboardObjective = scoreboard.getObjective(data.get("objective"));
		if (scoreboardObjective == null) {
			return false;
		}

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		ScoreAccess scoreAccess = scoreboard.getOrCreatePlayerScore(scoreHolder, scoreboardObjective);
		return comparison.compare(scoreAccess.get(), compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(OriginsPaper.apoliIdentifier("scoreboard"),
			SerializableData.serializableData()
				.add("name", SerializableDataTypes.STRING, null)
				.add("objective", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			ScoreboardCondition::condition
		);
	}
}
