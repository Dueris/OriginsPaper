package me.dueris.genesismc.factory.powers.simple;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftCreeper;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ScareCreepers extends CraftPower implements OriginSimple, Listener {
    public static ArrayList<Player> scaryPlayers = new ArrayList<>();
    
    @Override
    public String getSimpleTagID() {
        return "origins:scare_creepers";
    }

    @Override
    public void run(Player p) {
        //no
    }

    @EventHandler
    public void target(EntityTargetLivingEntityEvent e){
        if(e.getEntity() instanceof Creeper creeper){
            if(e.getTarget() instanceof Player player){
                if(scaryPlayers.contains(player)){
                    // TODO: maybe try experimenting more with this? atm doesnt seem to be possible without mixin
//                    net.minecraft.world.entity.monster.Creeper c = ((CraftCreeper) creeper).getHandle();
//                    c.goalSelector.addGoal(1, new AvoidEntityGoal(c, net.minecraft.world.entity.player.Player.class, 6.0F, 1.0, 1.2));
//                    c.setIgnited(false);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "genesis:origins-simple-ljshdljkghslkdfjhgkjshdfk;jghskljdfg-[@dueris]";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return null;
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
    public void event(OriginChangeEvent e) {
        boolean hasMimicWardenPower = false;
        if(OriginPlayerUtils.powerContainer.get(e.getPlayer()) == null) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.powerContainer.get(e.getPlayer()).get(layer)) {
                if (power.getTag().equals("origins:scare_creepers")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !scaryPlayers.contains(e.getPlayer())) {
            scaryPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower) {
            scaryPlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerJoinEvent e) {
        boolean hasMimicWardenPower = false;
        if(OriginPlayerUtils.powerContainer.get(e.getPlayer()) == null) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.powerContainer.get(e.getPlayer()).get(layer)) {
                if (power.getTag().equals("origins:scare_creepers")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !scaryPlayers.contains(e.getPlayer())) {
            scaryPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower) {
            scaryPlayers.remove(e.getPlayer());
        }
    }
}
