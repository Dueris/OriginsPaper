package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (prevent_entity_render.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    for (Entity entity : p.getWorld().getEntities()) {
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if (conditionExecutor.check("entity_condition", "entity_condition", p, origin, "origins:prevent_entity_render", null, p)) {
                            if (conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_render", null, p)) {
                                p.hideEntity(GenesisMC.getPlugin(), entity);
                                if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                    getPowerArray().remove(p);
                                    return;
                                }
                                if (!getPowerArray().contains(p)) return;
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            } else {
                                if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                    getPowerArray().remove(p);
                                    return;
                                }
                                if (!getPowerArray().contains(p)) return;
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                p.showEntity(GenesisMC.getPlugin(), entity);
                            }
                        } else {
                            if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                            p.showEntity(GenesisMC.getPlugin(), entity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_render;
    }
}
