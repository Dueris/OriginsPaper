package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect.getPotionEffectType;

public class EffectImmunity extends CraftPower {

	public EffectImmunity() {

	}

	@Override
	public void run(Player p) {
		if (effect_immunity.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
						List<String> effects = new ArrayList<>();
						if (power.getStringOrDefault("effect", null) != null) {
							effects.add(power.getString("effect"));
						}
						if (!power.getStringList("effects").isEmpty()) {
							effects.addAll(power.getStringList("effects"));
						}
						if(power.getBoolean("inverted")){
							for(PotionEffect type : p.getActivePotionEffects()){
								if(!effects.contains(type.getType().getKey().asString())){
									p.removePotionEffect(type.getType());
								}
							}
						}else{
							if (!effects.isEmpty()) {
								for (String effectString : effects) {
									PotionEffectType effectType = getPotionEffectType(effectString);
									if (effectType != null) {
										if (p.hasPotionEffect(effectType)) {
											p.removePotionEffect(effectType);
										}
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
	}


	@Override
	public String getPowerFile() {
		return "apoli:effect_immunity";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return effect_immunity;
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of(
			new FactoryObjectInstance("effect", String.class, ""),
			new FactoryObjectInstance("effects", JSONArray.class, new JSONArray()),
			new FactoryObjectInstance("inverted", Boolean.class, false)
		));
	}
}
