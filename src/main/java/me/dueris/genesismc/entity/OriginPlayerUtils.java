package me.dueris.genesismc.entity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginDataContainer;
import me.dueris.genesismc.enums.OriginDataType;
import me.dueris.genesismc.events.OriginChooseEvent;
import me.dueris.genesismc.events.PowerAssignEvent;
import me.dueris.genesismc.events.PowerUnassignEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.Gravity;
import me.dueris.genesismc.factory.powers.simple.OriginSimpleContainer;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.SendCharts;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javassist.NotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class OriginPlayerUtils {

    public static void setHasFirstChose(Player p, boolean chosen){
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "hasFirstChose"), PersistentDataType.BOOLEAN, chosen);
    }

    public static void moveEquipmentInventory(Player player, EquipmentSlot equipmentSlot) {
        ItemStack item = player.getInventory().getItem(equipmentSlot);

        if (item != null && item.getType() != Material.AIR) {
            // Find an empty slot in the player's inventory
            int emptySlot = player.getInventory().firstEmpty();

            if (emptySlot != -1) {
                // Set the equipment slot to empty
                player.getInventory().setItem(equipmentSlot, null);

                // Move the item to the empty slot
                player.getInventory().setItem(emptySlot, item);
            }
        }
    }

    public static void launchElytra(Player player, float speed) {
        Location location = player.getEyeLocation();
        @NotNull Vector direction = location.getDirection().normalize();
        Vector velocity = direction.multiply(speed);
        player.setVelocity(velocity);
    }

    /**
     * @param originTag The tag of the origin.
     * @return true if the player has the origin.
     */
    public static boolean hasOrigin(Player player, String originTag) {
        if(OriginDataContainer.getDataMap().containsKey(player)){
            HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
            for (OriginContainer origin : origins.values()) if (origin.getTag().equals(originTag)) return true;
        }
        return false;
    }

    /**
     * @param layer The layer the origin is in
     * @return The OriginContainer for the specified layer
     */

    public static OriginContainer getOrigin(Player player, LayerContainer layer) {
        if(!OriginDataContainer.getDataMap().containsKey(player)){
            if (OriginDataContainer.getLayer(player) == null) {
                setOrigin(player, layer, CraftApoli.nullOrigin());
                return CraftApoli.nullOrigin();
            }
        }
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player), layer);
    }

    /**
     * @return A HashMap of layers and OriginContainer that the player has.
     */

    public static HashMap<LayerContainer, OriginContainer> getOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!OriginDataContainer.getDataMap().containsKey(player) || OriginDataContainer.getLayer(player) == null) {
            ArrayList<LayerContainer> layers = CraftApoli.getLayers();
//            for (LayerContainer layer : layers) {
//                System.out.println("2");
//                setOrigin(player, layer, CraftApoli.nullOrigin());
//                return new HashMap<>(Map.of(layer, CraftApoli.nullOrigin()));
//            }
            // This caused a lot of isseus lol
        }
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
    }

    public static HashMap<Player, HashMap<LayerContainer, ArrayList<PowerContainer>>> powerContainer = new HashMap<>();

    public static void setupPowers(Player p){
        OriginDataContainer.loadData(p);
        String[] layers = OriginDataContainer.getLayer(p).split("\n");
        HashMap<LayerContainer, ArrayList<PowerContainer>> map = new HashMap<>();
        for (String layer : layers) {
            String[] layerData = layer.split("\\|");
            LayerContainer layerContainer = CraftApoli.getLayerFromTag(layerData[0]);
            ArrayList<PowerContainer> powers = new ArrayList<>();
            // setup powers
            for(String dataPiece : layerData){
                if(layerData.length == 1) continue;
                PowerContainer powerCon = CraftApoli.keyedPowerContainers.get(dataPiece);
                    if(powerCon != null){
                        if(powers.contains(powerCon)) continue;
                        powers.add(powerCon);
                        if(powerCon.isOriginMultipleParent()){
                            ArrayList<PowerContainer> nestedPowers = CraftApoli.getNestedPowers(powerCon);
                            for(PowerContainer nested : nestedPowers){
                                if(nested != null) powers.add(nested);
                            }
                        }
                    }
            }
            map.put(layerContainer, powers);
        }
        powerContainer.put(p, map);
//        for(PowerContainer power : powerContainer.get(p).get(CraftApoli.getLayerFromTag("origins:origin"))){
//            System.out.println(power.getTag());
//        }
    }

    public static ArrayList<PowerContainer> getMultiPowerFileFromType(Player p, String powerType) {
        ArrayList<PowerContainer> powers = new ArrayList<>();
        if(powerContainer.get(p) == null) return powers;
        for(LayerContainer layer : CraftApoli.getLayers()){
            if(layer == null) continue;
            for (PowerContainer power : powerContainer.get(p).get(layer)) {
                if (power == null) continue;
                if (power.getType().equals(powerType)) powers.add(power);
            }
        }
        return powers;
    }

    public static ArrayList<PowerContainer> getMultiPowerFileFromType(Player p, String powerType, LayerContainer layer) {
        ArrayList<PowerContainer> powers = new ArrayList<>();
        if(powerContainer.get(p) == null) return powers;
        for (PowerContainer power : powerContainer.get(p).get(layer)) {
            if (power == null) continue;
            if (power.getType().equals(powerType)) powers.add(power);
        }
        return powers;
    }

    public static PowerContainer getSinglePowerFileFromType(Player p, String powerType, LayerContainer layer) {
        if(powerContainer.get(p) == null) return null;
        for (PowerContainer power : powerContainer.get(p).get(layer)) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    public static boolean hasCoreOrigin(Player player, LayerContainer layer) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        String originTag = OriginPlayerUtils.getOrigin(player, layer).getTag();
        if (originTag.contains("origins:human")) {
            return true;
        } else if (originTag.contains("origins:enderian")) {
            return true;
        } else if (originTag.contains("origins:merling")) {
            return true;
        } else if (originTag.contains("origins:phantom")) {
            return true;
        } else if (originTag.contains("origins:elytrian")) {
            return true;
        } else if (originTag.contains("origins:blazeborn")) {
            return true;
        } else if (originTag.contains("origins:avian")) {
            return true;
        } else if (originTag.contains("origins:arachnid")) {
            return true;
        } else if (originTag.contains("origins:shulk")) {
            return true;
        } else if (originTag.contains("origins:feline")) {
            return true;
        } else if (originTag.contains("origins:starborne")) {
            return true;
        } else if (originTag.contains("origins:allay")) {
            return true;
        } else if (originTag.contains("origins:rabbit")) {
            return true;
        } else if (originTag.contains("origins:bee")) {
            return true;
        } else if (originTag.contains("origins:sculkling")) {
            return true;
        } else if (originTag.contains("origins:creep")) {
            return true;
        } else if (originTag.contains("origins:slimeling")) {
            return true;
        } else return originTag.contains("origins:piglin");
    }

    public static void setOrigin(Player player, LayerContainer layer, OriginContainer origin) {
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originLayer");
        HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        if (!CraftApoli.getLayers().contains(layer)) {
            return;
        }

        if (!origin.getTag().equals(CraftApoli.nullOrigin().getTag()))
            {try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }}
        for (LayerContainer layers : origins.keySet()) {
            if (layer.getTag().equals(layers.getTag())) origins.replace(layers, origin);
        }
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        OriginDataContainer.loadData(player);
        setupPowers(player);

        String originTag = origin.getTag();
        if (!originTag.equals(CraftApoli.nullOrigin().getTag())) SendCharts.originPopularity(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    assignPowers(player, layer);
                    // Extra precaution due to gravity messing up on origin switch
                    Gravity g = new Gravity();
                    g.run(player);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 3L);
    }

    /**
     * WARNING: will remove the layer containing the origin from the playerdata. If you need to make a player re choose an origin use setOrigin and pass in CraftApoli.nullOrigin().
     *
     * @param player player.
     * @param layer  the layer to remove from playerdata.
     */
    public static void removeOrigin(Player player, LayerContainer layer) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        ArrayList<LayerContainer> layers = new ArrayList<>(origins.keySet());
        for (LayerContainer playerLayer : layers) {
            if (playerLayer.getTag().equals(layer.getTag())) origins.remove(playerLayer);
        }
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        OriginDataContainer.loadData(player);
    }

    public static LayerContainer getLayer(Player p, OriginContainer origin) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(p);
        for (LayerContainer layer : origins.keySet()) {
            if (origins.get(layer).getTag().equals(origin.getTag())) return layer;
        }
        return null;
    }

    public static void resetOriginData(Player player, OriginDataType type) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.IN_PHASING_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
        }

    }

    public static void setOriginData(Player player, OriginDataType type, int value) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, value);
        }
    }

    public static void setOriginData(Player player, OriginDataType type, boolean value) {
        if (type.equals(OriginDataType.IN_PHASING_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, value);
        }
    }

    public static void triggerChooseEvent(Player player) {
        OriginChooseEvent chooseEvent = new OriginChooseEvent(player);
        getServer().getPluginManager().callEvent(chooseEvent);
    }

    public static boolean isInPhantomForm(Player player) {
        return player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
    }

    public static void assignPowers(Player player) {
        if (player == null) Bukkit.getServer().getConsoleSender().sendMessage("urm the player is null?!");
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            try {
                assignPowers(player, layer);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static HashMap<Player, ArrayList<Class<? extends CraftPower>>> powersAppliedList = new HashMap<>();
    public static ArrayList<Player> hasPowers = new ArrayList<>();

    public static List<Class<? extends CraftPower>> getPowersApplied(Player p) {
        List<Class<? extends CraftPower>> array = new ArrayList<>();
        for (Player plc : powersAppliedList.keySet()) {
            if (plc.equals(p)) {
                for (Class<? extends CraftPower> c : powersAppliedList.get(plc)) {
                    array.add(c);
                }

            }
        }
        return array;
    }

    public static void assignPowers(Player player, LayerContainer layer) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
//        OriginContainer origin = getOrigin(player, layer);
        if (player == null) Bukkit.getServer().getConsoleSender().sendMessage("rip player null");
//        if (origin.getPowerContainers().isEmpty()) {
//            player.sendMessage("BRO ITS EMPTY WAHT");
//        }
        for (PowerContainer power : powerContainer.get(player).get(layer)) {
            if (power == null) continue;
            if(power.getType().equalsIgnoreCase("origins:simple")){
                Class<? extends CraftPower> c = OriginSimpleContainer.getFromRegistryOrThrow(power.getTag());
                CraftPower craftPower = null;
    
                    try {
                        craftPower = c.newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    Field field = c.getDeclaredField("powerReference");
                    field.setAccessible(true);
                    if (power.getTag().equalsIgnoreCase(((NamespacedKey)field.get(craftPower)).asString())) {
                        craftPower.getPowerArray().add(player);
                        if (!powersAppliedList.containsKey(player)) {
                            ArrayList lst = new ArrayList<>();
                            lst.add(c);
                            powersAppliedList.put(player, lst);
                        } else {
                            powersAppliedList.get(player).add(c);
                        }
                        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned builtinImpl power[" + power.getTag() + "] to player " + player.getName());
                        }
                    }
            }else{
                for (Class<? extends CraftPower> c : CraftPower.getRegistered()) {
                    CraftPower craftPower = null;
    
                    try {
                        craftPower = c.newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (power.getType().equalsIgnoreCase(craftPower.getPowerFile())) {
                        craftPower.getPowerArray().add(player);
                        if (!powersAppliedList.containsKey(player)) {
                            ArrayList lst = new ArrayList<>();
                            lst.add(c);
                            powersAppliedList.put(player, lst);
                        } else {
                            powersAppliedList.get(player).add(c);
                        }
                        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
                        }
                    }
                }
            }
        }

//        PowerAssignEvent powerAssignEvent = new PowerAssignEvent(player, powerAppliedClasses, powerAppliedTypes, origin);
//        Bukkit.getServer().getPluginManager().callEvent(powerAssignEvent);
//        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(OriginPlayerUtils.getOrigin(player)));
        OriginDataContainer.loadData(player);
        setupPowers(player);

        hasPowers.add(player);
    }

    public static void unassignPowers(Player player) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void unassignPowers(Player player, LayerContainer layer) throws NotFoundException {
//        OriginContainer origin = getOrigin(player, layer);
        for (PowerContainer power : powerContainer.get(player).get(layer)) {
            if(power == null) continue;
            if(power.getType().equalsIgnoreCase("origins:simple")){
                Class<? extends CraftPower> c = OriginSimpleContainer.getFromRegistryOrThrow(power.getTag());
                CraftPower craftPower = null;
                try {
                    craftPower = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Field field = c.getDeclaredField("powerReference");
                    field.setAccessible(true);
                    if (power.getTag().equalsIgnoreCase(((NamespacedKey)field.get(craftPower)).asString())) {
                        craftPower.getPowerArray().remove(player);
                        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed builtinImpl power[" + power.getTag() + "] from player " + player.getName());
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                        | SecurityException e) {
                            throw new RuntimeException(e);
                }
            }else{
                for (Class<? extends CraftPower> c : CraftPower.getRegistered()) {
                    CraftPower craftPower = null;
                    try {
                        craftPower = c.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (power.getType().equalsIgnoreCase(craftPower.getPowerFile())) {
                        craftPower.getPowerArray().remove(player);
                        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
                        }
                    }
                }
            }
        }
        for (Class<? extends CraftPower> classes : getPowersApplied(player)) {
            powersAppliedList.get(player).remove(classes);
        }
//        PowerUnassignEvent powerUnassignEvent = new PowerUnassignEvent(player, powerRemovedClasses, powerRemovedTypes, origin);
//        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(OriginPlayerUtils.getOrigin(player)));
        OriginDataContainer.unloadData(player);
//        Bukkit.getServer().getPluginManager().callEvent(powerUnassignEvent);
        hasPowers.remove(player);
    }

    /**
     * @param p Player
     * @return The layers and origins currently assigned to the player
     */
    public static HashMap<LayerContainer, OriginContainer> returnOrigins(Player p) {
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(p));
    }

}
