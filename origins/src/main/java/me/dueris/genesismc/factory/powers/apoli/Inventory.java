package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.ContainerType;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Inventory extends CraftPower implements Listener {

    @EventHandler
    public void MoveBackChange(OriginChangeEvent e) {
        Player p = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!shulker_inventory.contains(p)) {
                            ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(p, power.getTag());
                            org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                            vaultItems.forEach(vault::addItem);
                            for (ItemStack item : vault.getContents()) {
                                if (item != null && item.getType() != Material.AIR) {
                                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                                    vault.removeItem(item);
                                }
                            }
                            ArrayList<ItemStack> prunedItems = new ArrayList<>();

                            Arrays.stream(vault.getContents())
                                .filter(Objects::nonNull)
                                .forEach(prunedItems::add);

                            InventorySerializer.storeItems(prunedItems, p, power.getTag());
                            vault.clear();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
            }
        }
    }

    @EventHandler
    public void keytrigger(KeybindTriggerEvent e) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (getPlayersWithPower().contains(e.getPlayer())) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getType(), layer)) {
                    if (CooldownUtils.isPlayerInCooldownFromTag(e.getPlayer(), Utils.getNameOrTag(power))) continue;
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), e.getPlayer())) {
                            ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(e.getPlayer(), power.getTag());
                            org.bukkit.inventory.Inventory vault = power.getEnumValue("container_type", ContainerType.class).createInventory(e.getPlayer(), Utils.createIfPresent(power.getString("title")));
                            vaultItems.forEach(vault::addItem);
                            e.getPlayer().openInventory(vault);
                        }
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void deathTIMEEE(PlayerDeathEvent e) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (shulker_inventory.contains(e.getPlayer())) {
                Player p = e.getPlayer();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                    if (power.getBooleanOrDefault("drop_on_death", false)) {
                        ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(p, power.getTag());
                        org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                        vaultItems.forEach(vault::addItem);
                        for (ItemStack item : vault.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                p.getWorld().dropItemNaturally(p.getLocation(), item);
                                vault.removeItem(item);
                            }
                        }
                        ArrayList<ItemStack> prunedItems = new ArrayList<>();

                        Arrays.stream(vault.getContents())
                            .filter(Objects::nonNull)
                            .forEach(prunedItems::add);

                        InventorySerializer.storeItems(prunedItems, p, power.getTag());
                        vault.clear();
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:inventory";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return shulker_inventory;
    }
}

