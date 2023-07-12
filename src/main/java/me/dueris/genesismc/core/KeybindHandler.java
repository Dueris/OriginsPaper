package me.dueris.genesismc.core;

import me.dueris.genesismc.core.events.OriginKeybindExecuteEvent;
import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.launch_into_air;
import static me.dueris.genesismc.core.factory.powers.Powers.shulker_inventory;
import static me.dueris.genesismc.core.factory.powers.item.LaunchAir.*;
import static org.bukkit.Bukkit.getServer;

public class KeybindHandler implements Listener {

    public static void keybindTriggerMethod(Player p, PlayerSwapHandItemsEvent e) {
        OriginKeybindExecuteEvent event = new OriginKeybindExecuteEvent(p);
        getServer().getPluginManager().callEvent(event);

        PersistentDataContainer data = p.getPersistentDataContainer();
        if (shulker_inventory.contains(p)) {
            e.setCancelled(true);

            ArrayList<ItemStack> vaultItems = ShulkUtils.getItems(p);

            Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory");

            vaultItems.stream()
                    .forEach(itemStack -> vault.addItem(itemStack));

            p.openInventory(vault);

        }
//        if (phasing.contains(p)) {
//            e.setCancelled(true);
//            boolean phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
//            if (phantomid == false) {
//                if (p.getGameMode() != GameMode.SPECTATOR) {
//
//                    if (p.getFoodLevel() > 6) {
//                        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, true);
//                        p.sendActionBar(DARK_AQUA + "Activated Phasing Form");
//                        p.setSilent(true);
//                        p.setCollidable(false);
//
//                    } else {
//                        p.sendMessage(RED + "You must be able to sprint to switch forms");
//                    }
//
//                } else {
//                    p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
//                }
//            } else if (phantomid == true) {
//                if (p.getGameMode() != GameMode.SPECTATOR) {
//
//                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
//                    p.sendActionBar(DARK_AQUA + "Deactivated Phasing Form");
//                    p.setSilent(false);
//                    p.setCollidable(true);
//
//                } else {
//                    p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
//                }
//            } else {
//                p.sendMessage(RED + "Error: Switching could not be executed");
//            }
//        }
        if (launch_into_air.contains(p)) {
            if (p.isSneaking()) return;
            if (cooldownAfterElytrian.containsKey(p.getUniqueId())) return;
            cooldownBeforeElytrian.put(p.getUniqueId(), 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldownBeforeElytrian.replace(p.getUniqueId(), cooldownBeforeElytrian.get(p.getUniqueId()) + 1);
                    cooldownAfterElytrian.put(p.getUniqueId(), System.currentTimeMillis());
                    canLaunch.remove(p);
                    doLaunch(p);
                    p.setVelocity(new Vector(p.getVelocity().getX(), p.getVelocity().getY() + 1.7, p.getVelocity().getZ()));
                    this.cancel();
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 2L);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPressMainKey(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if (!GeyserApi.api().isBedrockPlayer(p.getUniqueId())) {

                keybindTriggerMethod(p, e);

            }
        } else {
            keybindTriggerMethod(p, e);
        }
    }

}
