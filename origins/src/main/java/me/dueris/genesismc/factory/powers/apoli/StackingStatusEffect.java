package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class StackingStatusEffect extends CraftPower implements Listener {
    public static PotionEffectType getPotionEffectType(String effectString) {
        if (effectString == null) {
            return null;
        }
        return PotionEffectType.getByKey(NamespacedKey.fromString(effectString));
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer() == null) cancel();
                runExecution(e.getPlayer());
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 40);
    }

    @EventHandler
    public void lol(OriginChangeEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer() == null) cancel();
                runExecution(e.getPlayer());
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 40);
    }

    public void runExecution(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) && ConditionExecutor.testEntity(power.getJsonObject("entity_condition"), (CraftEntity) p)) {
                        setActive(p, power.getTag(), true);
                        applyStackingEffect(p, power);
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
    }

    private void applyStackingEffect(Player player, Power power) {
        for (JSONObject effect : power.getList$SingularPlural("effect", "effects")) {
            PotionEffectType potionEffectType = getPotionEffectType(effect.get("effect").toString());
            if (potionEffectType != null) {
                try {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 50, 1, false, false, true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Bukkit.getLogger().warning("Unknown effect ID: " + effect.get("effect").toString());
            }
        }
        player.sendHealthUpdate();
    }


    @Override
    public String getPowerFile() {
        return "apoli:stacking_status_effect";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return stacking_status_effect;
    }
}
