package io.github.dueris.originspaper.power;

import io.github.dueris.originspaper.client.resource.ResourceManager;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.component.PowerHolderComponentImpl;
import io.github.dueris.originspaper.power.type.EntitySetPowerType;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PowerIntegration {

	public static void callEntityAddCallback(Entity entity) {
		if (!PowerHolderComponent.KEY.isProvidedBy(entity) && entity instanceof LivingEntity livingEntity) {
			PowerHolderComponent.KEY.put(entity, new PowerHolderComponentImpl(livingEntity));
		}
		PowerHolderComponent.getPowerTypes(entity, PowerType.class, true).forEach(PowerType::onAdded);
		EntitySetPowerType.integrateLoadCallback(entity, (ServerLevel) entity.level());
		if (entity instanceof ServerPlayer) {
			ResourceManager.applyPlayer((ServerPlayer) entity);
		}
	}

	public static void callEntityRemoveCallback(Entity entity) {
		PowerHolderComponent.getPowerTypes(entity, PowerType.class, true).forEach(PowerType::onRemoved);
		EntitySetPowerType.integrateUnloadCallback(entity, (ServerLevel) entity.level());
		ModifyEnchantmentLevelPowerType.integrateCallback(entity, entity.level());
	}
}
