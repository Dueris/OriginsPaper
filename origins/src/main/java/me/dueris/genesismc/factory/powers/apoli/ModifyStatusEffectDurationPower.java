package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonArray;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.function.BinaryOperator;

public class ModifyStatusEffectDurationPower extends ModifierPower implements Listener {

	private final List<PotionEffectType> statusEffects;

	public ModifyStatusEffectDurationPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, NamespacedKey statusEffect, FactoryJsonArray statusEffects) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.statusEffects = statusEffect == null ? statusEffects.asList().stream().map(FactoryElement::getString).map(NamespacedKey::fromString).map(PotionEffectType::getByKey).toList() : List.of(PotionEffectType.getByKey(statusEffect));
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_status_effect_amplifier"))
			.add("status_effect", NamespacedKey.class, new OptionalInstance())
			.add("status_effects", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()));
	}

	@EventHandler
	public void runD(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (!getPlayers().contains(p)) return;
			if (isActive(p)) {
				for (PotionEffect effect : p.getActivePotionEffects().stream().filter(effect -> statusEffects.contains(effect.getType())).toList()) {
					for (Modifier modifier : getModifiers()) {
						Float value = modifier.value();
						String operation = modifier.operation();
						BinaryOperator mathOperator = Util.getOperationMappingsFloat().get(operation);
						if (mathOperator != null) {
							float result = (float) mathOperator.apply(effect.getAmplifier(), value);
							effect.withDuration(Math.round(result));
						}
					}
				}
			}
		}
	}

}
