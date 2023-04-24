package me.dueris.genesismc.core.origins.phantom;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class PhantomFormRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            if (phantomid == 2) {
                if (originid == 7300041) {
                    if (p.getLocation().add(0.9F, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(0.9F, 0, 0).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(0, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(-0.9F, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(0, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(-0.9F, 0, 0).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(0.9F, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(-0.9F, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getLocation().add(0, 0.5, 0).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||

                            p.getEyeLocation().add(0.9F, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(0.9F, 0, 0).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(0, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(-0.9F, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(0, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(-0.9F, 0, 0).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(0.9F, 0, -0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox()) ||
                            p.getEyeLocation().add(-0.9F, 0, 0.9F).getBlock().getCollisionShape().getBoundingBoxes().contains(p.getBoundingBox())
                    ) {
                                    //can form
                        p.sendMessage("block");


                      }

                    //code for if player is in "Phantom Form"
                    p.setFreezeTicks(60);
                    CraftPlayer craftPlayer = (CraftPlayer) p;
                    p.setInvulnerable(true);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.085);
                    Random random = new Random();
                    int r = random.nextInt(650);
                    if(!p.isSwimming()) {
                        if (r < 10) {
                            int foodamt = p.getFoodLevel();
                            p.setFoodLevel(foodamt - 1);
                        }
                    }
                } else {
                    p.setGameMode(p.getGameMode());
                    p.setInvulnerable(false);
                }
            }
        }
    }
}
