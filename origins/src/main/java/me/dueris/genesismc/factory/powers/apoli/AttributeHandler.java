package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AttributeExecuteEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.OriginPage;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class AttributeHandler extends CraftPower implements Listener {

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, double value) {
        BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
        if (mathOperator != null) {
            double result = (double) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
            new Throwable().printStackTrace();
        }
    }

    @EventHandler
    public void powerUpdate(PowerUpdateEvent e) {
        if (!e.getPower().getType().equalsIgnoreCase(this.getPowerFile())) return;
        Player p = e.getPlayer();
        OriginPage.setAttributesToDefault(p);
        if (attribute.contains(p)) {
            runAttributeModifyPower(e);
        }
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player p = e.getPlayer();
        OriginPage.setAttributesToDefault(p);
        if (attribute.contains(p)) {
            runAttributeModifyPower(e);
        }
    }

    protected void runAttributeModifyPower(PlayerEvent e) {
        Player p = e.getPlayer();
        if (!attribute.contains(p)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                if (power == null) continue;

                for (Modifier modifier : power.getModifiers()) {
                    if (modifier.handle.getString("attribute").equalsIgnoreCase("reach-entity-attributes:reach")) {
                        extra_reach.add(p);
                        continue;
                    } else if (modifier.handle.getString("attribute").equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                        extra_reach_attack.add(p);
                        continue;
                    } else {
                        ReachUtils.setFinalReach(p, ReachUtils.getDefaultReach(p));
                    }

                    try {
                        Attribute attribute_modifier = Attribute.valueOf(NamespacedKey.fromString(modifier.handle.getString("attribute")).asString().split(":")[1].replace(".", "_").toUpperCase());

                        float value = modifier.value();
                        double base_value = p.getAttribute(attribute_modifier).getBaseValue();
                        String operation = modifier.operation();
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                        AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power.toString(), power);
                        Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
                        setActive(p, power.getTag(), true);
                        p.sendHealthUpdate();
                    } catch (Exception ev) {
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:attribute";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return attribute;
    }

    public static class ReachUtils implements Listener {

        private static Block getClosestBlockInSight(Player player, double maxRange, double normalReach) {
            // Get the player's eye location
            Location eyeLocation = player.getEyeLocation();

            // Get the direction the player is looking at
            Vector direction = eyeLocation.getDirection();

            // Iterate through the blocks in the line of sight
            for (double distance = 0.0; distance <= maxRange; distance += 0.1) {
                Location targetLocation = eyeLocation.clone().add(direction.clone().multiply(distance));
                Block targetBlock = targetLocation.getBlock();

                // Check if the block can be broken and it's outside of the normal reach
                if (targetBlock.getType() != Material.AIR && targetBlock.getType().isSolid()
                    && distance > normalReach) {
                    return targetBlock;
                }
            }

            return null; // No block in sight within the range
        }

        public static int getDefaultReach(Entity entity) {
            if (entity instanceof Player p) {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
                    return 5;
                }
            }
            return 3;
        }

        public static void setFinalReach(Entity p, double value) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE, value);
        }

        public static double getFinalReach(Entity p) {
            if (p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE)) {
                return p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE);
            } else {
                return getDefaultReach(p);
            }
        }
    }
}
