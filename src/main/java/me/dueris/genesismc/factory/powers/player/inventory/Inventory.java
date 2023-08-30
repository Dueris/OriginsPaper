package me.dueris.genesismc.factory.powers.player.inventory;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.BukkitColour;
import me.dueris.genesismc.utils.OriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

import static me.dueris.genesismc.KeybindHandler.isKeyBeingPressed;

public class Inventory extends CraftPower implements CommandExecutor, Listener {

    public Inventory() {
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void MoveBackChange(OriginChangeEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!shulker_inventory.contains(p)) {
                        ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                        org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                        vaultItems.stream()
                                .forEach(itemStack -> vault.addItem(itemStack));
                        for (ItemStack item : vault.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                p.getWorld().dropItemNaturally(p.getLocation(), item);
                                vault.removeItem(item);
                            }
                        }
                        ArrayList<ItemStack> prunedItems = new ArrayList<>();

                        Arrays.stream(vault.getContents())
                                .filter(itemStack -> {
                                    return itemStack != null;
                                })
                                .forEach(itemStack -> prunedItems.add(itemStack));

                        InventoryUtils.storeItems(prunedItems, p);
                        vault.clear();
                        this.cancel();
                    }
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
        }

    }

    @EventHandler
    public void keytrigger(KeybindTriggerEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (getPowerArray().contains(e.getPlayer())) {
                ConditionExecutor executor = new ConditionExecutor();
                if (CooldownStuff.isPlayerInCooldown(e.getPlayer(), e.getKey())) return;
                if (executor.check("condition", "conditions", e.getPlayer(), origin, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:inventory").getKey().get("key").toString(), true)) {
                        ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(e.getPlayer());
                        org.bukkit.inventory.Inventory vault = Bukkit.createInventory(e.getPlayer(), InventoryType.valueOf(origin.getPowerFileFromType("origins:inventory").get("container_type", "chest").toUpperCase()), origin.getPowerFileFromType("origins:inventory").get("title", "inventory.container.title").replace("%player%", e.getPlayer().getName()));
                        vaultItems.stream()
                                .forEach(itemStack -> vault.addItem(itemStack));
                        e.getPlayer().openInventory(vault);
                    }
                } else {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @EventHandler
    public void deathTIMEEE(PlayerDeathEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (shulker_inventory.contains(e.getPlayer())) {
                Player p = e.getPlayer();
                if (origin.getPowerFileFromType("origins:inventory").getDropOnDeath()) {
                    ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                    org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                    vaultItems.stream()
                            .forEach(itemStack -> vault.addItem(itemStack));
                    for (ItemStack item : vault.getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            p.getWorld().dropItemNaturally(p.getLocation(), item);
                            vault.removeItem(item);
                        }
                    }
                    ArrayList<ItemStack> prunedItems = new ArrayList<>();

                    Arrays.stream(vault.getContents())
                            .filter(itemStack -> {
                                return itemStack != null;
                            })
                            .forEach(itemStack -> prunedItems.add(itemStack));

                    InventoryUtils.storeItems(prunedItems, p);
                    vault.clear();
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p) {
            //opens target players shulk inventory
            if (args.length >= 2 && p.hasPermission("genesism.origins.cmd.othershulk")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(target);
                    org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory: " + target.getName());
                    vaultItems.stream().forEach(itemStack -> vault.addItem(itemStack));
                    p.openInventory(vault);
                    return true;
                }
            }

            //opens own shulk inventory
            if (shulker_inventory.contains((Player) sender)) {
                ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory: " + p.getName());
                vaultItems.stream().forEach(itemStack -> vault.addItem(itemStack));
                p.openInventory(vault);
            } else {
                p.sendMessage(Component.text("powers.inventoryOpenPower").color(TextColor.fromHexString(BukkitColour.RED)));
            }
        }


        return true;
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:inventory";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return shulker_inventory;
    }
}

