package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (power == null) {
                        getPowerArray().remove(player);
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", player, power, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
                            if (!getPowerArray().contains(player)) return;
                            setActive(player, power.getTag(), true);
                            Particle particle = computeParticleArgs(power.getObject("particle"));
                            if(particle == null) throw new IllegalStateException("Unable to create CraftBukkit particle instance");
                            if(particle.equals(Particle.DUST_COLOR_TRANSITION)) throw new IllegalStateException("DustColorTransitions are currently not supported in this version");
                            int count = power.getIntOrDefault("count", 1);
                            float offset_y_no_vector = power.getFloatOrDefault("offset_y", 1.0f);
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

                            boolean visible_while_invis = power.getBooleanOrDefault("visible_while_invisible", false);
                            Particle final_particle = particle.builder().count(count).force(true).location(player.getLocation()).particle(particle).source(player).offset(offset_x, offset_y + offset_y_no_vector, offset_z).particle();
                            if (visible_while_invis) {
                                player.getWorld().spawnParticle(final_particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
                            } else {
                                if (!player.isInvisible()) {
                                    player.getWorld().spawnParticle(final_particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
                                }
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

    public static Particle computeParticleArgs(Object root){
        if(root instanceof String particleSt){
            return Particle.valueOf(ensureCorrectNamespace(particleSt).split(":")[1].toUpperCase());
        } else if (root instanceof JSONObject particle) {
            System.out.println(ensureCorrectNamespace(particle.get("type").toString()));
            return Particle.valueOf(ensureCorrectNamespace(particle.get("type").toString()).split(":")[1].toUpperCase());
        }
        return null;
    }

    private static String ensureCorrectNamespace(String string){
        System.out.println(string.contains(":"));
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
