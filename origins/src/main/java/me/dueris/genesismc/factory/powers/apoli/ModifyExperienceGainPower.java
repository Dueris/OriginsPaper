package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class ModifyExperienceGainPower extends CraftPower implements Listener {

    @EventHandler
    public void run(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        if (modify_xp_gain.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    for (Power power : OriginPlayerAccessor.getPowers(p, getType(), layer)) {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            for (Modifier modifier : power.getModifiers()) {
                                Float value = modifier.value();
                                String operation = modifier.operation();
                                BinaryOperator mathOperator = Utils.getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = (float) mathOperator.apply(e.getAmount(), value);
                                    e.setAmount(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                    setActive(p, power.getTag(), true);
                                }
                            }

                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:modify_xp_gain";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_xp_gain;
    }
}
