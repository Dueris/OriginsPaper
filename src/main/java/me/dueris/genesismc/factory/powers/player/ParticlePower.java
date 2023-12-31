package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ParticlePower extends CraftPower {

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

    @Override
    public void run(Player player) {
        if (particle.contains(player)) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) {
                        getPowerArray().remove(player);
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", player, power, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
                            if (!getPowerArray().contains(player)) return;
                            setActive(player, power.getTag(), true);
                            Particle particle = Particle.valueOf(power.get("particle", null).split(":")[1].toUpperCase());
                            int count = Integer.parseInt(power.get("count", "1"));
                            float offset_y_no_vector = Float.parseFloat(String.valueOf(power.get("offset_y", "1.0")));
                            float offset_x = 0.25f;
                            float offset_y = 0.50f;
                            float offset_z = 0.25f;
                            if (power.getSpread().get("y") != null) {
                                offset_y = Float.parseFloat(String.valueOf(power.getSpread().get("y")));
                            }

                            if (power.getSpread().get("x") != null) {
                                offset_x = Float.parseFloat(String.valueOf(power.getSpread().get("x")));
                            }

                            if (power.getSpread().get("z") != null) {
                                offset_z = Float.parseFloat(String.valueOf(power.getSpread().get("z")));
                            }
                            boolean visible_while_invis = Boolean.parseBoolean(power.get("visible_while_invisible", "false"));
                            Particle final_particle = particle.builder().count(count).force(true).location(player.getLocation()).particle(particle).source(player).offset(offset_x, offset_y + offset_y_no_vector, offset_z).particle();
                            if (visible_while_invis) {
                                player.getWorld().spawnParticle(particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
                            } else {
                                if (!player.isInvisible()) {
                                    player.getWorld().spawnParticle(particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
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

    @Override
    public String getPowerFile() {
        return "origins:particle";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return particle;
    }
}
