package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.network.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(prevent_entity_render.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    for(Entity entity : p.getWorld().getEntities()){
                        if(ConditionExecutor.check("entity_condition", "entity_condition", p, origin, "origins:prevent_entity_render", null, p)){
                            if(ConditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_render", null, p)){
                                p.hideEntity(GenesisMC.getPlugin(), entity);
                            }else{
                                p.showEntity(GenesisMC.getPlugin(), entity);
                            }
                        }else{
                            p.showEntity(GenesisMC.getPlugin(), entity);
                        }
                    }
                }
            }
        }
    }
}
