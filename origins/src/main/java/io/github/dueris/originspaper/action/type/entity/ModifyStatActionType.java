package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.Entity;

public class ModifyStatActionType {

	public static void action(Entity entity, Stat<?> stat, Modifier modifier) {

		if (!(entity instanceof ServerPlayer serverPlayerEntity)) {
			return;
		}

		ServerStatsCounter serverStatHandler = serverPlayerEntity.getStats();
		int originalValue = serverStatHandler.getValue(stat);

		serverPlayerEntity.resetStat(stat);
		serverPlayerEntity.awardStat(stat, (int) modifier.apply(entity, originalValue));

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_stat"),
			new SerializableData()
				.add("stat", SerializableDataTypes.STAT)
				.add("modifier", Modifier.DATA_TYPE),
			(data, entity) -> action(entity,
				data.get("stat"),
				data.get("modifier")
			)
		);
	}
}
