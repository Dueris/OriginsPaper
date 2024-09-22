package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class DisableRegenPower extends PowerType {
	public DisableRegenPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("disable_regen"), PowerType.getFactory().getSerializableData());
	}

	@EventHandler
	public void onRegen(@NotNull EntityRegainHealthEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER) && getPlayers().contains(((CraftPlayer) e.getEntity()).getHandle())) {
			if (isActive(((CraftPlayer) e.getEntity()).getHandle())) e.setCancelled(true);
		}
	}
}
