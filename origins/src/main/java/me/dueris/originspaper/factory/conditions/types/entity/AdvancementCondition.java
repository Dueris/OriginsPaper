package me.dueris.originspaper.factory.conditions.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class AdvancementCondition {

	public static boolean condition(DeserializedFactoryJson data, Entity entity) {

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

	public static ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("advancement"),
			InstanceDefiner.instanceDefiner()
				.add("advancement", SerializableDataTypes.IDENTIFIER),
			AdvancementCondition::condition
		);
	}
}
