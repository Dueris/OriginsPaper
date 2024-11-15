package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AdvancementEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<AdvancementEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("advancement", SerializableDataTypes.IDENTIFIER),
		data -> new AdvancementEntityConditionType(
			data.get("advancement")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("advancement", conditionType.advancement)
	);

	private final ResourceLocation advancement;

	public AdvancementEntityConditionType(ResourceLocation advancement) {
		this.advancement = advancement;
	}

	@Override
	public boolean test(Entity entity) {

		if (!(entity instanceof Player player)) {
			return false;
		}

		MinecraftServer server = player.getServer();
		if (server != null) {

			AdvancementHolder advancementEntry = server.getAdvancements().get(advancement);
			if (advancementEntry == null) {
				//  TODO: Throw an exception and pass it to the factory instance to be caught instead -eggohito
				OriginsPaper.LOGGER.warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", advancement);
				return false;
			} else {
				return ((ServerPlayer) player).getAdvancements()
					.getOrStartProgress(advancementEntry)
					.isDone();
			}

		} else {
			return false;
		}

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.ADVANCEMENT;
	}

}
