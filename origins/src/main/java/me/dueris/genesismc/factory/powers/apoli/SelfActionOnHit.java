package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class SelfActionOnHit extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void s(EntityDamageByEntityEvent e) {
        Entity actor = e.getEntity();
        Entity target = e.getDamager();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (CooldownUtils.isPlayerInCooldownFromTag((Player) target, Utils.getNameOrTag(power))) return;
                if (ConditionExecutor.testEntity(power.getJsonObjectOrNew("condition"), (CraftEntity) player)) {
                    setActive(player, power.getTag(), true);
                    Actions.executeEntity(target, power.getJsonObjectOrNew("entity_action"));
                    if (power.getObjectOrDefault("cooldown", 1) != null) {
                        CooldownUtils.addCooldown((Player) target, Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), power.getJsonObject("hud_render"));
                    }
                } else {
                    setActive(player, power.getTag(), false);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:self_action_on_hit";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return self_action_on_hit;
    }

}
