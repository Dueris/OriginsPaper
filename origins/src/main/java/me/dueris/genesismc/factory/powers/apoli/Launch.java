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
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.FireProjectile.in_continuous;

public class Launch extends CraftPower implements Listener {


    @EventHandler
    public void inContinuousFix(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (getPowerArray().contains(p)) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), p)) {
                        in_continuous.putIfAbsent(p, new ArrayList<>());
                        if (false /* Launch power doesnt execute continuously */) {
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
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (getPowerArray().contains(p)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        if (!CooldownUtils.isPlayerInCooldownFromTag(p, Utils.getNameOrTag(power))) {
                            if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), p)) {
                                String key = power.get("key").getOrDefault("key", "key.origins.primary_active").toString();
                                KeybindingUtils.toggleKey(p, key);
                                final int[] times = {-1};
                                boolean cont = !Boolean.valueOf(power.get("key").getOrDefault("continuous", "false").toString());
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        int cooldown = power.getIntOrDefault("cooldown", 1);
                                        if (times[0] >= 0) {
                                            if ((!false /* Launch power doesnt execute continuously */ || !KeybindingUtils.activeKeys.get(p).contains(key)) && !in_continuous.get(p).contains(key)) {
                                                CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown, power.get("hud_render"));
                                                KeybindingUtils.toggleKey(p, key);
                                                setActive(p, power.getTag(), false);
                                                this.cancel();
                                                return;
                                            }
                                        }
                                        int speed = Integer.parseInt(power.getStringOrDefault("speed", null)); // used as string so that upon parsing the int it throws if not found
                                        CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown, power.get("hud_render"));
                                        setActive(p, power.getTag(), true);
                                        p.setVelocity(p.getVelocity().setY(0));
                                        p.setVelocity(p.getVelocity().setY(speed));
                                        p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
                                        setActive(p, power.getTag(), true);
                                        times[0]++;
                                    }
                                }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:launch";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return launch_into_air;
    }
}
