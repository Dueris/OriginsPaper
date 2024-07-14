package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonArray;
import me.dueris.calio.data.CalioDataTypes;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BinaryOperator;

public class ModifyStatusEffectDurationPower extends ModifierPower implements Listener {
	private final List<PotionEffectType> statusEffects;
	private final HashMap<Player, List<PotionEffectType>> applied = new HashMap<>();

	public ModifyStatusEffectDurationPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, ResourceLocation statusEffect, FactoryJsonArray statusEffects) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.statusEffects = statusEffect == null ? statusEffects.asList().stream().map(FactoryElement::handle).map(CalioDataTypes::potionEffectType).toList() : List.of(PotionEffectType.getByKey(CraftNamespacedKey.fromMinecraft(statusEffect)));
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_status_effect_duration"))
			.add("status_effect", ResourceLocation.class, new OptionalInstance())
			.add("status_effects", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void modify(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (!getPlayers().contains(p)) return;
			if (isActive(p)) {
				PotionEffect effect = e.getNewEffect();
				if (effect != null && (statusEffects.isEmpty() || statusEffects.contains(effect.getType()))) {
					applied.putIfAbsent(p, new ArrayList<>());
					if (applied.get(p).contains(effect.getType())) return;
					applied.get(p).add(effect.getType());
					e.setCancelled(true);
					for (Modifier modifier : getModifiers()) {
						Float value = modifier.value();
						String operation = modifier.operation();
						BinaryOperator<Float> mathOperator = Util.getOperationMappingsFloat().get(operation);
						if (mathOperator != null) {
							float result = mathOperator.apply(Float.valueOf(effect.getDuration()), value);
							effect = effect.withDuration(Math.round(result));
						}
					}
					PotionEffect finalEffect = effect;
					new BukkitRunnable() {
						@Override
						public void run() {
							applied.get(p).remove(finalEffect.getType());
						}
					}.runTaskLater(OriginsPaper.getPlugin(), 1);
					p.addPotionEffect(effect);
				}
			}
		}
	}

}
