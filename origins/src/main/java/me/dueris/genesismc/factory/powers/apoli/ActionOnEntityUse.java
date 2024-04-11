package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnEntityUse extends CraftPower implements Listener {

    private static final ArrayList<Player> cooldownTick = new ArrayList<>();

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Player actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!getPowerArray().contains(actor)) return;
        if (cooldownTick.contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (!ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) actor)) return;
                if (!ConditionExecutor.testItem(power.get("item_condition"), actor.getInventory().getItem(e.getHand())))
                    return;
                if (!ConditionExecutor.testBiEntity(power, power.get("bientity_condition"), (CraftEntity) actor, (CraftEntity) target))
                    return;
                cooldownTick.add(actor);
                setActive(e.getPlayer(), power.getTag(), true);
                Actions.executeBiEntity(power, actor, target, power.getBiEntityAction());
                Actions.executeItem(actor.getActiveItem(), power.get("held_item_action"));
                Actions.executeItem(actor.getActiveItem(), power.get("result_item_action"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownTick.remove(actor);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 1);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_entity_use;
    }

}
