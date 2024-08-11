package me.dueris.originspaper.power;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.HudRender;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.ModifiableFloatPair;
import me.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static me.dueris.originspaper.power.ResourcePower.currentlyDisplayed;
import static me.dueris.originspaper.power.ResourcePower.serverLoadedBars;

public class CooldownPower extends PowerType implements CooldownInterface {
	private static final ConcurrentHashMap<KeyedBossBar, ModifiableFloatPair> timingsTracker = new ConcurrentHashMap<>();
	private static final Set<String> ticked = new HashSet<>();
	public static ConcurrentHashMap<Player, List<Pair<KeyedBossBar, ResourceInterface>>> cooldowns = new ConcurrentHashMap<>();
	private final HudRender hudRender;
	private final int cooldown;

	public CooldownPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						 HudRender hudRender, int cooldown) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.hudRender = hudRender;
		this.cooldown = cooldown;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("cooldown"))
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 0);
	}

	public static void addCooldown(org.bukkit.entity.Entity player, CooldownInterface power) {
		if (power.getCooldown() <= 1 || !(player instanceof Player)) return;
		addCooldown((Player) player, power);
	}

	protected static void addCooldown(Player player, CooldownInterface power) {
		if (power.getCooldown() <= 1) return;
		cooldowns.putIfAbsent(player, new ArrayList<>());
		if (isInCooldown(player, power)) return; // Already in cooldown
		ResourcePower.Bar bar = new ResourcePower.Bar(power, player);
		currentlyDisplayed.putIfAbsent(player, new ArrayList<>());
		currentlyDisplayed.get(player).add(bar);
		Pair<KeyedBossBar, ResourceInterface> pair = new Pair<>(bar.renderedBar, power);
		cooldowns.get(player).add(pair);
		timingsTracker.put(pair.first(), new ModifiableFloatPair(power.getCooldown(), power.getCooldown()));
	}

	public static boolean isInCooldown(org.bukkit.entity.Entity player, ResourceInterface power) {
		if (!(player instanceof Player)) return false;
		cooldowns.putIfAbsent((Player) player, new ArrayList<>());
		for (Pair<KeyedBossBar, ResourceInterface> pair : cooldowns.get(player)) {
			if (pair.second().getTag().equalsIgnoreCase(power.getTag())) return true;
		}
		return false;
	}

	@Override
	public boolean isActive(@NotNull Entity player) {
		return super.isActive(player) && isInCooldown(player.getBukkitEntity(), this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void preLoad(ServerLoadEvent e) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).values().stream()
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
		Util.collapseList(new ArrayList<>(cooldowns.values())).forEach((pair) -> {
			KeyedBossBar bar = pair.first();
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
					currentlyDisplayed.putIfAbsent(player, new ArrayList<>());
					ResourcePower.Bar b = null;
					for (ResourcePower.Bar b1 : currentlyDisplayed.get(player)) {
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

	private void scheduleRemoval(Pair<KeyedBossBar, ResourceInterface> pair) {
		for (Player p : cooldowns.keySet()) {
			if (cooldowns.get(p).contains(pair)) {
				cooldowns.get(p).remove(pair);
				break;
			}
		}

		timingsTracker.remove(pair.first());
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
