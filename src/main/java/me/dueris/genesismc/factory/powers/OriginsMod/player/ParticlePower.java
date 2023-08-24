package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ParticlePower extends CraftPower {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(particle.contains(player)){
                for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
                    ConditionExecutor executor = new ConditionExecutor();
                    if(executor.check("condition", "conditions", player, origin, getPowerFile(), null, player)){
                        setActive(true);
                        Particle particle = Particle.valueOf(origin.getPowerFileFromType("origins:particle").get("particle", null).split(":")[1].toUpperCase());
                        int frequency = Integer.parseInt(origin.getPowerFileFromType("origins:particle").get("frequency", "1"));
                        int count = Integer.parseInt(origin.getPowerFileFromType("origins:particle").get("count", "1"));
                        float offset_y_no_vector = Float.parseFloat(String.valueOf(origin.getPowerFileFromType("origins:particle").get("offset_y", "1.0")));
                        float offset_x = 0.25f;
                        float offset_y = 0.50f;
                        float offset_z = 0.25f;
                        if(origin.getPowerFileFromType("origins:particle").getSpread().get("y") != null){
                            offset_y = Float.parseFloat(String.valueOf(origin.getPowerFileFromType("origins:particle").getSpread().get("y")));
                        }

                        if(origin.getPowerFileFromType("origins:particle").getSpread().get("x") != null){
                            offset_x = Float.parseFloat(String.valueOf(origin.getPowerFileFromType("origins:particle").getSpread().get("x")));
                        }

                        if(origin.getPowerFileFromType("origins:particle").getSpread().get("z") != null){
                            offset_z = Float.parseFloat(String.valueOf(origin.getPowerFileFromType("origins:particle").getSpread().get("z")));
                        }
                        boolean visible_while_invis = Boolean.parseBoolean(origin.getPowerFileFromType("origins:particle").get("visible_while_invisible", "false"));
                        Particle final_particle = particle.builder().count(count).force(true).location(player.getLocation()).particle(particle).source(player).offset(offset_x, offset_y + offset_y_no_vector, offset_z).particle();
                        if(visible_while_invis){
                            player.getWorld().spawnParticle(particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
                        }else{
                            if(!player.isInvisible()){
                                player.getWorld().spawnParticle(particle, new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
                            }
                        }
                    }else{
                        setActive(false);
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
