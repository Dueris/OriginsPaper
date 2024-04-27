package me.dueris.genesismc.factory.powers.genesismc;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.LikeWater;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GravityPower extends CraftPower implements Listener {

    @Override
    public void run(Player p, Power power) {
        if (!LikeWater.likeWaterPlayers.contains(p)) { // Let LikeWater handle its own gravity
            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                setActive(p, power.getTag(), true);
                if (no_gravity.contains(p)) {
                    p.setGravity(false);
                    p.setFallDistance(0.1f);
                } else {
                    p.setGravity(true);
                }
            } else {
                setActive(p, power.getTag(), false);
            }
        }
    }

    @Override
    public void doesntHavePower(Player p) {
        if (!LikeWater.likeWaterPlayers.contains(p)) // Let LikeWater handle its own gravity
            p.setGravity(true);
    }

    @EventHandler
    public void serverTickEnd(PlayerToggleFlightEvent e) {
        if (creative_flight.contains(e.getPlayer()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
            || elytra.contains(e.getPlayer())) return;
        e.setCancelled(getPlayersWithPower().contains(e.getPlayer()));
    }

    @Override
    public String getType() {
        return "genesis:no_gravity";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return no_gravity;
    }

    @EventHandler
    public void shiftgodown(PlayerToggleSneakEvent e) {
        if (no_gravity.contains(e.getPlayer())) {
            if (e.getPlayer().isOnGround()) return;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getPlayer().isFlying()) return;
                    if (e.getPlayer().isSneaking()) {
                        if (e.getPlayer().getVelocity().getY() < -0.2) {
                            //nah
                        } else {
                            e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() - 0.1, e.getPlayer().getVelocity().getZ()));
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }

    }

    @EventHandler
    public void jumpyupy(PlayerJumpEvent e) {
        if (no_gravity.contains(e.getPlayer())) {
            e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() + 1, e.getPlayer().getVelocity().getZ()));
        }
    }
}
