package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.HudRender;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackerActionWhenHit extends PowerType implements CooldownPower {
	private final int cooldown;
	private final HudRender hudRender;
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject damageCondition;

	public AttackerActionWhenHit(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, int cooldown, FactoryJsonObject hudRender, FactoryJsonObject damageCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("attacker_action_when_hit"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void a(EntityDamageByEntityEvent e) {
		Entity actor = e.getEntity();

		if (!(actor instanceof Player player)) return;
		if (!getPlayers().contains(player)) return;
		if (!isActive(player) || !ConditionExecutor.testDamage(damageCondition, e)) return;
		if (Cooldown.isInCooldown(player, this)) return;

		Actions.executeEntity(e.getDamager(), entityAction);
		Cooldown.addCooldown(player, cooldown, this);
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
