package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class RevokeAdvancementActionType {

	public static void action(@NotNull Entity entity, @Nullable ResourceLocation advancementId, Collection<String> criteria) {

		MinecraftServer server = entity.getServer();
		if (server == null || !(entity instanceof ServerPlayer serverPlayerEntity)) {
			return;
		}

		AdvancementHolder holder = ((CraftAdvancement) Bukkit.getAdvancement(CraftNamespacedKey.fromMinecraft(advancementId))).getHandle();
		AdvancementProgress progress = serverPlayerEntity.getAdvancements().getOrStartProgress(holder);

		for (String remainingCriterion : criteria.isEmpty() ? progress.getCompletedCriteria() : criteria) {
			progress.revokeProgress(remainingCriterion);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("revoke_advancement"),
			new SerializableData()
				.add("advancement", SerializableDataTypes.IDENTIFIER, null)
				.add("criterion", SerializableDataTypes.STRING, null)
				.add("criteria", SerializableDataTypes.STRINGS, null),
			(data, entity) -> {

				Collection<String> criteria = new HashSet<>();

				data.ifPresent("criterion", criteria::add);
				data.ifPresent("criteria", criteria::addAll);

				action(entity, data.get("advancement"), criteria);

			}
		);
	}

}
