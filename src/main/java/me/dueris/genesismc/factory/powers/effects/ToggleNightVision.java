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
    Player p;

    public ToggleNightVision(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    public void execute(Player p, OriginContainer origin) {
        if (!getPowerArray().contains(p)) return;
        if (runCancel) return;
        String tag = origin.getPowerFileFromType(getPowerFile()).getTag();
        String key = (String) origin.getPowerFileFromType(getPowerFile()).getKey().get("key");
        KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));
        if (CooldownStuff.isPlayerInCooldown(p, key)) return;
        if (powers_active.containsKey(origin.getPowerFileFromType(getPowerFile()).getTag())) {
            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), !powers_active.get(tag));
            if (true) {
                if (active) {
                    //active
                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                    setActive(tag, false);
                    in_continuous.remove(p);
                    active = false;
                    runCancel = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            runCancel = false;
                            this.cancel();
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(),0, 5);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            //run code for while its disabled
                            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                } else {
                    //nonactive
                    KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                    setActive(tag, true);
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
                            ConditionExecutor conditionExecutor = new ConditionExecutor();
                            if (conditionExecutor.check("condition", "conditions", p, origin, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false, false));
                            }else{
                                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            }
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                }
//                ToggleTriggerEvent toggleTriggerEvent = new ToggleTriggerEvent(p, key, origin, !active);
//                Bukkit.getServer().getPluginManager().callEvent(toggleTriggerEvent);
            } else {
                KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                setActive(tag, true);
                in_continuous.add(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                        setActive(tag, false);
                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                        in_continuous.remove(p);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2);
            }
        } else {
            //set true
            KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
            setActive(tag, true);
            in_continuous.add(p);
            new BukkitRunnable() {
                @Override
                public void run() {
                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                    setActive(tag, false);
                    in_continuous.remove(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 2);
        }
    }

    @EventHandler
    public void keybindToggle(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (getPowerArray().contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                    if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString())) {
                        if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString(), true)) {
                            execute(p, origin);
                        }
                    }
                }else{
                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getKeybindItem(origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString(), p.getInventory()), p, origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString());
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    public static ArrayList<Player> in_continuous = new ArrayList<>();
    public boolean active = true;
    public boolean runCancel = false;

    @Override
    public String getPowerFile() {
        return "origins:toggle_night_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return toggle_night_vision;
    }
}
