package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ActionOnCallbackPower extends PowerType {
	private final ActionTypeFactory<Entity> entityActionRespawned;
	private final ActionTypeFactory<Entity> entityActionRemoved;
	private final ActionTypeFactory<Entity> entityActionGained;
	private final ActionTypeFactory<Entity> entityActionLost;
	private final ActionTypeFactory<Entity> entityActionAdded;

	public ActionOnCallbackPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 ActionTypeFactory<Entity> entityActionRespawned, ActionTypeFactory<Entity> entityActionRemoved, ActionTypeFactory<Entity> entityActionGained, ActionTypeFactory<Entity> entityActionLost, ActionTypeFactory<Entity> entityActionAdded) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityActionRespawned = entityActionRespawned;
		this.entityActionRemoved = entityActionRemoved;
		this.entityActionGained = entityActionGained;
		this.entityActionLost = entityActionLost;
		this.entityActionAdded = entityActionAdded;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_callback"))
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
		if (this.isActive(player) && entityActionAdded != null) {
			entityActionAdded.accept(player);
		}
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
