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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class SelfActionOnHit extends PowerType implements CooldownPower {
	private final FactoryJsonObject entityAction;
	private final int cooldown;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject targetAction;
	private final HudRender hudRender;

	public SelfActionOnHit(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, int cooldown, FactoryJsonObject damageCondition, FactoryJsonObject targetAction, FactoryJsonObject hudRender) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.cooldown = cooldown;
		this.damageCondition = damageCondition;
		this.targetAction = targetAction;
		this.hudRender = HudRender.createHudRender(hudRender);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("self_action_on_hit"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("target_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void s(@NotNull EntityDamageByEntityEvent e) {
		Entity target = e.getDamager();

		if (!(target instanceof Player player)) return;
		if (!getPlayers().contains(target)) return;

		if (Cooldown.isInCooldown((Player) target, this)) return;
		if (isActive(player) && ConditionExecutor.testDamage(damageCondition, e) && ConditionExecutor.testEntity(targetAction, target)) {
			Actions.executeEntity(target, entityAction);
			if (cooldown > 0) {
				Cooldown.addCooldown(player, cooldown, this);
			}
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
