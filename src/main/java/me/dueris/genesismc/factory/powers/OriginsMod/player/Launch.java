package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.KeybindHandler;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    
    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (launch_into_air.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:launch", null, p)) {
                    if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType("origins:launch").getKey().get("key").toString())) {
                        if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:launch").getKey().get("key").toString(), true)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    String key = (String) origin.getPowerFileFromType("origins:launch").getKey().get("key");
                                    if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                        KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));

                                        final boolean[] thing = new boolean[1];
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                int cooldown = Integer.parseInt(origin.getPowerFileFromType("origins:launch").get("cooldown", "1"));
                                                if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                    if (origin.getPowerFileFromType("origins:launch").getKey().get("continuous").toString() == "false") {
                                                        //continousus - false
                                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        thing[0] = true;
                                                        this.cancel();
                                                    } else {
                                                        //yes continuouous
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    }
                                                }
                                                if (in_continuous.contains(p)) {
                                                    //run code here for things that happen when toggled
                                                    //power is active
                                                    //dont change any other settings in this other than the powertype and the "retain_state"
                                                    int speed = Integer.parseInt(origin.getPowerFileFromType("origins:launch").get("speed", null));
                                                    CooldownStuff.addCooldown(p, "origins:launch", cooldown, key);
                                                    p.setVelocity(new Vector(p.getVelocity().getX(), p.getVelocity().getY() + speed, p.getVelocity().getZ()));
                                                } else {
                                                    this.cancel();
                                                }
                                            }
                                        }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);

                                        if (thing[0]) {
                                            thing[0] = false;
                                            this.cancel();
                                        }

                                        if (origin.getPowerFileFromType("origins:launch").getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                            in_continuous.add(p);
                                            new BukkitRunnable(){
                                                @Override
                                                public void run() {
                                                    in_continuous.remove(p);
                                                }
                                            }.runTaskLater(GenesisMC.getPlugin(),1l);
                                            this.cancel();
                                        } else {
                                            if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:launch").getKey().get("key").toString(), true)) {
                                                ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                if (in_continuous.contains(p)) {
                                                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getKeybindItem(key, p.getInventory()), p, key);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.GRAY_DYE);
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    in_continuous.remove(p);
                                                    this.cancel();
                                                } else {
                                                    KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.LIME_DYE);
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
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

    @Override
    public void run() {

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
