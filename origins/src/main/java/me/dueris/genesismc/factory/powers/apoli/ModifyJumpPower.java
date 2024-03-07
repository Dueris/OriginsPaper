package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower extends CraftPower implements Listener {

	@EventHandler
	public void ruDn(PlayerJumpEvent e) {
		Player p = e.getPlayer();
		if (modify_jump.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
							if (modifier.get("value") instanceof Number) {
								double modifierValue = ((Number) modifier.get("value")).doubleValue();
								int jumpBoostLevel = (int) /*((modifierValue - 1.0) * 2.0)*/ Math.round(modifierValue * 4);

								if (jumpBoostLevel >= 0) {
									p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, jumpBoostLevel, false, false, false));
									setActive(p, power.getTag(), true);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:modify_jump";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return modify_jump;
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of(
			new FactoryObjectInstance("modifier", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("modifiers", JSONArray.class, new JSONArray()),
			new FactoryObjectInstance("entity_action", JSONObject.class, new JSONObject())
		));
	}
}
