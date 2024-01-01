package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.KeybindUtils;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.dueris.genesismc.utils.KeybindUtils.isKeyBeingPressed;

import java.util.ArrayList;
import java.util.HashMap;

public class Launch extends CraftPower implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (launch_into_air.contains(e.getPlayer())) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:launch", p, null, null, null, p.getItemInHand(), null)) {
                        if (!CooldownManager.isPlayerInCooldown(p, power.getKey().getOrDefault("key", "key.origins.primary_active").toString())) {
                            if (isKeyBeingPressed(e.getPlayer(), power.getKey().getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        String key = (String) power.getKey().getOrDefault("key", "key.origins.primary_active");
                                        if (!CooldownManager.isPlayerInCooldown(p, key)) {
                                            KeybindUtils.runKeyChangeTrigger(KeybindUtils.getTriggerFromOriginKey(p, key));
                                            final boolean[] thing = new boolean[1];
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    int cooldown = Integer.parseInt(power.get("cooldown", "1"));
                                                    if (!CooldownManager.isPlayerInCooldown(p, key)) {
                                                        if (power.getKey().get("continuous").toString() == "false") {
                                                            //continousus - false
                                                            KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                            thing[0] = true;
                                                            setActive(p, power.getTag(), false);
                                                            this.cancel();
                                                        } else {
                                                            //yes continuouous
                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        }
                                                    }
                                                    //run code here for things that happen when toggled
                                                    //power is active
                                                    //dont change any other settings in this other than the powertype and the "retain_state"
                                                    int speed = Integer.parseInt(power.get("speed", null));
                                                    CooldownManager.addCooldown(p, power.getTag(), power.getType(), cooldown, key);
                                                    setActive(p, power.getTag(), true);
                                                    p.setVelocity(new Vector(p.getVelocity().getX(), speed, p.getVelocity().getZ()));
                                                    p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
                                                    this.cancel();
                                                }
                                            }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);

                                            if (thing[0]) {
                                                thing[0] = false;
                                                this.cancel();
                                            }

                                            if (power.getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                if (power == null) {
                                                    getPowerArray().remove(p);
                                                    return;
                                                }
                                                if (!getPowerArray().contains(p)) return;
                                                setActive(p, power.getTag(), false);
                                                KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                in_continuous.add(p);
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        in_continuous.remove(p);
                                                    }
                                                }.runTaskLater(GenesisMC.getPlugin(), 1L);
                                                this.cancel();
                                            } else {
                                                if (isKeyBeingPressed(e.getPlayer(), power.getKey().getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                                    ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    if (power == null) {
                                                        getPowerArray().remove(p);
                                                        return;
                                                    }
                                                    if (!getPowerArray().contains(p)) return;
                                                    setActive(p, power.getTag(), false);
                                                    KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    if (in_continuous.contains(p)) {
                                                        KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getKeybindItem(key, p.getInventory()), p, key);
                                                        KeybindUtils.getKeybindItem(key, p.getInventory()).setType(Material.GRAY_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);

                                                        setActive(p, power.getTag(), false);
                                                        KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        in_continuous.remove(p);
                                                        this.cancel();
                                                    } else {
                                                        KeybindUtils.runKeyChangeTrigger(KeybindUtils.getKeybindItem(key, p.getInventory()));
                                                        KeybindUtils.getKeybindItem(key, p.getInventory()).setType(Material.LIME_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);

                                                        setActive(p, power.getTag(), true);
                                                        KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        in_continuous.add(p);
                                                    }
                                                    this.cancel();
                                                }
                                            }
                                        }
                                    }
                                }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
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
        return "origins:launch";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return launch_into_air;
    }
}
