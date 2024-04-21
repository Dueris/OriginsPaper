package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ScareCreepers extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> scaryPlayers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("scare_creepers");
    private final NamespacedKey hitByPlayerKey = new NamespacedKey(GenesisMC.getPlugin(), "hit-by-player");

    @Override
    public String getType() {
        return null;
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return scaryPlayers;
    }


    @EventHandler
    public void load(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            applyPatch(creeper);
        }
    }

    @EventHandler
    public void load(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Creeper creeper) {
                applyPatch(creeper);
            }
        }
    }

    public void applyPatch(Creeper creeper) {
        Bukkit.getMobGoals().addGoal(creeper, 0, new AvoidEntityGoal<>(
            (PathfinderMob) ((CraftEntity) creeper).getHandle(), net.minecraft.world.entity.player.Player.class, 6, 1, 1.2,
            livingEntity -> {
                if (livingEntity.getBukkitEntity() instanceof Player player) {
                    if (getPlayersWithPower().contains(player)) {
                        String data = creeper.getPersistentDataContainer().get(hitByPlayerKey, PersistentDataType.STRING);
                        if (data == null) {
                            return true;
                        }
                        return !data.equals(player.getName());
                    }
                }
                return false;
            }
        ).asPaperVanillaGoal());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.CREEPER)) {
            Player player;
            if (event.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof org.bukkit.entity.Player shooter) player = shooter;
                else return;
            } else if (event.getDamager() instanceof org.bukkit.entity.Player damager) {
                player = damager;
            } else {
                return;
            }
            event.getEntity().getPersistentDataContainer().set(hitByPlayerKey, PersistentDataType.STRING, player.getName());
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.CREEPER)) {
            if (event.getTarget() instanceof Player player) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        String data = event.getEntity().getPersistentDataContainer().get(hitByPlayerKey, PersistentDataType.STRING);
                        if (data == null) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!data.equals(player.getName())) {
                            event.setCancelled(true);
                        }
                    }
                }.runTask(GenesisMC.getPlugin());
            }
        }
    }
}
