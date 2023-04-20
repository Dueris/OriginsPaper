package me.dueris.genesismc.core.origins.phantom;

import me.dueris.genesismc.core.GenesisMC;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static org.bukkit.Material.*;

public class PhantomFormRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            if (phantomid == 2) {
                if (originid == 7300041) {
                    if (p.getLocation().add(0.6F, 0, 0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(0.6F, 0, 0).getBlock().getType().isSolid() ||
                            p.getLocation().add(0, 0, 0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(-0.6F, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(0, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(-0.6F, 0, 0).getBlock().getType().isSolid() ||
                            p.getLocation().add(0.6F, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(-0.6F, 0, 0.6F).getBlock().getType().isSolid() ||
                            p.getLocation().add(0, 0.5, 0).getBlock().getType().isSolid() ||

                            p.getEyeLocation().add(0.6F, 0, 0.6F).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(0.6F, 0, 0).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(0, 0, 0.6F).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(-0.6F, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(0, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(-0.6F, 0, 0).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(0.6F, 0, -0.6F).getBlock().getType().isSolid() ||
                            p.getEyeLocation().add(-0.6F, 0, 0.6F).getBlock().getType().isSolid()


                    ) {
                        if (!p.getLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||

                                !p.getEyeLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("OBSIDIAN") ||
                                !p.getEyeLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("OBSIDIAN")


                        ) {
                            if (!p.getLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("BED") ||

                                    !p.getEyeLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("BED") ||
                                    !p.getEyeLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("BED")

                            ) {

                                if((
                                        (!p.getLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("CHEST") ||

                                                !p.getEyeLocation().add(0.6F, 0, 0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(0.6F, 0, 0).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(0, 0, 0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(-0.6F, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(0, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(-0.6F, 0, 0).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(0.6F, 0, -0.6F).getBlock().getType().toString().contains("CHEST") ||
                                                !p.getEyeLocation().add(-0.6F, 0, 0.6F).getBlock().getType().toString().contains("CHEST")))

                                ){
                                    //can form
                                    p.setGameMode(GameMode.SPECTATOR);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 7, 255, false, false, false));
                                    CraftPlayer player = (CraftPlayer) p;
                                    ServerPlayer serverPlayer = player.getHandle();

                                } else {
                                    if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                                        p.setGameMode(p.getPreviousGameMode());
                                        p.setInvulnerable(false);
                                    }

                                }


                            } else {
                                if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                                    p.setGameMode(p.getPreviousGameMode());
                                    p.setInvulnerable(false);
                                }

                            }


                        } else {
                            if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                                p.setGameMode(p.getPreviousGameMode());
                                p.setInvulnerable(false);
                            }
                        }

                    } else {
                        if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                            p.setGameMode(p.getPreviousGameMode());
                            p.setInvulnerable(false);
                        }
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
