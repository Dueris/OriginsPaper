package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionOnWakeUp extends PowerType {
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject blockAction;
	private final FactoryJsonObject blockCondition;

	public ActionOnWakeUp(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject blockAction, FactoryJsonObject blockCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_wake_up"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void w(PlayerBedLeaveEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!getPlayers().contains(e.getPlayer()) || !e.getPlayer().getWorld().isDayTime()) return;
				if (!isActive(e.getPlayer()) || ConditionExecutor.testBlock(blockCondition, e.getBed())) return;
				Actions.executeEntity(e.getPlayer(), entityAction);
				Actions.executeBlock(e.getBed().getLocation(), blockAction);
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 2);
	}

}
