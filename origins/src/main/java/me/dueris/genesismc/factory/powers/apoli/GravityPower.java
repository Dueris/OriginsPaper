package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.LikeWater;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
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
    public void run(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (no_gravity.contains(p)) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
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
            } else {
                if (LikeWater.likeWaterPlayers.contains(p)) {
                    p.setGravity(!(!p.isSwimming() && p.getEyeLocation().getBlock().getType().equals(Material.WATER) && p.getLocation().getBlock().getType().equals(Material.WATER) && p.isInWaterOrBubbleColumn()));
                } else {
                    p.setGravity(true);
                }
            }
        }
    }

    @EventHandler
    public void serverTickEnd(PlayerToggleFlightEvent e) {
        if (creative_flight.contains(e.getPlayer()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
            || elytra.contains(e.getPlayer())) return;
        e.setCancelled(getPowerArray().contains(e.getPlayer()));
    }

    @Override
    public String getPowerFile() {
        return "genesis:no_gravity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
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
