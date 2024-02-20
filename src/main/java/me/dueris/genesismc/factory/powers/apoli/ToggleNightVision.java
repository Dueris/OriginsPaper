package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.FireProjectile.in_continuous;

public class ToggleNightVision extends CraftPower implements Listener {

    public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();

    @Override
    public void run(Player p) {

    }

    public void execute(Player p, PowerContainer power){
        in_continuous.putIfAbsent(p, new ArrayList<>());
        int cooldown = power.getIntOrDefault("cooldown", 1);
        String key = (String) power.get("key").getOrDefault("key", "key.origins.primary_active");
        KeybindingUtils.toggleKey(p, key);

        boolean cont = !Boolean.valueOf(power.get("key").getOrDefault("continuous", "false").toString());
        new BukkitRunnable() {

            @Override
            public void run() {
                if((true /* TNV power always execute continuously */ || !KeybindingUtils.activeKeys.get(p).contains(key)) && !in_continuous.get(p).contains(key)){
                    CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown, key);
                    KeybindingUtils.toggleKey(p, key);
                    setActive(p, power.getTag(), false);
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    this.cancel();
                    return;
                }

                PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, 500, 0, false, false, false);
                if(p.hasPotionEffect(PotionEffectType.NIGHT_VISION)){
                    // Check duration
                    if(p.getPotionEffect(PotionEffectType.NIGHT_VISION).getDuration() < 350){
                        p.addPotionEffect(effect);
                    }
                }else{
                    p.addPotionEffect(effect);
                }
                setActive(p, power.getTag(), true);
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (LayerContainer layer : CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (getPowerArray().contains(p)) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
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

    @EventHandler
    public void inContinuousFix(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (LayerContainer layer : CraftApoli.getLayers()) {
            if (getPowerArray().contains(p)) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), p)) {
                        in_continuous.putIfAbsent(p, new ArrayList<>());
                        if(true /* TNV power always execute continuously */){
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

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
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
