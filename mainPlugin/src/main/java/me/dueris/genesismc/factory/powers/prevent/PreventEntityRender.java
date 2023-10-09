package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
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

    Player p;

    public PreventEntityRender() {
        this.p = p;
    }

    @Override
    public void run(Player p) {
        if (prevent_entity_render.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (Entity entity : p.getWorld().getEntities()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    for(PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())){
                        if (conditionExecutor.check("entity_condition", "entity_condition", p, power, "origins:prevent_entity_render", entity, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "origins:prevent_entity_render", entity, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                if(p.canSee(entity)){
                                    p.hideEntity(GenesisMC.getPlugin(), entity);
                                }
//                                entity.setGlowing(true);
                                setActive(power.getTag(), true);
                            } else {
                                setActive(power.getTag(), false);
//                                entity.setGlowing(false);
                                if(!p.canSee(entity)){
                                    p.showEntity(GenesisMC.getPlugin(), entity);
                                }
                            }
                        } else {
                            setActive(power.getTag(), false);
//                            entity.setGlowing(false);
                            if(!p.canSee(entity)){
                                p.showEntity(GenesisMC.getPlugin(), entity);
                            }
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
