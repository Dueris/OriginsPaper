package me.dueris.genesismc.choosing;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.HashMap;

import static me.dueris.genesismc.choosing.ChoosingMain.choosing;
import static me.dueris.genesismc.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class GuiTicker extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                try {
                    if (OriginPlayerUtils.hasOrigin(p, CraftApoli.nullOrigin().getTag())) {
                        String openInventoryTitle = p.getOpenInventory().getTitle();
                        OriginContainer ori = OriginPlayerUtils.getOrigin(p, layer);
                        if (!openInventoryTitle.startsWith("Choosing Menu") && !openInventoryTitle.startsWith("Custom Origins") && !openInventoryTitle.startsWith("Custom Origin") && !openInventoryTitle.startsWith("Origin")) {
                            if (OriginPlayerUtils.getOrigin(p, layer).getTag().equals(CraftApoli.nullOrigin().getTag())) {
                                if (ori.getOriginFile().get("condition") != null || ori.getOriginFile().get("conditions") != null) {
                                    if (ConditionExecutor.entityCondition.check((JSONObject) ori.getOriginFile().get("condition"), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null).isPresent()) {
                                        choosing.put(p, layer);
                                        Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                        mainmenu.setContents(GenesisMainMenuContents(p));
                                        p.openInventory(mainmenu);
                                    }
                                } else {
                                    choosing.put(p, layer);
                                    Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                    mainmenu.setContents(GenesisMainMenuContents(p));
                                    p.openInventory(mainmenu);
                                }

                            }
                        }
                    }

                    String openInventoryTitle = p.getOpenInventory().getTitle();
                    p.setInvulnerable(openInventoryTitle.startsWith("Origin - ") || openInventoryTitle.startsWith("Choosing Menu") || openInventoryTitle.startsWith("Custom Origins") || openInventoryTitle.startsWith("Expanded Origins") || openInventoryTitle.startsWith("Custom Origin"));
                } catch (Exception e) {
                    p.getPersistentDataContainer().remove(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"));
                }
            }
        }
    }
}
