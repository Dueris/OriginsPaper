package me.dueris.genesismc.core.factory.powers.OriginsMod;

import me.dueris.genesismc.core.CooldownStuff;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.KeybindHandler;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.dueris.genesismc.core.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.core.factory.powers.Powers.toggle_power;

public class Toggle implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e){
        Player p = e.getPlayer();
        ArrayList<Player> peopladf = new ArrayList<>();
        if(!peopladf.contains(p)) {
            if (toggle_power.contains(e.getPlayer())) {
                for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                    if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:toggle").getKey().get("key").toString(), true)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                String key = (String) origin.getPowerFileFromType("origins:toggle").getKey().get("key");
                                if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                    KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));

                                    final boolean[] thing = new boolean[1];
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                if (origin.getPowerFileFromType("origins:toggle").getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                    peopladf.remove(p);
                                                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    thing[0] = true;
                                                    this.cancel();
                                                } else {
                                                    if (!in_continuous.contains(p)) {
                                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                        peopladf.remove(p);
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        thing[0] = true;
                                                        this.cancel();
                                                    } else {
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    }
                                                }
                                                return;
                                            }
                                        }
                                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

                                    if (thing[0] == true) {
                                        thing[0] = false;
                                        this.cancel();
                                    }

                                    if (origin.getPowerFileFromType("origins:toggle").getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                        this.cancel();
                                    } else {
                                        if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:toggle").getKey().get("key").toString(), true)) {
                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                            this.cancel();
                                        }
                                    }
                                }
                            }
                        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

//                        if (in_continuous.contains(p)) {
//                            in_continuous.remove(p);
//                        } else {
//                            in_continuous.add(p);
//                        }
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                String key = origin.getPowerFileFromType("origins:toggle").getKey().get("key").toString();
//                                KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(e.getPlayer(), key));
////                                new BukkitRunnable(){
////                                    @Override
////                                    public void run() {
//                                if (origin.getPowerFileFromType("origins:toggle").get("retain_state", "true").toString().equalsIgnoreCase("false")) {
//                                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
//                                    this.cancel();
//                                } else {
//                                    if (!in_continuous.contains(e.getPlayer())) {
//                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
//                                        this.cancel();
//                                    }
//                                }
////                                    }
////                                }.runTaskLater(GenesisMC.getPlugin(), 1);
//                            }
//                        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                    }
                }
            }
        }
    }
}
