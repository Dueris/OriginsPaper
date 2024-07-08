package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends PowerType {

	public Invisibility(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("invisibility"));
	}

	@Override
	public void tick(Player p) {
		boolean shouldSetInvisible = isActive(p);

		p.setInvisible(shouldSetInvisible || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
	}

	public void doesntHavePower(Player p) {
		if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
			return;
		}
		p.setInvisible(false);
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		if (!getPlayers().contains(e.getPlayer())) {
			doesntHavePower(e.getPlayer());
			return;
		}
		tick(e.getPlayer());
	}

}
