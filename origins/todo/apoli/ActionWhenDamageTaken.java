package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class ActionWhenDamageTaken extends PowerType implements CooldownPower {
	private final HudRender hudRender;
	private final int cooldown;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject entityAction;

	public ActionWhenDamageTaken(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject damageCondition, int cooldown, FactoryJsonObject hudRender) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_when_damage_taken"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void d(@NotNull EntityDamageEvent e) {
		if (e.getDamage() == 0 || e.isCancelled()) return;
		Entity actor = e.getEntity();
		if (!(actor instanceof Player player)) return;
		if (!getPlayers().contains(player) || Cooldown.isInCooldown(player, this)) return;
		if (!isActive(player)) return;
		if (!ConditionExecutor.testDamage(damageCondition, e)) return;
		Actions.executeEntity(actor, entityAction);
		Cooldown.addCooldown(player, cooldown, this);
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}
}
