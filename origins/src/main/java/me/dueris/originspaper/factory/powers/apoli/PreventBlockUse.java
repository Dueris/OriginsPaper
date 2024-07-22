package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PreventBlockUse extends PowerType implements Listener {
	private final FactoryJsonObject blockCondition;

	public PreventBlockUse(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject blockCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.blockCondition = blockCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_block_use"))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void run(@NotNull PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (getPlayers().contains(e.getPlayer())) {
			if (ConditionExecutor.testBlock(blockCondition, (CraftBlock) e.getClickedBlock()) && isActive(e.getPlayer())) {
				e.setCancelled(true);
			}
		}
	}

}
