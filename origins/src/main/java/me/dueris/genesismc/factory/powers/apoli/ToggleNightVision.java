package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleNightVision extends CraftPower implements Listener {

    public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();

    @Override
    public void run(Player p) {

    }

    public void execute(Player p, Power power) {
        in_continuous.putIfAbsent(p, new ArrayList<>());
        int cooldown = power.getNumberOrDefault("cooldown", 1).getInt();
        String key = power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active");
        KeybindingUtils.toggleKey(p, key);

        boolean cont = !Boolean.valueOf(power.getJsonObject("key").getStringOrDefault("continuous", "false"));
        new BukkitRunnable() {

            @Override
            public void run() {
                /* TNV power always execute continuously */
                if (!in_continuous.get(p).contains(key)) {
                    CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), cooldown, power.getJsonObject("hud_render"));
                    KeybindingUtils.toggleKey(p, key);
                    setActive(p, power.getTag(), false);
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
                setActive(p, power.getTag(), true);
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (getPowerArray().contains(p)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                        if (!CooldownUtils.isPlayerInCooldownFromTag(p, Utils.getNameOrTag(power))) {
                            if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
                                execute(p, power);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void inContinuousFix(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (getPowerArray().contains(p)) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
                        in_continuous.putIfAbsent(p, new ArrayList<>());
                        if (true /* TNV power always execute continuously */) {
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
    }


    @Override
    public String getPowerFile() {
        return "apoli:toggle_night_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return toggle_night_vision;
    }
}
