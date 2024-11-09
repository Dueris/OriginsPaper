package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OriginConditionType {

	public static boolean condition(Entity entity, ResourceLocation originId, @Nullable ResourceLocation layerId) {

		if (!(entity instanceof ServerPlayer)) {
			return false;
		}

		OriginComponent originComponent = OriginComponent.ORIGIN.getNullable((Player) entity);
		if (originComponent == null) {
			return false;
		}

		if (layerId == null) {
			return originComponent.getOrigins().values()
				.stream()
				.map(Origin::getId)
				.anyMatch(originId::equals);
		}

		OriginLayer layer = OriginLayerManager.getNullable(layerId);
		if (layer == null) {
			return false;
		}

		Origin origin = originComponent.getOrigin(layer);
		return origin != null
			&& origin.getId().equals(originId);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.originIdentifier("origin"),
			new SerializableData()
				.add("origin", SerializableDataTypes.IDENTIFIER)
				.add("layer", SerializableDataTypes.IDENTIFIER, null),
			(data, entity) -> condition(entity,
				data.get("origin"),
				data.get("layer")
			)
		);
	}

}

