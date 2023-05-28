package me.dueris.genesismc.core.factory.powers.block.fluid;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.water_breathing;

public class WaterBreathe extends BukkitRunnable {
    public static ArrayList<Player> outofAIR = new ArrayList<>();
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(water_breathing.contains(OriginPlayer.getOriginTag(p))) {
                if(isInBreathableWater(p) || p.isInWaterOrBubbleColumn()){
                    p.setRemainingAir(300);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 3, 1, false, false, false));
                    if(outofAIR.contains(p)){
                        outofAIR.remove(p);
                    }
                }else{
                    int remainingAir = p.getRemainingAir();
                    if (remainingAir <= 5) {
                        p.setRemainingAir(0);
                        outofAIR.add(p);
                    } else {
                        p.setRemainingAir(remainingAir - 5);

                        if(outofAIR.contains(p)){
                            outofAIR.remove(p);
                        }
                    }
                }
                if(outofAIR.contains(p)){
                    if(p.getRemainingAir() > 20){
                        outofAIR.remove(p);
                    }
                }
            }
        }

    }

    public static boolean isInBreathableWater(Player player) {
        Block block = player.getEyeLocation().getBlock();
        Material material = block.getType();
        if(material == Material.WATER || block.getBlockData() instanceof Waterlogged || block instanceof Waterlogged || block.getState() instanceof Waterlogged){
            return true;
        }
        return false;
    }

}
