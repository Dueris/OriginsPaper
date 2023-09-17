package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.KeybindHandler;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
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

import java.util.ArrayList;

import static me.dueris.genesismc.KeybindHandler.isKeyBeingPressed;

public class Launch extends CraftPower implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (launch_into_air.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:launch", p, null, null, null, p.getItemInHand(), null)) {
                        if (!CooldownStuff.isPlayerInCooldown(p, power.getKey().getOrDefault("key", "key.origins.primary_active").toString())) {
                            if (isKeyBeingPressed(e.getPlayer(), power.getKey().getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        String key = (String) power.getKey().getOrDefault("key", "key.origins.primary_active");
                                        if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                            KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));
                                            final boolean[] thing = new boolean[1];
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    int cooldown = Integer.parseInt(power.get("cooldown", "1"));
                                                    if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                        if (power.getKey().get("continuous").toString() == "false") {
                                                            //continousus - false
                                                            KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                            thing[0] = true;
                                                            setActive(power.getTag(), false);
                                                            this.cancel();
                                                        } else {
                                                            //yes continuouous
                                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        }
                                                    }
                                                    //run code here for things that happen when toggled
                                                    //power is active
                                                    //dont change any other settings in this other than the powertype and the "retain_state"
                                                    int speed = Integer.parseInt(power.get("speed", null));
                                                    CooldownStuff.addCooldown(p, origin, power.getTag(), power.getType(), cooldown, key);
                                                    setActive(power.getTag(), true);
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
                                                ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                if (power == null) {
                                                    getPowerArray().remove(p);
                                                    return;
                                                }
                                                if (!getPowerArray().contains(p)) return;
                                                setActive(power.getTag(), false);
                                                KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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
                                                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    if (power == null) {
                                                        getPowerArray().remove(p);
                                                        return;
                                                    }
                                                    if (!getPowerArray().contains(p)) return;
                                                    setActive(power.getTag(), false);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    if (in_continuous.contains(p)) {
                                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getKeybindItem(key, p.getInventory()), p, key);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.GRAY_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        if (power == null) {
                                                            getPowerArray().remove(p);
                                                            return;
                                                        }
                                                        if (!getPowerArray().contains(p)) return;
                                                        setActive(power.getTag(), false);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        in_continuous.remove(p);
                                                        this.cancel();
                                                    } else {
                                                        KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.LIME_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                        if (power == null) {
                                                            getPowerArray().remove(p);
                                                            return;
                                                        }
                                                        if (!getPowerArray().contains(p)) return;
                                                        setActive(power.getTag(), true);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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

    Player p;

    public Launch() {
        this.p = p;
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
