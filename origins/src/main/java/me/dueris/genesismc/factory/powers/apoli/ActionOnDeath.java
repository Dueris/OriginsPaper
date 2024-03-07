package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionOnDeath extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void d(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPowerArray().contains(p)) {
				for (Layer layer : CraftApoli.getLayersFromRegistry()) {
					for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
						if (power == null) continue;
						if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p) && !ConditionExecutor.testDamage(power.get("damage_condition"), e.getEntity().getLastDamageCause()))
							return;
						if (((CraftPlayer) p).getHandle().getLastHurtByMob() != null) {
							if(!ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) p, ((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity())) return;

							setActive(p, power.getTag(), true);
							Actions.EntityActionType(p, power.getEntityAction());
							Actions.BiEntityActionType(((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p/* player is target? */, power.getBiEntityAction());
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								setActive(p, power.getTag(), false);
							}
						}.runTaskLater(GenesisMC.getPlugin(), 2L);
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:action_on_death";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return action_on_death;
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of(
			new FactoryObjectInstance("bientity_action", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("bientity_condition", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("damage_condition", JSONObject.class, new JSONObject())
		));
	}
}
