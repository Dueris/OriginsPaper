package me.dueris.originspaper.factory.powers;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class DisableRegenPower extends PowerType {
	public DisableRegenPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("disable_regen"));
	}

	@EventHandler
	public void onRegen(@NotNull EntityRegainHealthEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER) && getPlayers().contains(((CraftPlayer) e.getEntity()).getHandle())) {
			if (isActive(((CraftPlayer) e.getEntity()).getHandle())) e.setCancelled(true);
		}
	}
}
