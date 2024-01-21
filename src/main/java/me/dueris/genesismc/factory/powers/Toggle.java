package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.utils.KeybindUtils;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.utils.KeybindUtils.isKeyBeingPressed;

public class Toggle extends CraftPower implements Listener {
    public static ArrayList<Player> in_continuous = new ArrayList<>();
    public boolean active = true;
    public boolean runCancel = false;

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

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (toggle_power.contains(e.getPlayer())) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (GenesisMC.getConditionExecutor().check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)) {
                        if (GenesisMC.getConditionExecutor().check("entity_condition", "entity_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)) {
                            if (!CooldownManager.isPlayerInCooldown(p, power.get("key").getOrDefault("key", "key.origins.primary_active").toString())) {
                                if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                    execute(p, power);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void execute(Player p, PowerContainer power) {
        if (!getPowerArray().contains(p)) return;
        if (runCancel) return;
        String tag = power.getTag();
        String key = (String) power.get("key").getOrDefault("key", "key.origins.primary_active");
        KeybindUtils.runKeyChangeTrigger(KeybindUtils.getTriggerFromOriginKey(p, key));
        if (CooldownManager.isPlayerInCooldown(p, key)) return;
        if (!powers_active.containsKey(p)) {
            powers_active.put(p, new HashMap());
        }
        if (powers_active.get(p).containsKey(power.getTag())) {
            setActive(p, power.getTag(), !powers_active.get(p).get(tag));
            if (power.getBooleanOrDefault("retain_state", false) == true) {
                if (active) {
                    //active
                    KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                    ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                    KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                    setActive(p, tag, false);
                    in_continuous.remove(p);
                    active = false;
                    runCancel = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            runCancel = false;
                            this.cancel();
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 5);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            //run code for while its disabled
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                } else {
                    //nonactive
                    KeybindUtils.runKeyChangeTrigger(KeybindUtils.getKeybindItem(key, p.getInventory()));
                    ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                    KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                    setActive(p, tag, true);
                    in_continuous.add(p);
                    active = true;
                    runCancel = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            runCancel = false;
                            this.cancel();
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 5);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            //run code for while its enabled
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                }
//                ToggleTriggerEvent toggleTriggerEvent = new ToggleTriggerEvent(p, key, origin, !active);
//                Bukkit.getServer().getPluginManager().callEvent(toggleTriggerEvent);
            } else {
                KeybindUtils.runKeyChangeTrigger(KeybindUtils.getKeybindItem(key, p.getInventory()));
                setActive(p, tag, true);
                in_continuous.add(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                        setActive(p, tag, false);
                        ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                        KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                        in_continuous.remove(p);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2);
            }
        } else {
            //set true
            KeybindUtils.runKeyChangeTrigger(KeybindUtils.getKeybindItem(key, p.getInventory()));
            setActive(p, tag, true);
            in_continuous.add(p);
            new BukkitRunnable() {
                @Override
                public void run() {
                    KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                    setActive(p, tag, false);
                    in_continuous.remove(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 2);
        }
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
