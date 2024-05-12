package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.data.types.JsonKeybind;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.KeybindingUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Toggle extends PowerType implements Listener, KeyedPower {
	public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();
	private final boolean activeByDefault;
	private final JsonKeybind key;
	private final boolean retainState;

	@Register
	public Toggle(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean activeByDefault, FactoryJsonObject key, boolean retainState) {
		super(name, description, hidden, condition, loading_priority);
		this.activeByDefault = activeByDefault;
		this.key = JsonKeybind.createJsonKeybind(key);
		this.retainState = retainState;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("toggle"))
			.add("active_by_default", boolean.class, true)
			.add("key", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("retain_state", boolean.class, true);
	}

	@EventHandler
	public void inContinuousFix(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			in_continuous.putIfAbsent(p, new ArrayList<>());
			if (KeybindingUtils.isKeyActive(getJsonKey().getKey(), p)) {
				if (in_continuous.get(p).contains(getJsonKey().getKey())) {
					in_continuous.get(p).remove(getJsonKey().getKey());
				} else {
					in_continuous.get(p).add(getJsonKey().getKey());
				}
			}
		}
	}

	@EventHandler
	public void activeByDefaultEvent(OriginChangeEvent e) {
		e.getOrigin().getPowerContainers().forEach(power -> {
			if (power instanceof Toggle toggle && toggle.isActiveByDefault()) {
				in_continuous.putIfAbsent(e.getPlayer(), new ArrayList<>());
				if (in_continuous.get(e.getPlayer()).contains(getJsonKey().getKey()))
					return;
				in_continuous.get(e.getPlayer()).add(getJsonKey().getKey());
				execute(e.getPlayer(), (KeyedPower) power);
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
				if ((!cond.get() && !getRetainState()) || (!in_continuous.get(p).contains(key))) {
					this.cancel();
				}
			}
		}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
	}

	@Override
	public boolean isActive(Player player) {
		return super.isActive(player) && in_continuous.getOrDefault(player, new ArrayList<>()).contains(key.getKey());
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
