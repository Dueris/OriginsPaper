package me.dueris.genesismc.factory.powers.effects;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.factory.powers.Toggle.in_continuous;

public class ToggleNightVision extends CraftPower implements Listener {
    @Override
    public void run() {
        
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (getPowerArray().contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, getPowerFile(), null, p)) {
                    if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString())) {
                        if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString(), true)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    String key = (String) origin.getPowerFileFromType(getPowerFile()).getKey().get("key");
                                    if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                        KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));

                                        final boolean[] thing = new boolean[1];
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                    if (origin.getPowerFileFromType(getPowerFile()).get("continuous", "true") == "false") {
                                                        //continousus - false
                                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        thing[0] = true;
                                                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                                        this.cancel();
                                                    } else {
                                                        //yes continuouous
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                                    }
                                                }

                                                if (in_continuous.contains(p)) {
                                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20000, 1, false, false, false));
                                                } else {
                                                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                                    this.cancel();
                                                }
                                            }
                                        }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);

                                        if (thing[0]) {
                                            thing[0] = false;
                                            this.cancel();
                                        }

                                        if (origin.getPowerFileFromType(getPowerFile()).get("continuous", "true").equalsIgnoreCase("false")) {
                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                            in_continuous.add(p);
                                            if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                            new BukkitRunnable(){
                                                @Override
                                                public void run() {
                                                    in_continuous.remove(p);
                                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                                }
                                            }.runTaskLater(GenesisMC.getPlugin(), 1L);
                                            this.cancel();
                                        } else {
                                            if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString(), true)) {
                                                ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                if (in_continuous.contains(p)) {
                                                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getKeybindItem(key, p.getInventory()), p, key);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.GRAY_DYE);
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    in_continuous.remove(p);
                                                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                                    this.cancel();
                                                } else {
                                                    KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setType(Material.LIME_DYE);
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    in_continuous.add(p);
                                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
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
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public String getPowerFile() {
        return "origins:toggle_night_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return toggle_night_vision;
    }
}
