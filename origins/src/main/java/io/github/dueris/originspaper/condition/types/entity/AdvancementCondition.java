package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AdvancementCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return false;
		}

		MinecraftServer server = playerEntity.getServer();
		ResourceLocation advancementId = data.get("advancement");

		if (server != null) {

			AdvancementHolder advancementEntry = server.getAdvancements().get(advancementId);
			if (advancementEntry == null) {
				OriginsPaper.getPlugin().getLog4JLogger().warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", advancementId);
				return false;
			}

			return ((ServerPlayer) playerEntity)
				.getAdvancements()
				.getOrStartProgress(advancementEntry)
				.isDone();

		}

		return false;

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("advancement"),
			SerializableData.serializableData()
				.add("advancement", SerializableDataTypes.IDENTIFIER),
			AdvancementCondition::condition
		);
	}
}
