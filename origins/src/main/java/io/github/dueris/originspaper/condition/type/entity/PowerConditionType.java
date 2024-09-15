package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PowerConditionType {

	public static boolean condition(Entity entity, PowerReference power, @Nullable ResourceLocation source) {
		if (entity instanceof Player player) {
			return PlayerPowerRepository.getOrCreateRepo((ServerPlayer) player).getAppliedPowers(source).contains(power.getType());
		}
		return false;
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE)
				.add("source", SerializableDataTypes.IDENTIFIER, null),
			(data, entity) -> condition(entity,
				data.get("power"),
				data.get("source")
			)
		);
	}

}
