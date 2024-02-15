package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.screen.contents.ScreenContent;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import static me.dueris.genesismc.screen.OriginChoosing.choosing;

public class GuiTicker extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                try {
                    if (OriginPlayerAccessor.hasOrigin(p, CraftApoli.nullOrigin().getTag())) {
                        String openInventoryTitle = p.getOpenInventory().getTitle();
                        OriginContainer ori = OriginPlayerAccessor.getOrigin(p, layer);
                        if (!openInventoryTitle.startsWith("Choosing Menu") && !openInventoryTitle.startsWith("Custom Origins") && !openInventoryTitle.startsWith("Custom Origin") && !openInventoryTitle.startsWith("Origin")) {
                            if (OriginPlayerAccessor.getOrigin(p, layer).getTag().equals(CraftApoli.nullOrigin().getTag())) {
                                if (ori.getOriginFile().get("condition") != null || ori.getOriginFile().get("conditions") != null) {
                                    if (ConditionExecutor.entityCondition.check((JSONObject) ori.getOriginFile().get("condition"), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null).isPresent()) {
                                        choosing.put(p, layer);
                                        Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                        mainmenu.setContents(ScreenContent.GenesisMainMenuContents(p));
                                        p.openInventory(mainmenu);
                                    }
                                } else {
                                    choosing.put(p, layer);
                                    Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                    mainmenu.setContents(ScreenContent.GenesisMainMenuContents(p));
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
