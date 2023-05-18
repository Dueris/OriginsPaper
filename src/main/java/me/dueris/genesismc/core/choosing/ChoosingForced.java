package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingForced extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if(origintag.equalsIgnoreCase("genesis:origin-null") || p.getScoreboardTags().contains("choosing")){
                if(!p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origin")){
                    @NotNull Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu");
                    mainmenu.setContents(GenesisMainMenuContents(p));
                    p.openInventory(mainmenu);

                }
            }

            if (!p.getScoreboardTags().contains("chosen")) {
                p.addScoreboardTag("choosing");
            }
            if (p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") || p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") || p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins") || p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origin")) {
                p.setInvulnerable(true);
            } else p.setInvulnerable(false);
        }
    }
}
