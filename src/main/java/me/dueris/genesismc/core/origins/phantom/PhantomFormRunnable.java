package me.dueris.genesismc.core.origins.phantom;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Random;


public class PhantomFormRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            
//            if(p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)){
//            p.setFlying(true);
//            }else{
//            p.setFlying(false);
//            }

            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            if (phantomid == 2) {
                if (origintag.equalsIgnoreCase("genesis:origin-phantom")) {
                    if ((p.getLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0.5, 0).getBlock().isSolid() ||

                            p.getEyeLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid())
                    ) {
                        //can form
                        if(p.isInsideVehicle()) return;
                        p.setCollidable(false);
                        p.setGameMode(GameMode.SPECTATOR);
                        p.setFlying(true);

                    }else{
                        if(p.getGameMode().equals(GameMode.SPECTATOR)){
                            if (p.getPreviousGameMode().equals(GameMode.CREATIVE)) {
                                p.setGameMode(p.getPreviousGameMode());
                                p.setFlying(true);
                            }else{
                                p.setGameMode(p.getPreviousGameMode());
                                if(p.isOnGround());
                                p.setFlying(false);
                            }

                        }
                    }

                    //code for if player is in "Phantom Form"


                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.085);
                    Random random = new Random();
                    int r = random.nextInt(650);
                    if(!p.isSwimming()) {
                        if (r < 10) {
                            if(p.getFoodLevel() > 1){
                                int foodamt = p.getFoodLevel();
                                p.setFoodLevel(foodamt - 1);
                            }
                        }
                    }
                }
            }
        }
    }
}
