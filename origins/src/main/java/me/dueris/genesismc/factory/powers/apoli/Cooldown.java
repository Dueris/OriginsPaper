package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.HudRender;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.ModifiableFloatPair;
import me.dueris.genesismc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static me.dueris.genesismc.factory.powers.apoli.Resource.currentlyDisplayed;
import static me.dueris.genesismc.factory.powers.apoli.Resource.serverLoadedBars;

public class Cooldown extends PowerType implements CooldownPower {
	private static final ConcurrentHashMap<KeyedBossBar, ModifiableFloatPair> timingsTracker = new ConcurrentHashMap<>();
	private static final Set<String> ticked = new HashSet<>();
	public static ConcurrentHashMap<Player, List<Pair<KeyedBossBar, ResourcePower>>> cooldowns = new ConcurrentHashMap<>();
	private final HudRender hudRender;
	private final int cooldown;

	public Cooldown(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject hudRender, int cooldown) {
		super(name, description, hidden, condition, loading_priority);
		this.hudRender = HudRender.createHudRender(hudRender);
		this.cooldown = cooldown;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("cooldown"))
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 0);
	}

	public static void addCooldown(Player player, int amt, CooldownPower power) {
		addCooldown(player, amt, power, 1.0);
	}

	protected static void addCooldown(Player player, int amt, CooldownPower power, double start) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		if (isInCooldown(player, power)) return; // Already in cooldown
		Resource.Bar bar = new Resource.Bar(power, player);
		Resource.currentlyDisplayed.putIfAbsent(player, new ArrayList<>());
		Resource.currentlyDisplayed.get(player).add(bar);
		Pair<KeyedBossBar, ResourcePower> pair = new Pair<>() {
			@Override
			public KeyedBossBar left() {
				return bar.renderedBar;
			}

			@Override
			public ResourcePower right() {
				return power;
			}
		};
		cooldowns.get(player).add(pair);
		timingsTracker.put(pair.key(), new ModifiableFloatPair(amt, amt));
	}

	public static boolean isInCooldown(Player player, ResourcePower power) {
		cooldowns.putIfAbsent(player, new ArrayList<>());
		for (Pair<KeyedBossBar, ResourcePower> pair : cooldowns.get(player)) {
			if (pair.right().getTag().equalsIgnoreCase(power.getTag())) return true;
		}
		return false;
	}

	@Override
	public boolean isActive(Player player) {
		return super.isActive(player) && isInCooldown(player, this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void preLoad(ServerLoadEvent e) {
		GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).values().stream()
			.filter(p -> (p.getType().equalsIgnoreCase(getType()))).forEach(power -> {
				serverLoadedBars.put(power.getTag(), null);
			});
	}

	@EventHandler
	public void serverTickEnd(ServerTickEndEvent e) {
		ticked.clear();
	}

	@Override
	public void tick() {
		Util.collectValues(new ArrayList<>(cooldowns.values())).forEach((pair) -> {
			KeyedBossBar bar = pair.left();
			String keyString = bar.getKey().asString();
			if (ticked.contains(keyString)) {
				return;
			}
			ticked.add(keyString);
			ModifiableFloatPair floatPair = timingsTracker.get(bar);
			float max = floatPair.a();
			float cur = floatPair.setB(floatPair.b() - 1);
			if (cur <= 0) {
				for (Player player : bar.getPlayers()) {
					Resource.currentlyDisplayed.putIfAbsent(player, new ArrayList<>());
					Resource.Bar b = null;
					for (Resource.Bar b1 : currentlyDisplayed.get(player)) {
						if (b1.renderedBar.equals(bar)) {
							b = b1;
							break;
						}
					}
					if (b != null) {
						currentlyDisplayed.get(player).remove(b);
					}
				}
				bar.setProgress(0);
				bar.setVisible(false);
				bar.removeAll();
				Bukkit.getServer().removeBossBar(bar.getKey());
				scheduleRemoval(pair);
			} else {
				bar.setProgress(1 - (cur / max));
			}
		});
	}

	private void scheduleRemoval(Pair<KeyedBossBar, ResourcePower> pair) {
		for (Player p : cooldowns.keySet()) {
			if (cooldowns.get(p).contains(pair)) {
				cooldowns.get(p).remove(pair);
				break;
			}
		}

		timingsTracker.remove(pair.key());
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}
}
