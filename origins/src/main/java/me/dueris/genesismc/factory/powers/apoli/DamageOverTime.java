package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class DamageOverTime extends CraftPower implements Listener {

    @EventHandler
    public void erk(PlayerDeathEvent e) {
        if (e.getDeathMessage().equals("death.attack.hurt_by_water")) {
            if (e.getPlayer().getName().equals("Optima1")) {
                e.setDeathMessage("Optima1 got too thirsty");
            } else {
                e.setDeathMessage("{p} took a bath for too long."
                    .replace("{p}", e.getPlayer().getName()));
            }
        } else if (e.getDeathMessage().equals("death.attack.no_water_for_gills")) {
            e.setDeathMessage("{p} didn't manage to keep wet"
                .replace("{p}", e.getPlayer().getName()));
        } else if (e.getDeathMessage().equals("death.attack.genericDamageOverTime")) {
            e.setDeathMessage("{p} died to a damage over time effect"
                .replace("{p}", e.getPlayer().getName()));
        } else if (e.getDeathMessage().equals("death.attack.wardenSonicBoom")) {
            e.setDeathMessage("{p} was imploded by a sonic boom"
                .replace("{p}", e.getPlayer().getName()));
        }
    }

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (!power.isPresent("interval")) {
                        throw new IllegalArgumentException("Interval must not be null! Provide an interval!! : " + power.fillStackTrace());
                    }
                    long interval = power.getNumberOrDefault("interval", 20L).getLong();
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        float damage = p.getWorld().getDifficulty().equals(Difficulty.EASY) ? power.getNumberOrDefault("damage_easy", power.getNumberOrDefault("damage", 1.0f).getFloat()).getFloat() : power.getNumberOrDefault("damage", 1.0f).getFloat();

                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            setActive(p, power.getTag(), true);

                            if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                NamespacedKey key = NamespacedKey.fromString(power.getStringOrDefault("damage_type", "generic"));
                                DamageType dmgType = Utils.DAMAGE_REGISTRY.get(CraftNamespacedKey.toMinecraft(key));
                                ((CraftPlayer) p).getHandle().hurt(Utils.getDamageSource(dmgType), damage);
                            }

                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:damage_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return damage_over_time;
    }
}