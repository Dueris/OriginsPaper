package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.KeybindTriggerEvent;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.factory.data.types.JsonKeybind;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.KeybindUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Toggle extends PowerType implements KeyedPower {
	public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();
	private final boolean activeByDefault;
	private final JsonKeybind key;
	private final boolean retainState;

	public Toggle(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean activeByDefault, FactoryElement key, boolean retainState) {
		super(name, description, hidden, condition, loading_priority);
		this.activeByDefault = activeByDefault;
		this.key = JsonKeybind.createJsonKeybind(key);
		this.retainState = retainState;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("toggle"))
			.add("active_by_default", boolean.class, true)
			.add("key", FactoryElement.class, new FactoryElement(new Gson().fromJson("{\"key\": \"key.origins.primary_active\"}", JsonElement.class)))
			.add("retain_state", boolean.class, true);
	}

	@EventHandler
	public void inContinuousFix(@NotNull KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			in_continuous.putIfAbsent(p, new ArrayList<>());
			if (KeybindUtil.isKeyActive(getJsonKey().key(), p)) {
				if (in_continuous.get(p).contains(getJsonKey().key())) {
					in_continuous.get(p).remove(getJsonKey().key());
				} else {
					in_continuous.get(p).add(getJsonKey().key());
				}
			}
		}
	}

	@EventHandler
	public void activeByDefaultEvent(@NotNull OriginChangeEvent e) {
		e.getOrigin().getPowerContainers().forEach(power -> {
			if (power instanceof Toggle toggle && toggle.isActiveByDefault()) {
				in_continuous.putIfAbsent(e.getPlayer(), new ArrayList<>());
				if (in_continuous.get(e.getPlayer()).contains(getJsonKey().key()))
					return;
				in_continuous.get(e.getPlayer()).add(getJsonKey().key());
				execute(e.getPlayer(), (KeyedPower) power);
			}
		});
	}

	@EventHandler
	public void keybindPress(@NotNull KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				if (KeybindUtil.isKeyActive(this.getJsonKey().key(), p)) {
					execute(p, this);
				}
			}
		}
	}

	public void execute(Player p, @NotNull KeyedPower power) {
		in_continuous.putIfAbsent(p, new ArrayList<>());
		String key = power.getJsonKey().key();

		new BukkitRunnable() {
			@Override
			public void run() {
				AtomicBoolean cond = new AtomicBoolean(power.isActive(p));
				/* Toggle power always execute continuously */
				if ((!cond.get() && !getRetainState()) || (!in_continuous.get(p).contains(key))) {
					this.cancel();
				}
			}
		}.runTaskTimer(OriginsPaper.getPlugin(), 0, 1);
	}

	@Override
	public boolean isActive(Player player) {
		return super.isActive(player) && in_continuous.getOrDefault(player, new ArrayList<>()).contains(key.key());
	}

	@Override
	public JsonKeybind getJsonKey() {
		return key;
	}

	public boolean isActiveByDefault() {
		return activeByDefault;
	}

	public boolean getRetainState() {
		return retainState;
	}
}
