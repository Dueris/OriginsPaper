package me.dueris.genesismc.core.factory.powers.armour;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

import static me.dueris.genesismc.core.api.entity.OriginPlayer.launchElytra;
import static me.dueris.genesismc.core.factory.powers.Powers.elytra;
import static me.dueris.genesismc.core.factory.powers.Powers.more_kinetic_damage;

public class FlightElytra implements Listener {
    public static ArrayList<UUID> glidingPlayers = new ArrayList<>();
    @EventHandler
    public void ExecuteFlight(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if (elytra.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if(!p.isOnGround()){
                glidingPlayers.add(p.getUniqueId());
                if(p.getGameMode() == GameMode.SPECTATOR) return;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(p.isOnGround() || p.isFlying()) {this.cancel(); glidingPlayers.remove(p.getUniqueId());}
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 3, false, false, false));
                        p.setGliding(true);
                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
            }
        }
    }

    @EventHandler
    public void BoostHandler(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (elytra.contains(OriginPlayer.getOriginTag(e.getPlayer())) && e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if(!glidingPlayers.contains(p.getUniqueId())) return;
            if(e.getItem() != null){
                ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET);
                if(e.getItem().isSimilar(rocket)){
                    launchElytra(p);
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                    p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void ElytraDamageHandler(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if (elytra.contains(OriginPlayer.getOriginTag(p))) {
                if(glidingPlayers.contains(p.getUniqueId())) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) {
                        e.setDamage(e.getDamage() * 0.25);
                    }
                }
            }
            if (more_kinetic_damage.contains(OriginPlayer.getOriginTag(p))) {
                if(!glidingPlayers.contains(p.getUniqueId())) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL) || e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) {
                        e.setDamage(e.getDamage() * 1.5);
                    }
                }
            }
        }
    }
}
