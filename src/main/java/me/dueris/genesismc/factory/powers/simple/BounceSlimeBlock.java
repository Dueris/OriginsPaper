package me.dueris.genesismc.factory.powers.simple;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.events.PlayerHitGroundEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;

import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BounceSlimeBlock extends CraftPower implements OriginSimple, Listener {
    @Override
    public void run(Player p) {

    }

    public static ArrayList<Player> bouncePlayers = new ArrayList<>();
    public static HashMap<Player, Location> lastLoc = new HashMap<>();
    public static ArrayList<Player> getBouncePlayers() {
        return bouncePlayers;
    }

    @EventHandler
    public void gameEvent(GenericGameEvent event){
        if(event.getEvent().equals(GameEvent.HIT_GROUND)){
            if(event.getEntity() instanceof Player player) {
                PlayerHitGroundEvent playerHitGroundEvent = new PlayerHitGroundEvent(player);
                Bukkit.getPluginManager().callEvent(playerHitGroundEvent);
                if (player.isSneaking()) return;
                if (!bouncePlayers.contains(player) && !lastLoc.containsKey(player)) return;
                Location lastLocation = lastLoc.get(player);

                if(lastLocation.getY() > player.getY()){
                    double coefficientOfRestitution = 0.45;
                    double reboundVelocity = -coefficientOfRestitution * -(lastLocation.getY() - player.getY());
                    if(reboundVelocity <= 0.2) return;

                    if (!player.isOnGround() || player.isJumping() || player.isSprinting()) return;
                    player.setVelocity(new Vector(player.getVelocity().getX(), reboundVelocity, player.getVelocity().getZ()));
                }
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e){
        if(!e.isCancelled()){
            if(!bouncePlayers.contains(e.getPlayer())) return;
            if(e.getPlayer().isOnGround()) return;
            lastLoc.put(e.getPlayer(), e.getFrom());
        }
    }

    @EventHandler
    public void event(OriginChangeEvent e) {
        boolean hasPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(e.getPlayer()).values()) {
            for (String power : origin.getPowers()) {
                if (power.equals("origins:slime_block_bounce")) {
                    hasPower = true;
                    break;
                }
            }
        }

        if (hasPower && !bouncePlayers.contains(e.getPlayer())) {
            bouncePlayers.add(e.getPlayer());
        } else if (!hasPower) {
            bouncePlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerJoinEvent e) {
        boolean hasPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(e.getPlayer()).values()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                if (power.getTag().equals("origins:slime_block_bounce")) {
                    hasPower = true;
                    break;
                }
            }
        }

        if (hasPower && !bouncePlayers.contains(e.getPlayer())) {
            bouncePlayers.add(e.getPlayer());
        } else if (!hasPower) {
            bouncePlayers.remove(e.getPlayer());
        }
    }

    @Override
    public String getPowerFile() {
        return "genesis:bouncing_slime_fsd3sd;jf[@dueris]";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return bouncePlayers;
    }

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
    public String getSimpleTagID() {
        return "origins:slime_block_bounce";
    }
}
