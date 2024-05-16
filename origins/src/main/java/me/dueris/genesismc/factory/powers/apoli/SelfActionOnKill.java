package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.data.types.HudRender;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SelfActionOnKill extends PowerType implements Listener, CooldownPower {
	private final FactoryJsonObject entityAction;
	private final int cooldown;
	private final HudRender hudRender;

	public SelfActionOnKill(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, int cooldown, FactoryJsonObject hudRender) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("self_action_on_kill"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void k(EntityDeathEvent e) {
		Entity target = e.getEntity();

		if (!(target instanceof Player player)) return;
		if (!getPlayers().contains(target)) return;

		if (Cooldown.isInCooldown(player, this)) return;
		if (isActive(player)) {
			Actions.executeEntity(target, entityAction);
			Cooldown.addCooldown(player, cooldown, this);
		}
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}
}
