package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingForced extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if(originid == 0){
                if(!p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins")){
                    @NotNull Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu");
                        mainmenu.setContents(GenesisMainMenuContents());
                        p.openInventory(mainmenu);

                }
            }

            if (!p.getScoreboardTags().contains("chosen")) {
                p.addScoreboardTag("choosing");
            }
            if (p.getScoreboardTags().contains("choosing")) {
                if (p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") || p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") || p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins") ) {
                    p.setGameMode(GameMode.SPECTATOR);
                } else {
                    if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                        p.setGameMode(p.getPreviousGameMode());

                    } else {
                        p.setGameMode(p.getGameMode());
                    }

                }


            }


        }
    }
}
