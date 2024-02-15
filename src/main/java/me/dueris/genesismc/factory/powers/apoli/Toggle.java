package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.util.KeybindingUtils.isKeyBeingPressed;

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
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (GenesisMC.getConditionExecutor().check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)) {
                        if (GenesisMC.getConditionExecutor().check("entity_condition", "entity_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)) {
                            if (!CooldownUtils.isPlayerInCooldown(p, power.get("key").getOrDefault("key", "key.origins.primary_active").toString())) {
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
        KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getTriggerFromOriginKey(p, key));
        if (CooldownUtils.isPlayerInCooldown(p, key)) return;
        if (!powers_active.containsKey(p)) {
            powers_active.put(p, new HashMap());
        }
        if (powers_active.get(p).containsKey(power.getTag())) {
            setActive(p, power.getTag(), !powers_active.get(p).get(tag));
            if (power.getBooleanOrDefault("retain_state", false)) {
                if (active) {
                    //active
                    KeybindingUtils.runKeyChangeTriggerReturn(KeybindingUtils.getTriggerFromOriginKey(p, key), p, key);
                    ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                    KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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
                    KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getKeybindItem(key, p.getInventory()));
                    ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                    KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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
                KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getKeybindItem(key, p.getInventory()));
                setActive(p, tag, true);
                in_continuous.add(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        KeybindingUtils.runKeyChangeTriggerReturn(KeybindingUtils.getTriggerFromOriginKey(p, key), p, key);
                        setActive(p, tag, false);
                        ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                        in_continuous.remove(p);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2);
            }
        } else {
            //set true
            KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getKeybindItem(key, p.getInventory()));
            setActive(p, tag, true);
            in_continuous.add(p);
            new BukkitRunnable() {
                @Override
                public void run() {
                    KeybindingUtils.runKeyChangeTriggerReturn(KeybindingUtils.getTriggerFromOriginKey(p, key), p, key);
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
