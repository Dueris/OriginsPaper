package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.util.InstanceGetter;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionOnBlockPlace extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void blockBreak(BlockPlaceEvent e) {
        if (action_on_block_place.contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if(!(ConditionExecutor.testEntity((JSONObject) power.get("condition"), (CraftEntity) e.getPlayer()) && ConditionExecutor.testItem((JSONObject) power.get("item_condition"), e.getItemInHand()) && ConditionExecutor.testBlock((JSONObject) power.get("place_to_condition"), (CraftBlock) e.getBlockPlaced()) && ConditionExecutor.testBlock((JSONObject) power.get("place_on_condition"), (CraftBlock) e.getBlockAgainst()))) return;
                    boolean pass = power.getPowerFile().getFactoryProvider().getJsonArray("directions").isEmpty();
                    for(BlockFace face : InstanceGetter.getBlockFaceFromDirection(power.getPowerFile().getFactoryProvider().getJsonArray("directions"))){
                        if(e.getBlock().getFace(e.getBlockAgainst()).equals(face)){
                            pass = true;
                        }
                    }
                    if(!pass) return;
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                    Actions.ItemActionType(e.getItemInHand(), power.getAction("held_item_action"));
                    Actions.BlockActionType(e.getBlockAgainst().getLocation(), power.getAction("place_on_action"));
                    Actions.BlockActionType(e.getBlockPlaced().getLocation(), power.getAction("place_to_action"));
                    e.getPlayer().getInventory().addItem(power.getPowerFile().getFactoryProvider().getItemStack("result_stack"));
                    Actions.ItemActionType(power.getPowerFile().getFactoryProvider().getItemStack("result_stack"), power.getAction("result_item_action"));
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
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return super.getDefaultObjectFactory(List.of(
            new FactoryObjectInstance("entity_action", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("held_item_action", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("place_to_action", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("place_on_action", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("item_condition", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("place_to_condition", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("place_on_condition", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("result_stack", ItemStack.class, new ItemStack(Material.AIR)),
            new FactoryObjectInstance("result_item_action", JSONObject.class, new JSONObject())
        ));
    }
}
