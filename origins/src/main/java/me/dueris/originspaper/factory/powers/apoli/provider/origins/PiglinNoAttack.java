package me.dueris.originspaper.factory.powers.apoli.provider.origins;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.apoli.provider.PowerProvider;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class PiglinNoAttack implements Listener, PowerProvider {
	protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("piglin_brothers");
	static ArrayList<EntityType> piglinValid = new ArrayList<>();

	static {
		piglinValid.add(EntityType.PIGLIN);
		piglinValid.add(EntityType.PIGLIN_BRUTE);
		piglinValid.add(EntityType.ZOMBIFIED_PIGLIN);
	}

	private final HashMap<Player, HashMap<Entity, Integer>> cooldowns = new HashMap<>();

	@Override
	public void tick(Player p) {
		if (cooldowns.containsKey(p)) {
			for (Entity en : cooldowns.get(p).keySet()) {
				if (cooldowns.get(p).get(en) <= 1) {
					cooldowns.get(p).remove(en);
				} else {
					HashMap<Entity, Integer> map = new HashMap<>();
					map.put(en, cooldowns.get(p).get(en) - 1);
					cooldowns.put(p, map);
				}
			}
		}
	}

	@EventHandler
	public void target(@NotNull EntityTargetEvent e) {
		if (piglinValid.contains(e.getEntity().getType())) {
			if (PowerHolderComponent.hasPower(e.getTarget(), powerReference.toString())) {
				if (!cooldowns.containsKey(e.getTarget())) {
					cooldowns.put((Player) e.getTarget(), new HashMap<>());
				}
				if (!cooldowns.get((Player) e.getTarget()).containsKey(e.getEntity())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void damageEntity(@NotNull EntityDamageByEntityEvent e) {
		if (piglinValid.contains(e.getEntity().getType())) {
			if (PowerHolderComponent.hasPower(e.getDamager(), powerReference.toString())) {
				Player p = (Player) e.getDamager();
				HashMap<Entity, Integer> map = new HashMap<>();
				map.put(e.getEntity(), 600);
				cooldowns.put(p, map);
			}
		}
	}

}
