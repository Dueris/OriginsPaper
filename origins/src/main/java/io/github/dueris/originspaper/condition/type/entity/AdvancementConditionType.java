package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AdvancementConditionType {

	public static boolean condition(Entity entity, ResourceLocation advancementId) {

		if (!(entity instanceof Player player)) {
			return false;
		}

		MinecraftServer server = player.getServer();
		if (server != null) {

			AdvancementHolder advancement = server.getAdvancements().get(advancementId);
			if (advancement == null) {
				OriginsPaper.LOGGER.warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", advancementId);
				return false;
			} else {
				return ((ServerPlayer) player).getAdvancements()
					.getOrStartProgress(advancement)
					.isDone();
			}

		} else {
			return false;
		}

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("advancement"),
			new SerializableData()
				.add("advancement", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> condition(entity,
				data.get("advancement")
			)
		);
	}

}
