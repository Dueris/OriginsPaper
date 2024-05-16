package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

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

	public static FactoryData regsiterComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_callback"))
			.add("entity_action_chosen", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("execute_chosen_when_orb", boolean.class, true)
			.add("entity_action_gained", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_lost", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_added", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_removed", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action_respawned", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void choose(OriginChangeEvent e) {
		Player actor = e.getPlayer();

		if (!getPlayers().contains(actor)) return;

		if (!isActive(actor)) return;
		if (executeChosenWhenOrb && !e.isFromOrb()) return;
		Actions.executeEntity(e.getPlayer(), entityActionChosen);
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		Player player = e.getPlayer();
		if (!getPlayers().contains(player) || !e.getPower().getTag().equalsIgnoreCase(getTag())) return;
		if (e.isRemoved()) {
			Actions.executeEntity(e.getPlayer(), entityActionRemoved);
			Actions.executeEntity(e.getPlayer(), entityActionLost);
		} else {
			Actions.executeEntity(e.getPlayer(), entityActionAdded);
			Actions.executeEntity(e.getPlayer(), entityActionGained);
		}
	}

	@EventHandler
	public void respawn(PlayerPostRespawnEvent e) {
		Player player = e.getPlayer();
		if (!getPlayers().contains(player)) return;
		Actions.executeEntity(e.getPlayer(), entityActionRespawned);
	}

}
