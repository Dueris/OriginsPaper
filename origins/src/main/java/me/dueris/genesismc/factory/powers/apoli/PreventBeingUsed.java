package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreventBeingUsed extends PowerType implements Listener {
	private final FactoryJsonObject itemCondition;

	public PreventBeingUsed(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_being_used"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void run(PlayerInteractEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			Player p = e.getPlayer();
			if (isActive(p) && ConditionExecutor.testItem(itemCondition, e.getItem())) {
				e.setCancelled(true);
			}
		}
	}

}
