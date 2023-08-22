package me.dueris.genesismc.core.factory.powers.OriginsMod;

import me.dueris.genesismc.core.CooldownStuff;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.KeybindHandler;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.core.factory.powers.Powers.toggle_power;

public class Toggle implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (toggle_power.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:toggle", null, p)) {
                    if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType("origins:toggle").getKey().get("key").toString())) {
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
                                                    if (origin.getPowerFileFromType("origins:toggle").get("retain_state", "false") == "false") {
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
                                                } else {
                                                    this.cancel();
                                                }
                                            }
                                        }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);

                                        if (thing[0]) {
                                            thing[0] = false;
                                            this.cancel();
                                        }

                                        if (origin.getPowerFileFromType("origins:toggle").get("retain_state", "false").equalsIgnoreCase("false")) {
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
                                            if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:toggle").getKey().get("key").toString(), true)) {
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
}
