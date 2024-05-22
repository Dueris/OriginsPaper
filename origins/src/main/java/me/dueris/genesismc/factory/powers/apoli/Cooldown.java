package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.HudRender;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Cooldown extends PowerType {
	private static final ConcurrentHashMap<NamespacedKey, Double> incrementGetter = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Player, List<Pair<KeyedBossBar, ResourcePower>>> cooldowns = new ConcurrentHashMap<>();
	private final HudRender hudRender;

	public Cooldown(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject hudRender) {
		super(name, description, hidden, condition, loading_priority);
		this.hudRender = HudRender.createHudRender(hudRender);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("cooldown"))
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public static void addCooldown(Player player, int amt, ResourcePower power) {
		addCooldown(player, amt, power, 1.0);
	}

	protected static void addCooldown(Player player, int amt, ResourcePower power, double start) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		if (isInCooldown(player, power)) return; // Already in cooldown
		KeyedBossBar bossBar = Bukkit.createBossBar(
			NamespacedKey.fromString(power.getTag() + "_cooldown_" + player.getName().toLowerCase()),
			Util.getNameOrTag((PowerType) power), Resource.Bar.getBarColor(power.getHudRender()), BarStyle.SEGMENTED_6);
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
			public ResourcePower right() {
				return power;
			}
		});
	}

	public static boolean isInCooldown(Player player, ResourcePower power) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		for (Pair<KeyedBossBar, ResourcePower> pair : cooldowns.get(player)) {
			if (pair.right().getTag().equalsIgnoreCase(power.getTag())) return true;
		}
		return false;
	}

	@Override
	public void tick() {
		Util.collectValues(new ArrayList<>(cooldowns.values())).forEach((pair) -> {
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

	private void scheduleRemoval(Pair<KeyedBossBar, ResourcePower> pair) {
		for (Player p : cooldowns.keySet()) {
			if (cooldowns.get(p).contains(pair)) {
				cooldowns.get(p).remove(pair);
				incrementGetter.remove(pair.left().getKey());
				break;
			}
		}
	}

	public HudRender getHudRender() {
		return hudRender;
	}
}
