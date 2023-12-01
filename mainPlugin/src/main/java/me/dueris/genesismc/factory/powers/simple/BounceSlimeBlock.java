package me.dueris.genesismc.factory.powers.simple;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BounceSlimeBlock extends CraftPower implements OriginSimple, Listener {
    @Override
    public void run(Player p) {

    }

    public static ArrayList<Player> bouncePlayers = new ArrayList<>();

    public static ArrayList<Player> getBouncePlayers() {
        return bouncePlayers;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!bouncePlayers.contains(player)) return;

        double velocityY = player.getVelocity().getY();

        if (velocityY < -0.4) {
            double coefficientOfRestitution = 0.85;
            double reboundVelocity = -coefficientOfRestitution * velocityY;

            if (!player.isOnGround()) return;
            player.setVelocity(new Vector(0, reboundVelocity, 0));
        }
    }

    @EventHandler
    public void event(OriginChangeEvent e) {
        boolean hasPower = false;

        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
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

        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public String getSimpleTagID() {
        return "origins:slime_block_bounce";
    }
}
