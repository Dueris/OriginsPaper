package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ScoreboardEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<ScoreboardEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("name", SerializableDataTypes.STRING.optional(), Optional.empty())
			.add("objective", SerializableDataTypes.STRING)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new ScoreboardEntityConditionType(
			data.get("name"),
			data.get("objective"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("name", conditionType.name)
			.set("objective", conditionType.objective)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Optional<String> name;
	private final String objective;

	private final Comparison comparison;
	private final int compareTo;

	public ScoreboardEntityConditionType(Optional<String> name, String objective, Comparison comparison, int compareTo) {
		this.name = name;
		this.objective = objective;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {

		ScoreHolder scoreHolder = ScoreHolder.forNameOnly(name.orElse(entity.getScoreboardName()));
		Scoreboard scoreboard = entity.level().getScoreboard();

		return Optional.ofNullable(scoreboard.getObjective(objective))
			.flatMap(objective -> Optional.ofNullable(scoreboard.getPlayerScoreInfo(scoreHolder, objective)))
			.map(ReadOnlyScoreInfo::value)
			.map(score -> comparison.compare(score, compareTo))
			.orElse(false);

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.SCOREBOARD;
	}

}
