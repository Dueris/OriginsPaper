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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.dueris.genesismc.core.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.core.factory.powers.Powers.fire_projectile;

public class Launch implements Listener {
    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        ArrayList<Player> peopladf = new ArrayList<>();
        if(!peopladf.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if (fire_projectile.contains(p)) {
                    if (ConditionExecutor.check("condition", p, origin, "origins:launch", null, p)) {
                        if (!CooldownStuff.isPlayerInCooldown(p, origin.getPowerFileFromType("origins:launch").getKey().get("key").toString())) {
                            if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:launch").getKey().get("key").toString(), true)) {
                                String key = origin.getPowerFileFromType("origins:launch").getKey().get("key").toString();
                                String continuous = origin.getPowerFileFromType("origins:launch").getKey().get("continuous").toString();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                            KeybindHandler.runKeyChangeTrigger(KeybindHandler.getKeybindItem(key, p.getInventory()));
                                            p.setVelocity(new Vector(p.getVelocity().getX(), Double.parseDouble(origin.getPowerFileFromType("origins:launch").get("speed", null)), p.getVelocity().getZ()));
                                        }else{
                                            if(!Boolean.parseBoolean(continuous)){
                                                KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                CooldownStuff.addCooldown(p, "origins:launch", Integer.parseInt(origin.getPowerFileFromType("origins:launch").get("cooldown", "1")), e.getKey());
                                                this.cancel();
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
    }
