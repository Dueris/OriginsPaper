package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import me.dueris.genesismc.core.CooldownStuff;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.KeybindHandler;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.dueris.genesismc.core.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.core.factory.powers.Powers.fire_projectile;

public class FireProjectile implements Listener {
    /*
    {
    "type": "origins:fire_projectile",
    "entity_type": "minecraft:arrow",
    "cooldown": 2,
    "hud_render": {
        "should_render": false
    },
    "tag": "{pickup:0b}",
    "key": {
        "key": "key.attack",
        "continuous": true
    }
}
     */
    public static ArrayList<Player> in_continuous = new ArrayList<>();

    @EventHandler
    public void keybindCONTINUOUSDF(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (fire_projectile.contains(p)) {
                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                    if (in_continuous.contains(p)) {
                        in_continuous.remove(p);
                    } else {
                        in_continuous.add(p);
                    }
                }
            }
        }
    }

    public static ArrayList<Player> enderian_pearl = new ArrayList<>();

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (enderian_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            p.teleport(e.getTo());
        }

    }

    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        ArrayList<Player> peopladf = new ArrayList<>();
        if (!peopladf.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if (fire_projectile.contains(p)) {
                    if (ConditionExecutor.check("condition", p, origin, "origins:fire_projectile", null, p)) {
                        if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString())) {
                            if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Sound sound;
                                        int cooldown = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("cooldown", "1"));
                                        String tag = origin.getPowerFileFromType("origins:fire_projectile").get("tag", null);
                                        float divergence = Float.parseFloat(origin.getPowerFileFromType("origins:fire_projectile").get("divergence", "1.0"));
                                        int speed = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("speed", "1"));
                                        int amt = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("count", "1"));
                                        int start_delay = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("start_delay", "0"));
                                        int interval = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("interval", "1"));

                                        // Introduce a slight random divergence
                                        divergence += (float) ((Math.random() - 0.5) * 0.05); // Adjust the 0.05 value to control the randomness level

                                        EntityType type;
                                        if (origin.getPowerFileFromType("origins:fire_projectile").get("entity_type", null).equalsIgnoreCase("origins:enderian_pearl")) {
                                            type = EntityType.ENDER_PEARL;
                                            enderian_pearl.add(p);
                                        } else {
                                            type = EntityType.valueOf(origin.getPowerFileFromType("origins:fire_projectile").get("entity_type", null).split(":")[1].toUpperCase());
                                            enderian_pearl.remove(p);
                                        }

                                        String key = (String) origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key");
                                        if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                            KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));

                                            float finalDivergence = divergence;
                                            float finalDivergence1 = divergence;
                                            final boolean[] thing = new boolean[1];
                                            new BukkitRunnable() {
                                                int shotsLeft = -amt;

                                                @Override
                                                public void run() {
                                                    if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                        if (shotsLeft >= 0) {
                                                            if (origin.getPowerFileFromType("origins:fire_projectile").getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                                KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                                CooldownStuff.addCooldown(p, "origins:fire_projectile", cooldown, key);
                                                                peopladf.remove(p);
                                                                ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                shotsLeft = 0;
                                                                thing[0] = true;
                                                                this.cancel();
                                                            } else {
                                                                if (!in_continuous.contains(p)) {
                                                                    KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                                    CooldownStuff.addCooldown(p, "origins:fire_projectile", cooldown, key);
                                                                    peopladf.remove(p);
                                                                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                    shotsLeft = 0;
                                                                    thing[0] = true;
                                                                    this.cancel();
                                                                } else {
                                                                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                    shotsLeft = -amt;
                                                                }
                                                            }
                                                            return;
                                                        }

                                                        p.setCooldown(KeybindHandler.getKeybindItem(e.getKey(), p.getInventory()).getType(), cooldown);

                                                        if (type.getEntityClass() != null && Projectile.class.isAssignableFrom(type.getEntityClass())) {
                                                            Projectile projectile = (Projectile) p.getWorld().spawnEntity(p.getEyeLocation(), type);
                                                            projectile.setShooter(p);

                                                            Vector direction = p.getEyeLocation().getDirection();

                                                            double yawRadians = Math.toRadians(p.getEyeLocation().getYaw() + finalDivergence1);

                                                            double x = -Math.sin(yawRadians) * Math.cos(Math.toRadians(p.getLocation().getPitch()));
                                                            double y = -Math.sin(Math.toRadians(p.getLocation().getPitch()));
                                                            double z = Math.cos(yawRadians) * Math.cos(Math.toRadians(p.getLocation().getPitch()));

                                                            direction.setX(x);
                                                            direction.setY(y);
                                                            direction.setZ(z);

                                                            projectile.setVelocity(direction.normalize().multiply(speed));

                                                            peopladf.add(p);

                                                        }

                                                        shotsLeft++; // Decrement the remaining shots
                                                    }
                                                }
                                            }.runTaskTimer(GenesisMC.getPlugin(), start_delay, interval);

                                            if (thing[0]) {
                                                thing[0] = false;
                                                this.cancel();
                                            }

                                            if (origin.getPowerFileFromType("origins:fire_projectile").getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                this.cancel();
                                            } else {
                                                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                                                    ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                    met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                    KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
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

    public static int parseOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    //TODO: make the kye thinger and all the executorss
}
