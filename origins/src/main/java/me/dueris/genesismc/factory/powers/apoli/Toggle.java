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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Toggle extends CraftPower implements Listener {
    public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();

    @EventHandler
    public void inContinuousFix(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (getPowerArray().contains(p)) {
                in_continuous.putIfAbsent(p, new ArrayList<>());
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), p)) {
                        if (true /* Toggle power always execute continuously */) {
                            if (in_continuous.get(p).contains(power.get("key").getOrDefault("key", "key.origins.primary_active").toString())) {
                                in_continuous.get(p).remove(power.get("key").getOrDefault("key", "key.origins.primary_active").toString());
                            } else {
                                in_continuous.get(p).add(power.get("key").getOrDefault("key", "key.origins.primary_active").toString());
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (getPowerArray().contains(p)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        if (!CooldownUtils.isPlayerInCooldownFromTag(p, Utils.getNameOrTag(power))) {
                            if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), p)) {
                                execute(p, power);
                            }
                        }
                    }
                }
            }
        }
    }

    public void execute(Player p, Power power) {
        in_continuous.putIfAbsent(p, new ArrayList<>());
        int cooldown = power.getIntOrDefault("cooldown", 1);
        String key = (String) power.get("key").getOrDefault("key", "key.origins.primary_active");
        KeybindingUtils.toggleKey(p, key);

        new BukkitRunnable() {
            @Override
            public void run() {
                AtomicBoolean cond = new AtomicBoolean(ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p));
                /* Toggle power always execute continuously */
                if (!cond.get() || ((true) && !in_continuous.get(p).contains(key))) {
                    CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown, power.get("hud_render"));
                    KeybindingUtils.toggleKey(p, key);
                    setActive(p, power.getTag(), false);
                    this.cancel();
                    return;
                }

                setActive(p, power.getTag(), true);
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:toggle";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return toggle_power;
    }
}
