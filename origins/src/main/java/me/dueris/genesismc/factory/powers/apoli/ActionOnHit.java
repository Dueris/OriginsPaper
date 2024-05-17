package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.HudRender;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ActionOnHit extends PowerType implements CooldownPower {
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;
	private final int cooldown;
	private final FactoryJsonObject entityAction;
	private final HudRender hudRender;

	public ActionOnHit(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject bientityAction, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition, int cooldown, FactoryJsonObject hudRender, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_hit"))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new Gson().fromJson("{\"should_render\": false}", JsonObject.class)))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void action(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player p) {
			Entity target = e.getEntity();
			if (getPlayers().contains(p)) {
				if (Cooldown.isInCooldown(p, this)) return;
				if (!isActive(p)) return;
				if (!ConditionExecutor.testDamage(damageCondition, e)) return;
				if (!ConditionExecutor.testBiEntity(bientityCondition, (CraftEntity) p, (CraftEntity) target)) return;
				Actions.executeBiEntity(p, target, bientityAction);
				Cooldown.addCooldown(p, cooldown, this);
			}
		}
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	public FactoryJsonObject getBientityAction() {
		return bientityAction;
	}

	public FactoryJsonObject getEntityAction() {
		return entityAction;
	}

	public FactoryJsonObject getBientityCondition() {
		return bientityCondition;
	}

	public FactoryJsonObject getDamageCondition() {
		return damageCondition;
	}

}
