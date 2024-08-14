package io.github.dueris.originspaper.power;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ActionOnCallbackPower extends PowerType {
	private final ActionFactory<Entity> entityActionRespawned;
	private final ActionFactory<Entity> entityActionRemoved;
	private final ActionFactory<Entity> entityActionGained;
	private final ActionFactory<Entity> entityActionLost;
	private final ActionFactory<Entity> entityActionAdded;

	public ActionOnCallbackPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								 ActionFactory<Entity> entityActionRespawned, ActionFactory<Entity> entityActionRemoved, ActionFactory<Entity> entityActionGained, ActionFactory<Entity> entityActionLost, ActionFactory<Entity> entityActionAdded) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityActionRespawned = entityActionRespawned;
		this.entityActionRemoved = entityActionRemoved;
		this.entityActionGained = entityActionGained;
		this.entityActionLost = entityActionLost;
		this.entityActionAdded = entityActionAdded;
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_callback"))
			.add("entity_action_respawned", ApoliDataTypes.ENTITY_ACTION, null)
			.add("entity_action_removed", ApoliDataTypes.ENTITY_ACTION, null)
			.add("entity_action_gained", ApoliDataTypes.ENTITY_ACTION, null)
			.add("entity_action_lost", ApoliDataTypes.ENTITY_ACTION, null)
			.add("entity_action_added", ApoliDataTypes.ENTITY_ACTION, null);
	}

	@EventHandler
	public void onRespawn(@NotNull PlayerPostRespawnEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player)) {
			if (this.isActive(player) && entityActionRespawned != null) {
				entityActionRespawned.accept(player);
			}
		}
	}

	@Override
	public void onAdded(Player player) {
		super.onAdded(player);
	}

	@Override
	public void onGained(Player player) {
		if (this.isActive(player) && entityActionGained != null) {
			entityActionGained.accept(player);
		}
	}

	@Override
	public void onLost(Player player) {
		if (this.isActive(player) && entityActionLost != null) {
			entityActionLost.accept(player);
		}
	}

	@Override
	public void onRemoved(Player player) {
		if (this.isActive(player) && entityActionRemoved != null) {
			entityActionRemoved.accept(player);
		}
	}
}
