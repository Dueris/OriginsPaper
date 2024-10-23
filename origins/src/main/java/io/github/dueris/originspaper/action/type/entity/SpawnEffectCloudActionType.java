package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;

public class SpawnEffectCloudActionType {

	public static void action(Entity entity, PotionContents potionContents, float radius, float radiusOnUse, int duration, int durationOnUse, int waitTime) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		AreaEffectCloud aec = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
		if (entity instanceof LivingEntity living) {
			aec.setOwner(living);
		}

		aec.setPotionContents(potionContents);
		aec.setRadius(radius);
		aec.setRadiusOnUse(radiusOnUse);
		aec.setDuration(duration);
		aec.setDurationOnUse(durationOnUse);
		aec.setWaitTime(waitTime);

		serverWorld.addFreshEntity(aec);

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("spawn_effect_cloud"),
			new SerializableData()
				.add("effect_component", SerializableDataTypes.POTION_CONTENTS_COMPONENT, PotionContents.EMPTY)
				.add("radius", SerializableDataTypes.FLOAT, 3.0F)
				.add("radius_on_use", SerializableDataTypes.FLOAT, -0.5F)
				.add("duration", SerializableDataTypes.INT, 600)
				.add("duration_on_use", SerializableDataTypes.INT, 0)
				.add("wait_time", SerializableDataTypes.INT, 10),
			(data, entity) -> action(entity,
				data.get("effect_component"),
				data.get("radius"),
				data.get("radius_on_use"),
				data.get("duration"),
				data.get("duration_on_use"),
				data.get("wait_time")
			)
		);
	}

}
