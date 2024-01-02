package me.dueris.genesismc.factory.powers.block;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WaterBreathe extends CraftPower implements Listener {
    public static ArrayList<Player> outofAIR = new ArrayList<>();
    private static ArrayList<Player> genesisExecuting = new ArrayList<>();

    public static boolean isInBreathableWater(Player player) {
        Block block = player.getEyeLocation().getBlock();
        Material material = block.getType();
        if (block.getType().equals(Material.WATER)) {
            return true;
        } else return player.isInWater() && !material.equals(Material.AIR);
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

    @EventHandler
    public void interuptMinecraft(EntityAirChangeEvent e){
        if(e.getEntity() instanceof Player player){
            if(water_breathing.contains(player)){
                if(!genesisExecuting.contains(player)){
                    e.setCancelled(true);
                    e.setAmount(0);
                }
            }
        }
    }

    @Override
    public void run(Player p) {
        if (!getPowerArray().contains(p)) return;
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                    setActive(p, power.getTag(), true);
                    if (water_breathing.contains(p)) {
                        genesisExecuting.add(p);
                        if (isInBreathableWater(p)) {
                            if (p.getRemainingAir() < 290) {
                                p.setRemainingAir(p.getRemainingAir() + 4);
                            } else {
                                p.setRemainingAir(300);
                            }
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 3, 1, false, false, false));
                            outofAIR.remove(p);
                        } else {
                            if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                                return;
                            int remainingAir = p.getRemainingAir();
                            if (remainingAir <= 5) {
                                p.setRemainingAir(-10);
                                outofAIR.add(p);
                            } else {
                                spawnBubbleLooseParticle(p.getEyeLocation());
                                p.setRemainingAir(remainingAir - 1);
                                outofAIR.remove(p);
                            }
                        }
                        if (outofAIR.contains(p)) {
                            if (p.getRemainingAir() > 20) {
                                outofAIR.remove(p);
                            }
                        }
                        genesisExecuting.remove(p);
                    }
                } else {
                    setActive(p, power.getTag(), false);
                }
            }

        }
    }

    private void spawnBubbleLooseParticle(Location location){
        Random r = new Random();
        location.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, r.nextInt(7));
    }

    @Override
    public String getPowerFile() {
        return "origins:water_breathing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return water_breathing;
    }

    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (outofAIR.contains(p)) {
                int remainingAir = p.getRemainingAir();
                if (remainingAir <= 5) {
                    int finalDmg = 2;
                    if(p.getInventory().getHelmet() != null){
                        if(p.getInventory().getHelmet().getType() == Material.TURTLE_HELMET){
                            finalDmg = 1;
                        } else if(p.getInventory().getHelmet().containsEnchantment(Enchantment.OXYGEN)){
                            finalDmg = 1;
                        }
                    }
                    p.damage(finalDmg);
                }
            }
        }
    }
}
