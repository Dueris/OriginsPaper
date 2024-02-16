package me.dueris.genesismc.factory.powers.apoli;

import com.mojang.brigadier.StringReader;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.particles.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ParticlePower extends CraftPower {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player player) {
        if (particle.contains(player)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (power == null) {
                        getPowerArray().remove(player);
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", player, power, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
                            if (!getPowerArray().contains(player)) return;
                            Particle particle = computeParticleArgs(power.getObject("particle"));
                            if(particle == null) throw new IllegalStateException("Unable to create CraftBukkit particle instance");
                            boolean visible_while_invis = power.getBooleanOrDefault("visible_while_invisible", false);
                            boolean pass = visible_while_invis ? true : !player.isInvisible();
                            setActive(player, power.getTag(), pass);

                            float offset_x = 0.25f;
                            float offset_y = 0.50f;
                            float offset_z = 0.25f;

                            if (power.get("spread").get("y") != null) {
                                offset_y = Float.parseFloat(String.valueOf(power.get("spread").get("y")));
                            }
                            if (power.get("spread").get("x") != null) {
                                offset_x = Float.parseFloat(String.valueOf(power.get("spread").get("x")));
                            }
                            if (power.get("spread").get("z") != null) {
                                offset_z = Float.parseFloat(String.valueOf(power.get("spread").get("z")));
                            }

                            Particle.DustOptions data = null;
                            if(containsParams(power)){
                                String provided = power.get("particle").getOrDefault("params", "").toString();
                                if(provided.contains(" ")){
                                    String[] splitArgs = provided.split(" ");
                                    float arg1 = Float.valueOf(splitArgs[0]);
                                    float arg2 = Float.valueOf(splitArgs[1]);
                                    float arg3 = Float.valueOf(splitArgs[2]);
                                    float size = Float.valueOf(splitArgs[3]);
                                    data = new Particle.DustOptions(Color.fromRGB(calculateValue(arg1), calculateValue(arg2), calculateValue(arg3)), size);
                                }
                            }

                            if(pass){
                                player.getWorld().spawnParticle(
                                        particle.builder().source(player).force(false).location(player.getLocation()).count(1).particle(),
                                        new Location(player.getWorld(), player.getEyeLocation().getX(), player.getEyeLocation().getY() - 0.7, player.getEyeLocation().getZ()),
                                        1, offset_x, offset_y, offset_z, 0, data);
//                                run(power, ((CraftPlayer)player).getHandle(), particle);
                            }
                        } else {
                            if (!getPowerArray().contains(player)) return;
                            setActive(player, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    public static boolean containsParams(PowerContainer power){
        return power.getObject("particle") instanceof String ? false : power.getObject("particle") instanceof JSONObject ? power.get("particle").containsKey("params") : false;
    }

    private static int calculateValue(float value){
        if(Math.round(value * 255) > 255){
            return 254;
        }else{
            return Math.round(value * 255);
        }
    }

    public static Particle computeParticleArgs(Object root){
        if(root instanceof String particleSt){
            return Particle.valueOf(ensureCorrectNamespace(particleSt).split(":")[1].toUpperCase());
        } else if (root instanceof JSONObject particle) {
            return Particle.valueOf(ensureCorrectNamespace(particle.get("type").toString()).split(":")[1].toUpperCase());
        }
        return null;
    }

    private static String ensureCorrectNamespace(String string){
        if(string.endsWith("dust")){
            string = string.replace("dust", "redstone");
        }
        return string.contains(":") ? string : "minecraft:" + string;
    }

    @Override
    public String getPowerFile() {
        return "apoli:particle";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return particle;
    }
}
