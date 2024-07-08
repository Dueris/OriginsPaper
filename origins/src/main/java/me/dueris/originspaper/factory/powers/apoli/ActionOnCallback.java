package me.dueris.originspaper.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionOnCallback extends PowerType {
	private final FactoryJsonObject entityActionChosen;
	private final boolean executeChosenWhenOrb;
	private final FactoryJsonObject entityActionGained;
	private final FactoryJsonObject entityActionLost;
	private final FactoryJsonObject entityActionAdded;
	private final FactoryJsonObject entityActionRemoved;
	private final FactoryJsonObject entityActionRespawned;

	public ActionOnCallback(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityActionChosen, boolean executeChosenWhenOrb, FactoryJsonObject entityActionGained, FactoryJsonObject entityActionLost, FactoryJsonObject entityActionAdded, FactoryJsonObject entityActionRemoved, FactoryJsonObject entityActionRespawned) {
		super(name, description, hidden, condition, loading_priority);
		this.executeChosenWhenOrb = executeChosenWhenOrb;
		this.entityActionChosen = entityActionChosen;
		this.entityActionGained = entityActionGained;
		this.entityActionLost = entityActionLost;
		this.entityActionAdded = entityActionAdded;
		this.entityActionRemoved = entityActionRemoved;
		this.entityActionRespawned = entityActionRespawned;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_callback"))
			.add("entity_action_chosen", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("execute_chosen_when_orb", boolean.class, true)
			.add("entity_action_gained", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_lost", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_added", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_removed", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_respawned", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@Override
	public void bootstrapApply(Player player) {
		Actions.executeEntity(player, entityActionAdded);
		Actions.executeEntity(player, entityActionGained);
	}

	@Override
	public void bootstrapUnapply(Player player) {
		Actions.executeEntity(player, entityActionRemoved);
		Actions.executeEntity(player, entityActionLost);
	}

	@EventHandler
	public void choose(OriginChangeEvent e) {
		Player actor = e.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!getPlayers().contains(actor)) return;

				if (!isActive(actor)) return;
				if (!executeChosenWhenOrb && e.isFromOrb()) return;
				Actions.executeEntity(e.getPlayer(), entityActionChosen);
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 1);
	}

	@EventHandler
	public void respawn(PlayerPostRespawnEvent e) {
		Player player = e.getPlayer();
		if (!getPlayers().contains(player)) return;
		Actions.executeEntity(e.getPlayer(), entityActionRespawned);
	}

}
