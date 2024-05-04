package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.ContainerType;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Inventory extends CraftPower implements Listener {

    @EventHandler
    public void MoveBackChange(PowerUpdateEvent e) {
        if (!e.isRemoved()) return;
        Power power = e.getPower();
        Player p = e.getPlayer();
        GenesisMC.scheduler.parent.onMain(() -> {
            ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(p, power.getTag());
            for (ItemStack item : new ArrayList<>(vaultItems)) {
                if (item != null && item.getType() != Material.AIR) {
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                    vaultItems.remove(item);
                }
            }

            InventorySerializer.storeItems(new ArrayList<>(), p, power.getTag());
        });
    }

    @EventHandler
    public void keytrigger(KeybindTriggerEvent e) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (getPlayersWithPower().contains(e.getPlayer())) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getType(), layer)) {
                    if (Cooldown.isInCooldown(e.getPlayer(), power)) continue;
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), e.getPlayer())) {
                            ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(e.getPlayer(), power.getTag());
                            org.bukkit.inventory.Inventory vault = power.getEnumValueOrDefault("container_type", ContainerType.class, ContainerType.DROPPER).createInventory(e.getPlayer(), Utils.createIfPresent(power.getStringOrDefault("title", "container.inventory")));
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
                        dropItems(power, e.getPlayer());
                    }
                }
            }
        }
    }

    private void dropItems(Power power, Player p) {
        ArrayList<ItemStack> vaultItems = InventorySerializer.getItems(p, power.getTag());
        for (ItemStack item : new ArrayList<>(vaultItems)) {
            if (item != null && item.getType() != Material.AIR && ConditionExecutor.testItem(power.getJsonObject("drop_on_death_filter"), item)) {
                p.getWorld().dropItemNaturally(p.getLocation(), item);
                vaultItems.remove(item);
            }
        }

        InventorySerializer.storeItems(vaultItems, p, power.getTag());
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

