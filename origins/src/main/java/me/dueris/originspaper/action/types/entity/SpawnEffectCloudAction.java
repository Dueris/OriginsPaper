package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

public class SpawnEffectCloudAction {

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

		AreaEffectCloud aec = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
		if (entity instanceof LivingEntity livingEntity) {
			aec.setOwner(livingEntity);
		}

		aec.setRadius(data.getFloat("radius"));
		aec.setRadiusOnUse(data.getFloat("radius_on_use"));
		aec.setDuration(data.getInt("duration"));
		aec.setDurationOnUse(data.getInt("duration_on_use"));
		aec.setWaitTime(data.getInt("wait_time"));
		aec.setPotionContents(data.get("effect_component"));

		entity.level().addFreshEntity(aec);

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("spawn_effect_cloud"),
			InstanceDefiner.instanceDefiner()
				.add("radius", SerializableDataTypes.FLOAT, 3.0F)
				.add("radius_on_use", SerializableDataTypes.FLOAT, -0.5F)
				.add("duration", SerializableDataTypes.INT, 600)
				.add("duration_on_use", SerializableDataTypes.INT, 0)
				.add("wait_time", SerializableDataTypes.INT, 10)
				.add("effect_component", SerializableDataTypes.POTION_CONTENTS_COMPONENT, PotionContents.EMPTY),
			SpawnEffectCloudAction::action
		);
	}
}
