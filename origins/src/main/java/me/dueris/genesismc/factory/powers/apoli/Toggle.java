package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Toggle extends PowerType implements Listener, KeyedPower {
	public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();

	@EventHandler
	public void inContinuousFix(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			in_continuous.putIfAbsent(p, new ArrayList<>());
			for (PowerType power : OriginPlayerAccessor.getPowers(p, getType())) {
				if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
					if (true /* Toggle power always execute continuously */) {
						if (in_continuous.get(p).contains(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"))) {
							in_continuous.get(p).remove(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"));
						} else {
							in_continuous.get(p).add(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void activeByDefault(OriginChangeEvent e) {
		e.getOrigin().getPowerContainers().forEach(power -> {
			if (power.getType().equalsIgnoreCase(getType()) && power.getBooleanOrDefault("active_by_default", true)) {
				in_continuous.putIfAbsent(e.getPlayer(), new ArrayList<>());
				if (in_continuous.get(e.getPlayer()).contains(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active")))
					return;
				in_continuous.get(e.getPlayer()).add(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"));
				execute(e.getPlayer(), power);
			}
		});
	}

	@EventHandler
	public void keybindPress(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				if (KeybindingUtils.isKeyActive(this.getJsonKey().getKey(), p)) {
					execute(p, this);
				}
			}
		}
	}

	public void execute(Player p, KeyedPower power) {
		in_continuous.putIfAbsent(p, new ArrayList<>());
		String key = power.getJsonKey().getKey();

		new BukkitRunnable() {
			@Override
			public void run() {
				AtomicBoolean cond = new AtomicBoolean(power.isActive(p));
				/* Toggle power always execute continuously */
				if (!cond.get() || (!in_continuous.get(p).contains(key))) {
					this.cancel();
				}
			}
		}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
	}
}
