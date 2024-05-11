package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class ModifyStatusEffectDurationPower extends CraftPower implements Listener {

	@EventHandler
	public void runD(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (!modify_effect_duration.contains(p)) return;
			for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
				if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
					setActive(p, power.getTag(), true);
					if (power.getStringOrDefault("status_effect", null) != null) {
						if (e.getNewEffect().getType().equals(PotionEffectType.getByName(power.getStringOrDefault("status_effect", null)))) {
							PotionEffect effect = e.getNewEffect();
							for (Modifier modifier : power.getModifiers()) {
								Float value = modifier.value();
								String operation = modifier.operation();
								BinaryOperator mathOperator = Utils.getOperationMappingsFloat().get(operation);
								if (mathOperator != null) {
									float result = (float) mathOperator.apply(effect.getDuration(), value);
									effect.withDuration(Math.toIntExact(Long.parseLong(String.valueOf(result))));
								}
							}

						}
					} else {
						for (PotionEffect effect : p.getActivePotionEffects()) {
							for (Modifier modifier : power.getModifiers()) {
								Float value = modifier.value();
								String operation = modifier.operation();
								BinaryOperator mathOperator = Utils.getOperationMappingsFloat().get(operation);
								if (mathOperator != null) {
									float result = (float) mathOperator.apply(effect.getDuration(), value);
									effect.withDuration(Math.toIntExact(Long.parseLong(String.valueOf(result))));
								}
							}
						}
					}
				} else {
					setActive(p, power.getTag(), false);
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:modify_status_effect_duration";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_effect_duration;
	}
}
