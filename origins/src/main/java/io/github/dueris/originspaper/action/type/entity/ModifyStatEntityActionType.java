package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModifyStatEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ModifyStatEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("stat", SerializableDataTypes.STAT)
			.add("modifier", Modifier.DATA_TYPE),
		data -> new ModifyStatEntityActionType(
			data.get("stat"),
			data.get("modifier")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("stat", actionType.stat)
			.set("modifier", actionType.modifier)
	);

	private final Stat<?> stat;
	private final Modifier modifier;

	public ModifyStatEntityActionType(Stat<?> stat, Modifier modifier) {
		this.stat = stat;
		this.modifier = modifier;
	}

	@Override
	protected void execute(Entity entity) {

		if (entity instanceof ServerPlayer serverPlayer) {

			ServerStatsCounter statHandler = serverPlayer.getStats();
			int originalValue = statHandler.getValue(stat);

			serverPlayer.resetStat(stat);
			serverPlayer.awardStat(stat, (int) modifier.apply(entity, originalValue));

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.MODIFY_STAT;
	}

}
