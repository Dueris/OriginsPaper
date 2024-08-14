package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModifyStatAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {
		if (!(entity instanceof ServerPlayer serverPlayerEntity)) return;

		Stat<?> stat = data.get("stat");
		ServerStatsCounter serverStatHandler = serverPlayerEntity.getStats();

		int newValue;
		int originalValue = serverStatHandler.getValue(stat);

		serverPlayerEntity.resetStat(stat);

		Modifier modifier = data.get("modifier");
		newValue = (int) modifier.apply(entity, originalValue);

		serverPlayerEntity.awardStat(stat, newValue);
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_stat"),
			InstanceDefiner.instanceDefiner()
				.add("stat", SerializableDataTypes.STAT)
				.add("modifier", Modifier.DATA_TYPE),
			ModifyStatAction::action
		);
	}
}
