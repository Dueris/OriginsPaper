package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerUtils;
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
import java.util.HashMap;

import static me.dueris.genesismc.util.KeybindingUtils.isKeyBeingPressed;

public class Launch extends CraftPower implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();

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
        if (launch_into_air.contains(e.getPlayer())) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "apoli:launch", p, null, null, null, p.getItemInHand(), null)) {
                        if (!CooldownUtils.isPlayerInCooldown(p, power.get("key").getOrDefault("key", "key.origins.primary_active").toString())) {
                            if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        String key = (String) power.get("key").getOrDefault("key", "key.origins.primary_active");
                                        if (!CooldownUtils.isPlayerInCooldown(p, key)) {
                                            KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getTriggerFromOriginKey(p, key));
                                            final boolean[] thing = new boolean[1];
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    int cooldown = power.getIntOrDefault("cooldown", 1);
                                                    if (!CooldownUtils.isPlayerInCooldown(p, key)) {
                                                        //continousus - false
                                                        KeybindingUtils.runKeyChangeTriggerReturn(KeybindingUtils.getTriggerFromOriginKey(p, key), p, key);
                                                        ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        thing[0] = true;
                                                        setActive(p, power.getTag(), false);
                                                        this.cancel();
                                                    }

                                                    int speed = Integer.parseInt(power.getStringOrDefault("speed", null)); // used as string so that upon parsing the int it throws if not found
                                                    CooldownUtils.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown, key);
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

                                            if (power.get("key").get("continuous").toString().equalsIgnoreCase("false")) {
                                                ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                if (power == null) {
                                                    getPowerArray().remove(p);
                                                    return;
                                                }
                                                if (!getPowerArray().contains(p)) return;
                                                setActive(p, power.getTag(), false);
                                                KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                in_continuous.add(p);
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        in_continuous.remove(p);
                                                    }
                                                }.runTaskLater(GenesisMC.getPlugin(), 1L);
                                                this.cancel();
                                            } else {
                                                if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                                    ItemMeta met = KeybindingUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    if (power == null) {
                                                        getPowerArray().remove(p);
                                                        return;
                                                    }
                                                    if (!getPowerArray().contains(p)) return;
                                                    setActive(p, power.getTag(), false);
                                                    KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                    if (in_continuous.contains(p)) {
                                                        KeybindingUtils.runKeyChangeTriggerReturn(KeybindingUtils.getKeybindItem(key, p.getInventory()), p, key);
                                                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setType(Material.GRAY_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);

                                                        setActive(p, power.getTag(), false);
                                                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        in_continuous.remove(p);
                                                        this.cancel();
                                                    } else {
                                                        KeybindingUtils.runKeyChangeTrigger(KeybindingUtils.getKeybindItem(key, p.getInventory()));
                                                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setType(Material.LIME_DYE);
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);

                                                        setActive(p, power.getTag(), true);
                                                        KeybindingUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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
        return "apoli:launch";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return launch_into_air;
    }
}
