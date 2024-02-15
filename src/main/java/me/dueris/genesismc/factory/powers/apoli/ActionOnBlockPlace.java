package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.console.OriginConsoleSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBlockPlace extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void blockBreak(BlockPlaceEvent e) {
        if (action_on_block_place.contains(e.getPlayer())) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (GenesisMC.getConditionExecutor().check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, e.getBlockPlaced(), null, e.getItemInHand(), null)) {
                        if (GenesisMC.getConditionExecutor().check("item_condition", "item_conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, e.getBlockPlaced(), null, e.getItemInHand(), null)) {
                            if (GenesisMC.getConditionExecutor().check("place_on_condition", "place_on_conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, e.getBlockAgainst(), null, e.getItemInHand(), null)) {
                                if (GenesisMC.getConditionExecutor().check("place_to_condition", "place_to_conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, e.getBlockPlaced(), null, e.getItemInHand(), null)) {
                                    e.setCancelled(true);
                                    setActive(e.getPlayer(), power.getTag(), true);
                                    Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                                    Actions.ItemActionType(e.getItemInHand(), power.getAction("held_item_action"));
                                    Actions.BlockActionType(e.getBlockAgainst().getLocation(), power.getAction("place_on_action"));
                                    Actions.BlockActionType(e.getBlockPlaced().getLocation(), power.getAction("place_to_action"));
                                    if (power.get("result_stack") != null) {
                                        JSONObject jsonObject = power.get("result_stack");
                                        int amt;
                                        if (jsonObject.get("amount").toString() != null) {
                                            amt = Integer.parseInt(jsonObject.get("amount").toString());
                                        } else {
                                            amt = 1;
                                        }
                                        Bukkit.dispatchCommand(new OriginConsoleSender(), "give {player} {item} {amount}"
                                                .replace("{player}", e.getPlayer().getName()).replace("{item}", jsonObject.get("item").toString()).replace("{amount}", String.valueOf(amt))
                                        );
                                    }
                                } else {
                                    setActive(e.getPlayer(), power.getTag(), false);
                                }
                            } else {
                                setActive(e.getPlayer(), power.getTag(), false);
                            }
                        } else {
                            setActive(e.getPlayer(), power.getTag(), false);
                        }
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_block_place";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_block_place;
    }

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
}
