package me.dueris.genesismc.factory.powers.apoli;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown extends CraftPower {
	private static final ConcurrentHashMap<NamespacedKey, Double> incrementGetter = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Player, List<Pair<KeyedBossBar, Power>>> cooldowns = new ConcurrentHashMap<>();

	public static void addCooldown(Player player, int amt, Power power) {
		addCooldown(player, amt, power, 1.0);
	}

	protected static void addCooldown(Player player, int amt, Power power, double start) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		if (isInCooldown(player, power)) return; // Already in cooldown
		KeyedBossBar bossBar = Bukkit.createBossBar(
			NamespacedKey.fromString(power.getTag() + "_cooldown_" + player.getName().toLowerCase()),
			Utils.getNameOrTag(power).left(), Resource.Bar.getBarColor(power.getElement("hud_render")), BarStyle.SEGMENTED_6);
		bossBar.setProgress(start);
		bossBar.setVisible(true);
		bossBar.addPlayer(player);
		incrementGetter.put(bossBar.getKey(), 1.0 / amt);
		cooldowns.get(player).add(new Pair<>() {
			@Override
			public KeyedBossBar left() {
				return bossBar;
			}

			@Override
			public Power right() {
				return power;
			}
		});
	}

	public static boolean isInCooldown(Player player, Power power) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		for (Pair<KeyedBossBar, Power> pair : cooldowns.get(player)) {
			if (pair.right().getTag().equalsIgnoreCase(power.getTag())) return true;
		}
		return false;
	}

	@Override
	public String getType() {
		return "apoli:cooldown";
	}

	@Override
	public void run() {
		Utils.collectValues(new ArrayList<>(cooldowns.values())).forEach((pair) -> {
			KeyedBossBar bar = pair.left();
			if (!incrementGetter.containsKey(bar.getKey())) return;
			double increment = incrementGetter.get(bar.getKey());
			if (bar.getProgress() - increment <= 0) {
				bar.setProgress(0);
				bar.setVisible(false);
				bar.removeAll();
				Bukkit.getServer().removeBossBar(bar.getKey());
				scheduleRemoval(pair);
			} else {
				bar.setProgress(bar.getProgress() - increment);
			}
		});
	}

	private void scheduleRemoval(Pair<KeyedBossBar, Power> pair) {
		for (Player p : cooldowns.keySet()) {
			if (cooldowns.get(p).contains(pair)) {
				cooldowns.get(p).remove(pair);
				incrementGetter.remove(pair.left().getKey());
				break;
			}
		}
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return cooldown;
	}
}
