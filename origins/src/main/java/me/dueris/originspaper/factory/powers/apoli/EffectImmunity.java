package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EffectImmunity extends PowerType {
	private final List<NamespacedKey> effects;
	private final boolean inverted;

	public EffectImmunity(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, NamespacedKey effect, FactoryJsonArray effects, boolean inverted) {
		super(name, description, hidden, condition, loading_priority);
		this.effects = effect == null ? effects.asList().stream().map(FactoryElement::getString).map(NamespacedKey::fromString).toList() : List.of(effect);
		this.inverted = inverted;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("effect_immunity"))
			.add("effect", NamespacedKey.class, new OptionalInstance())
			.add("effects", FactoryJsonArray.class, new OptionalInstance())
			.add("inverted", boolean.class, false);
	}

	@Override
	public void tick(Player p) {
		if (!effects.isEmpty() && isActive(p)) {
			List<PotionEffectType> toRemove = new ArrayList<>();
			p.getActivePotionEffects().forEach(potionEffect -> {
				boolean shouldRemove = inverted != effects.contains(potionEffect.getType().getKey());
				if (shouldRemove) {
					toRemove.add(potionEffect.getType());
				}
			});

			for (PotionEffectType potionEffectType : toRemove) {
				p.removePotionEffect(potionEffectType);
			}
		}
	}

	public boolean isInverted() {
		return inverted;
	}

	public List<NamespacedKey> getEffects() {
		return effects;
	}
}
