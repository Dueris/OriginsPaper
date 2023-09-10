package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.factory.powers.CraftPower;
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
//            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
//                for (Entity entity : p.getWorld().getEntities()) {
//                    ConditionExecutor conditionExecutor = new ConditionExecutor();
//                    if (conditionExecutor.check("entity_condition", "entity_condition", p, origin, "origins:prevent_entity_render", p, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
//                        if (conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_render", p, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
//                            p.hideEntity(GenesisMC.getPlugin(), entity);
//                            if (power == null) {
//                                getPowerArray().remove(p);
//                                return;
//                            }
//                            if (!getPowerArray().contains(p)) return;
//                            setActive(power.getTag(), true);
//                        } else {
//                            if (power == null) {
//                                getPowerArray().remove(p);
//                                return;
//                            }
//                            if (!getPowerArray().contains(p)) return;
//                            setActive(power.getTag(), false);
//                            p.showEntity(GenesisMC.getPlugin(), entity);
//                        }
//                    } else {
//                        if (power == null) {
//                            getPowerArray().remove(p);
//                            return;
//                        }
//                        if (!getPowerArray().contains(p)) return;
//                        setActive(power.getTag(), false);
//                        p.showEntity(GenesisMC.getPlugin(), entity);
//                    }
//                }
//            }
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
