package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.KeybindHandler;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.events.OriginKeybindExecuteEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
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
    public void keybindCONTINUOUSDF(KeybindTriggerEvent e){
        Player p = e.getPlayer();
        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
            if(fire_projectile.contains(p)) {
                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                    if(in_continuous.contains(p)){
                        in_continuous.remove(p);
                    }else{
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
    public void keybindPress(KeybindTriggerEvent e){
        Player p = e.getPlayer();
        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
            if(fire_projectile.contains(p)){
                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Sound sound;
                            int cooldown;
                            String tag;
                            int speed;
                            int amt;
                            int start_delay;
                            int interval;

//                            if (origin.getPowerFileFromType("origins:fire_projectile").get("sound") == null) {
//                                sound = Sound.ENTITY_EGG_THROW;
//                            } else {
//                                sound = Sound.valueOf(origin.getPowerFileFromType("origins:fire_projectile").get("sound").toUpperCase().split(":")[1].replaceAll("\\.", "_"));
//                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("cooldown") == null) {
                                cooldown = 1;
                            } else {
                                cooldown = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("cooldown"));
                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("tag") == null) {
                                tag = null;
                            } else {
                                tag = origin.getPowerFileFromType("origins:fire_projectile").get("tag");
                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("count") == null) {
                                amt = 1;
                            } else {
                                amt = parseOrDefault(origin.getPowerFileFromType("origins:fire_projectile").get("count"), 1);
                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("start_delay") == null) {
                                start_delay = 0;
                            } else {
                                start_delay = parseOrDefault(origin.getPowerFileFromType("origins:fire_projectile").get("start_delay"), 0);
                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("speed") == null) {
                                speed = 1;
                            } else {
                                speed = parseOrDefault(origin.getPowerFileFromType("origins:fire_projectile").get("speed"), 1);
                            }

                            if (origin.getPowerFileFromType("origins:fire_projectile").get("interval") == null) {
                                interval = 1;
                            } else {
                                interval = parseOrDefault(origin.getPowerFileFromType("origins:fire_projectile").get("interval"), 1);
                            }

                            EntityType type;
                            if(origin.getPowerFileFromType("origins:fire_projectile").get("entity_type").equalsIgnoreCase("origins:enderian_pearl")){
                                type = EntityType.ENDER_PEARL;
                                enderian_pearl.add(p);
                            }else{
                                type = EntityType.valueOf(origin.getPowerFileFromType("origins:fire_projectile").get("entity_type").split(":")[1].toUpperCase());
                                enderian_pearl.remove(p);
                            }

                            new BukkitRunnable() {
                                int shotsLeft = amt-amt-amt;

                                @Override
                                public void run() {

                                    if (shotsLeft >= 0) {
                                        if(origin.getPowerFileFromType("origins:fire_projectile").getKey().get("continuous").toString().equalsIgnoreCase("false")){
                                            this.cancel();
                                        }else{
                                            if (!in_continuous.contains(p)) {
                                                this.cancel();
                                            }else{
                                                shotsLeft = amt-amt-amt;
                                            }
                                        }
                                        return;
                                    }

//                                    p.playSound(p.getLocation(), 5, 1);
                                    p.setCooldown(KeybindHandler.getKeybindItem(e.getKey(), p.getInventory()).getType(), cooldown);

                                    if (type.getEntityClass() != null && Projectile.class.isAssignableFrom(type.getEntityClass())) {
                                        Projectile projectile = (Projectile) p.getWorld().spawnEntity(p.getEyeLocation(), type);
                                        projectile.setShooter(p);

                                        projectile.setVelocity((p.getEyeLocation().getDirection().multiply(speed)));

                                        projectile.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "custom_data"), PersistentDataType.STRING, tag);
//                                        p.sendMessage(String.valueOf(amt));
//                                        p.sendMessage(String.valueOf(shotsLeft));
                                    }

                                    shotsLeft++;// Decrement the remaining shots
                                }
                            }.runTaskTimer(GenesisMC.getPlugin(), start_delay, interval);

                            if(origin.getPowerFileFromType("origins:fire_projectile").getKey().get("continuous").toString().equalsIgnoreCase("false")){
                                this.cancel();
                            }else{
                                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:fire_projectile").getKey().get("key").toString(), true)) {
                                    this.cancel();
                                }
                            }
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
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
