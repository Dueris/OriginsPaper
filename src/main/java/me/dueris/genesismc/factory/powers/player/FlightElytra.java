package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.protocol.SendStringPacketPayload;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.dueris.genesismc.entity.OriginPlayerUtils.launchElytra;
import static me.dueris.genesismc.entity.OriginPlayerUtils.powerContainer;

public class FlightElytra extends CraftPower implements Listener {
    public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    public static ArrayList<UUID> getGlidingPlayers() {
        return glidingPlayers;
    }

    @EventHandler
    @SuppressWarnings({"unchecked", "Not scheduled yet"})
    public void ExecuteFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if(p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (elytra.contains(e.getPlayer())) {
            e.setCancelled(true);
            p.setFlying(false);
            ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(p, power.getTag(), true);
                        if (!p.isGliding() && !p.getLocation().add(0, 1, 0).getBlock().isCollidable()) {
                            if (p.getGameMode() == GameMode.SPECTATOR) return;
                            glidingPlayers.add(p.getUniqueId());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (p.isOnGround() || p.isFlying()) {
                                        this.cancel();
                                        glidingPlayers.remove(p.getUniqueId());
                                    }
                                    glidingPlayers.add(p.getUniqueId());
                                    p.setGliding(true);
                                    p.setFallDistance(0);
                                }
                            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
                        }
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBoost(PlayerInteractEvent event) {
        if(getPowerArray().contains(event.getPlayer())){
            Player player = event.getPlayer();
            Action action = event.getAction();
    
            if (!player.isGliding()) return;
    
            if (action != Action.RIGHT_CLICK_AIR) return;
    
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if(handItem.getType().equals(Material.FIREWORK_ROCKET)){
                launchElytra(player, 1.75F);
                if (player.getGameMode() != GameMode.CREATIVE){handItem.setAmount(handItem.getAmount() - 1);}
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
        
                int totalTicks = 10;
                long interval = 1L;
        
                new BukkitRunnable() {
                    int ticksRemaining = totalTicks;
        
                    @Override
                    public void run() {
                        if (ticksRemaining > 0) {
                            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1, 1, 1, 1);
                            ticksRemaining--;
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 0L, interval);
        
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ElytraDamageHandler(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (elytra.contains(p)) {
                if (glidingPlayers.contains(p)) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) {
                        e.setDamage(e.getDamage() * 0.25);
                    }
                }
            }
            if (more_kinetic_damage.contains(p)) {
                if (!glidingPlayers.contains(p)) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT) || e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                        e.setDamage(e.getDamage() * 1.5);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:elytra_flight";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return elytra;
    }
}
