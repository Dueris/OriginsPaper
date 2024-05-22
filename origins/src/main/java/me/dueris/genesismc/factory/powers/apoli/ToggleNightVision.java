package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.data.types.JsonKeybind;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.KeybindUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleNightVision extends PowerType implements KeyedPower {
	public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();
	private final JsonKeybind key;

	public ToggleNightVision(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryElement key) {
		super(name, description, hidden, condition, loading_priority);
		this.key = JsonKeybind.createJsonKeybind(key);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("toggle_night_vision"))
			.add("key", FactoryElement.class, new FactoryElement(new Gson().fromJson("{\"key\": \"key.origins.primary_active\"}", JsonElement.class)));
	}

	public void execute(Player p) {
		in_continuous.putIfAbsent(p, new ArrayList<>());
		String key = getJsonKey().key();

		new BukkitRunnable() {
			@Override
			public void run() {
				/* TNV power always execute continuously */
				if (!in_continuous.get(p).contains(key) || !isActive(p)) {
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					this.cancel();
					return;
				}

				PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, 500, 0, false, false, false);
				if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					// Check duration
					if (p.getPotionEffect(PotionEffectType.NIGHT_VISION).getDuration() < 350) {
						p.addPotionEffect(effect);
					}
				} else {
					p.addPotionEffect(effect);
				}
			}
		}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
	}

	@EventHandler
	public void keybindToggle(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				if (KeybindUtil.isKeyActive(getJsonKey().key(), p)) {
					execute(p);
				}
			}
		}
	}

	@EventHandler
	public void inContinuousFix(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (KeybindUtil.isKeyActive(getJsonKey().key(), p)) {
				in_continuous.putIfAbsent(p, new ArrayList<>());
				if (in_continuous.get(p).contains(getJsonKey().key())) {
					in_continuous.get(p).remove(getJsonKey().key());
				} else {
					in_continuous.get(p).add(getJsonKey().key());
				}
			}
		}
	}

	@Override
	public JsonKeybind getJsonKey() {
		return key;
	}
}
