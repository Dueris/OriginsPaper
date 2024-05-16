package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision extends PowerType {
	private final int strength;

	public NightVision(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, float strength) {
		super(name, description, hidden, condition, loading_priority);
		this.strength = Math.round(strength);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("night_vision"))
			.add("strength", float.class, 1.0F);
	}

	@Override
	public void tick(Player p) {
		if (isActive(p)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, strength, false, false, false));
		} else {
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
	}
}
