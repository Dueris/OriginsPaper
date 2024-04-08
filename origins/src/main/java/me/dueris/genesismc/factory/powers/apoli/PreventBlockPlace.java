package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_block_place;

public class PreventBlockPlace extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void blockBreak(BlockPlaceEvent e) {
        if (prevent_block_place.contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (!(ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer()) && ConditionExecutor.testItem(power.get("item_condition"), e.getItemInHand()) && ConditionExecutor.testBlock(power.get("place_to_condition"), (CraftBlock) e.getBlockPlaced()) && ConditionExecutor.testBlock(power.get("place_on_condition"), (CraftBlock) e.getBlockAgainst())))
                        return;
                    e.setCancelled(true);
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.executeEntity(e.getPlayer(), power.getEntityAction());
                    Actions.executeItem(e.getItemInHand(), power.getAction("held_item_action"));
                    Actions.executeBlock(e.getBlockAgainst().getLocation(), power.getAction("place_on_action"));
                    Actions.executeBlock(e.getBlockPlaced().getLocation(), power.getAction("place_to_action"));
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_block_place";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_block_place;
    }

}
