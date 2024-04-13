package me.dueris.genesismc.util.entity;

import javassist.NotFoundException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.GravityPower;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.util.SendCharts;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OriginPlayerAccessor implements Listener {

    // Power maps of every power based on each layer applied to the player
    public static HashMap<Player, HashMap<Layer, ArrayList<Power>>> playerPowerMapping = new HashMap<>();
    // A list of CraftPowers to be ran on the player
    public static HashMap<Player, ArrayList<ApoliPower>> powersAppliedList = new HashMap<>();
    // A list of Players that have powers that should be run
    public static ArrayList<Player> hasPowers = new ArrayList<>();
    /**
     * For some reason, a mod on the client breaks the ability to check the
     * SharedConstant value retrieved and set in Player#isSprinting(), but it still sends
     * the sprinting state update to the server. This is a workaround to ensure that
     * the EntityCondition apoli:is_sprinting catches that
     */
    public static ArrayList<Player> currentSprintingPlayersFallback = new ArrayList<>();

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

    public static boolean hasOrigin(Player player, String originTag) {
        if (OriginDataContainer.getDataMap().containsKey(player)) {
            HashMap<Layer, Origin> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
            for (Origin origin : origins.values()) if (origin.getTag().equals(originTag)) return true;
        }
        return false;
    }

    public static Origin getOrigin(Player player, Layer layer) {
        if (!OriginDataContainer.getDataMap().containsKey(player)) {
            if (OriginDataContainer.getLayer(player) == null) {
                setOrigin(player, layer, CraftApoli.nullOrigin());
                return CraftApoli.nullOrigin();
            }
        }
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player), layer);
    }

    public static HashMap<Layer, Origin> getOrigin(Player player) {
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
    }

    public static void setupPowers(Player p) {
        OriginDataContainer.loadData(p);
        String[] layers = OriginDataContainer.getLayer(p).split("\n");
        HashMap<Layer, ArrayList<Power>> map = new HashMap<>();
        for (String layer : layers) {
            String[] layerData = layer.split("\\|");
            Layer layerContainer = CraftApoli.getLayerFromTag(layerData[0]);
            ArrayList<Power> powers = new ArrayList<>();
            // setup powers
            for (String dataPiece : layerData) {
                if (layerData.length == 1) continue;
                Power powerCon = (Power) GenesisMC.getPlugin().registry.retrieve(Registries.POWER).get(NamespacedKey.fromString(dataPiece));
                if (powerCon != null) {
                    if (powers.contains(powerCon)) continue;
                    powers.add(powerCon);
                    if (powerCon.isOriginMultipleParent()) {
                        ArrayList<Power> nestedPowers = CraftApoli.getNestedPowers(powerCon);
                        for (Power nested : nestedPowers) {
                            if (nested != null) powers.add(nested);
                        }
                    }
                }
            }
            map.put(layerContainer, powers);
        }
        playerPowerMapping.put(p, map);
    }

    public static ArrayList<Power> getMultiPowerFileFromType(Player p, String powerType) {
        ArrayList<Power> powers = new ArrayList<>();
        if (playerPowerMapping.get(p) == null) return powers;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (layer == null) continue;
            for (Power power : playerPowerMapping.get(p).get(layer)) {
                if (power == null) continue;
                if (power.getType().equals(powerType)) powers.add(power);
            }
        }
        return powers;
    }

    public static ArrayList<Power> getMultiPowerFileFromType(Player p, String powerType, Layer layer) {
        ArrayList<Power> powers = new ArrayList<>();
        if (playerPowerMapping.get(p) == null) return powers;
        for (Power power : playerPowerMapping.get(p).get(layer)) {
            if (power == null) continue;
            if (power.getType().equals(powerType)) powers.add(power);
        }
        return powers;
    }

    public static Power getSinglePowerFileFromType(Player p, String powerType, Layer layer) {
        if (playerPowerMapping.get(p) == null) return null;
        for (Power power : playerPowerMapping.get(p).get(layer)) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    public static boolean hasCoreOrigin(Player player, Layer layer) {
        String originTag = OriginPlayerAccessor.getOrigin(player, layer).getTag();
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

    public static boolean hasPower(Player p, String powerKey) {
        if (playerPowerMapping.containsKey(p)) {
            for (Layer layerContainer : playerPowerMapping.get(p).keySet()) {
                for (Power power : playerPowerMapping.get(p).get(layerContainer)) {
                    if (power.getTag().equalsIgnoreCase(powerKey)) return true;
                }
            }
        }
        return false;
    }

    public static void setOrigin(Player player, Layer layer, Origin origin) {
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originLayer");
        HashMap<Layer, Origin> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        if (!CraftApoli.getLayersFromRegistry().contains(layer)) {
            return;
        }

        if (!origin.getTag().equals(CraftApoli.nullOrigin().getTag())) {
            try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        for (Layer layers : origins.keySet()) {
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
                    GravityPower g = new GravityPower();
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

    public static void removeOrigin(Player player, Layer layer) {
        HashMap<Layer, Origin> origins = getOrigin(player);
        ArrayList<Layer> layers = new ArrayList<>(origins.keySet());
        for (Layer playerLayer : layers) {
            if (playerLayer.getTag().equals(layer.getTag())) origins.remove(playerLayer);
        }
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        OriginDataContainer.loadData(player);
    }

    public static Layer getLayer(Player p, Origin origin) {
        HashMap<Layer, Origin> origins = getOrigin(p);
        for (Layer layer : origins.keySet()) {
            if (origins.get(layer).getTag().equals(origin.getTag())) return layer;
        }
        return null;
    }

    public static boolean isInPhantomForm(Player player) {
        return player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
    }

    public static void assignPowers(@NotNull Player player) {
        HashMap<Layer, Origin> origins = getOrigin(player);
        for (Layer layer : origins.keySet()) {
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

    public static List<ApoliPower> getPowersApplied(Player p) {
        List<ApoliPower> array = new ArrayList<>();
        for (Player plc : powersAppliedList.keySet()) {
            if (plc.equals(p)) {
                array.addAll(powersAppliedList.get(plc));

            }
        }
        return array;
    }

    public static void assignPowers(@NotNull Player player, @NotNull Layer layer) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        try {
            List<Power> powersToExecute = new ArrayList<>();
            CompletableFuture.runAsync(() -> {
                for (Power power : playerPowerMapping.get(player).get(layer)) {
                    if (power == null) continue;
                    String name = power.getType();
                    if (name.equalsIgnoreCase("apoli:simple")) {
                        name = power.getTag();
                    }
                    ApoliPower c = (ApoliPower) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(NamespacedKey.fromString(name));
                    if (c != null) {
                        c.getPowerArray().add(player);
                        if (!powersAppliedList.containsKey(player)) {
                            ArrayList lst = new ArrayList<>();
                            lst.add(c);
                            powersAppliedList.put(player, lst);
                        } else {
                            powersAppliedList.get(player).add(c);
                        }
                        if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
                        }
                        powersToExecute.add(power);
                    }
                }
            }).thenRun(() -> {
                OriginDataContainer.loadData(player);
                setupPowers(player);
                hasPowers.add(player);
            }).get();

            powersToExecute.forEach((power) -> {
                new PowerUpdateEvent(player, power, false).callEvent();
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void unassignPowers(@NotNull Player player) {
        HashMap<Layer, Origin> origins = getOrigin(player);
        for (Layer layer : origins.keySet()) {
            try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void unassignPowers(@NotNull Player player, @NotNull Layer layer) throws NotFoundException {
        try {
            List<Power> powersToExecute = new ArrayList<>();
            CompletableFuture.runAsync(() -> {
                for (Power power : playerPowerMapping.get(player).get(layer)) {
                    if (power == null) continue;
                    String name = power.getType();
                    if (name.equalsIgnoreCase("apoli:simple")) {
                        name = power.getTag();
                    }
                    ApoliPower c = (ApoliPower) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(NamespacedKey.fromString(name));
                    if (c != null) {
                        c.getPowerArray().remove(player);
                        if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
                        }
                        powersToExecute.add(power);
                    }
                }
            }).thenRun(() -> {
                for (ApoliPower classes : getPowersApplied(player)) {
                    powersAppliedList.get(player).remove(classes);
                }
                OriginDataContainer.unloadData(player);
                hasPowers.remove(player);
            }).get();

            powersToExecute.forEach((power) -> {
                new PowerUpdateEvent(player, power, true).callEvent();
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void sprint(PlayerToggleSprintEvent e) {
        if (e.isSprinting()) currentSprintingPlayersFallback.add(e.getPlayer());
        else if (currentSprintingPlayersFallback.contains(e.getPlayer()))
            currentSprintingPlayersFallback.remove(e.getPlayer());
    }
}
