package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.DataConverter;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

public class AttributeConditioned extends CraftPower implements Listener {

    private static final HashMap<Player, Boolean> applied = new HashMap<>();

    public void executeConditionAttribute(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;
                for (Modifier modifier : power.getModifiers()) {
                    if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) return;
                    Attribute attributeModifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
                    if (p.getAttribute(attributeModifier) != null) {
                        AttributeModifier m = DataConverter.convertToAttributeModifier(modifier);
                        p.getAttribute(attributeModifier).addTransientModifier(m);
                    }
                    p.sendHealthUpdate();
                }
            }

        }
    }

    public void inverseConditionAttribute(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;

                for (Modifier modifier : power.getModifiers()) {
                    Attribute attributeModifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
                    if (p.getAttribute(attributeModifier) != null) {
                        AttributeModifier m = DataConverter.convertToAttributeModifier(modifier);
                        p.getAttribute(attributeModifier).addTransientModifier(m);
                    }
                }
            }

        }
    }


    @EventHandler
    public void join(PlayerJoinEvent e) {
        applied.put(e.getPlayer(), false);
    }

    @Override
    public void run(Player p, Power power) {
        if (!applied.containsKey(p)) {
            applied.put(p, false);
        }
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            if (!applied.get(p)) {
                executeConditionAttribute(p);
                applied.put(p, true);
                setActive(p, power.getTag(), true);
            }
        } else {
            if (applied.get(p)) {
                inverseConditionAttribute(p);
                applied.put(p, false);
                setActive(p, power.getTag(), false);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:conditioned_attribute";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return conditioned_attribute;
    }
}
