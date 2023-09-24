package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.protocol.SendStringPacketPayload;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

import static me.dueris.genesismc.entity.OriginPlayer.launchElytra;

public class FlightElytra extends CraftPower implements Listener {
    public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public FlightElytra() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    public static ArrayList<UUID> getGlidingPlayers() {
        return glidingPlayers;
    }

    @EventHandler
    @SuppressWarnings({"unchecked", "Not scheduled yet"})
    public void ExecuteFlight(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (elytra.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor executor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(power.getTag(), true);
                        if (power.getShouldRender()) {
                            SendStringPacketPayload.sendCustomPacket(p, "genesismc-elytra-render[packetID:a354b]");
                            CraftPlayer player = (CraftPlayer) p;
                            Bukkit.getServer().getGlobalRegionScheduler().execute(GenesisMC.getPlugin(), new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.getWorld().setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, false);
                                }
                            });
                        }
                        if (!p.isOnGround() && !p.isGliding()) {
                            glidingPlayers.add(p.getUniqueId());
                            if (p.getGameMode() == GameMode.SPECTATOR) return;
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

                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBoost(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (!player.isGliding()) return;

        if (action != Action.LEFT_CLICK_AIR) {
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem.isSimilar(new ItemStack(Material.FIREWORK_ROCKET)))

        launchElytra(player, 1.75F);
        if (player.getGameMode() != GameMode.CREATIVE) handItem.setAmount(handItem.getAmount() - 1);
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
