package me.dueris.genesismc;

import me.dueris.genesismc.factory.powers.OriginsMod.player.inventory.InventoryUtils;
import me.dueris.genesismc.utils.BukkitColour;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class DataContainer implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (e.getView().getTitle().startsWith("Shulker Inventory")) {

            ArrayList<ItemStack> prunedItems = new ArrayList<>();

            Arrays.stream(e.getInventory().getContents())
                    .filter(itemStack -> {
                        return itemStack != null;
                    })
                    .forEach(itemStack -> prunedItems.add(itemStack));

            Player target = Bukkit.getPlayer(e.getView().getTitle().split(":")[1].substring(1));
            if (target == null) {
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "errors.inventorySaveFail").replace("%player%", e.getView().getTitle().split(":")[1].substring(1))).color(TextColor.fromHexString(BukkitColour.RED)));
                return;
            }
            InventoryUtils.storeItems(prunedItems, target);
        }

    }

}