package me.dueris.genesismc.factory.powers.apoli;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.util.CooldownUtils.createCooldownBar;
import static me.dueris.genesismc.util.CooldownUtils.getBarColor;

public class Resource extends CraftPower implements Listener {
	public static HashMap<Player, HashMap<String, Pair<BossBar, Double>>> registeredBars = new HashMap();
	public static Resource INSTANCE = new Resource();

	public static double countNumbersBetween(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException("Start integer should be less than or equal to end integer.");
		}

		int count = 0;

		for (int i = start + 1; i < end; i++) {
			count++;
		}

		return count + 1;
	}

	public static Pair<BossBar, Double> getResource(Entity entity, String tag) {
		if (registeredBars.containsKey(entity) && registeredBars.get(entity).containsKey(tag)) {
			return registeredBars.get(entity).get(tag);
		}
		return null;
	}

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void start(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute(e.getPlayer());
			}
		}.runTaskLater(GenesisMC.getPlugin(), 5);
	}

	@EventHandler
	public void start(OriginChangeEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute(e.getPlayer());
			}
		}.runTaskLater(GenesisMC.getPlugin(), 5);
	}

	private void execute(Player p) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
				final String tag = power.getTag();
				JSONObject hudRender = power.get("hud_render");
				BossBar bar = createCooldownBar(p, getBarColor(hudRender), BarStyle.SEGMENTED_6, Utils.getNameOrTag(power).first());
				bar.setProgress(1.0);
				Pair<BossBar, Double> pair = new Pair<BossBar, Double>() {
					@Override
					public BossBar left() {
						return bar;
					}

					@Override
					public Double right() {
						return countNumbersBetween(power.getIntOrDefault("start_value", power.getInt("min")), power.getInt("max"));
					}
				};
				HashMap<String, Pair<BossBar, Double>> map = new HashMap<>();
				map.put(tag, pair);
				registeredBars.put(p, map);
				final boolean[] shouldRender = {true};
				if (power.get("hud_render") != null) {
					new BukkitRunnable() {
						@Override
						public void run() {
							HashMap<String, Object> hud_render = power.get("hud_render");
							final boolean[] canRender = {true};
							if ((boolean) hud_render.getOrDefault("should_render", false)) {
								shouldRender[0] = canRender[0];
							}
							if (hud_render.containsKey("condition")) {
								canRender[0] = ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p);
							}
						}
					}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
				}
				bar.setVisible(shouldRender[0]);
				bar.addPlayer(p);
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:resource";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return resource;
	}
}
