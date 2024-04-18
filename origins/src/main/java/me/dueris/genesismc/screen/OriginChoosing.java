package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OrbInteractEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.ModifyPlayerSpawnPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.SendCharts;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.dueris.genesismc.content.OrbOfOrigins.orb;
import static me.dueris.genesismc.factory.powers.ApoliPower.phasing;
import static org.bukkit.Bukkit.getServer;

public class OriginChoosing implements Listener {

    public static HashMap<Player, Layer> choosing = new HashMap<>();
    public static List<Player> orbChoosing = new ArrayList<>();

    @EventHandler
    public void onOrbClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (GenesisConfigs.getMainConfig().getString("orb-of-origins").equalsIgnoreCase("true")) {
            if (e.getAction().isRightClick()) {
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(orb)) {
                        if (!((CraftPlayer) p).getHandle().getAbilities().instabuild) {
                            Utils.consumeItem(e.getItem());
                        }
                        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                            OriginPlayerAccessor.setOrigin(p, layer, CraftApoli.nullOrigin());
                        }
                        OrbInteractEvent event = new OrbInteractEvent(p);
                        getServer().getPluginManager().callEvent(event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void orbInteractEvent(OrbInteractEvent e) {
        orbChoosing.add(e.getPlayer());
    }

    @EventHandler
    public void finishChoosing(OriginChooseEvent e) {
        orbChoosing.remove(e.getPlayer());
    }

    @EventHandler
    public void onOrbRandom(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (e.getView().getTitle().startsWith("Choosing Menu")) {
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "orb");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) == null)
                return;
            if (!Objects.equals(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "orb"))
                return;
            Player p = (Player) e.getWhoClicked();
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);

            Layer layer = choosing.get(p);
            List<Origin> origins = layer.getRandomOrigins();
            if (origins.isEmpty()) {
                p.sendMessage(ChatColor.RED + "Random is not allowed on this layer!");
                return;
            }
            Origin origin = origins.get(new Random().nextInt(origins.size()));

            OriginPlayerAccessor.setOrigin(p, layer, origin);
            p.closeInventory();

            OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
            getServer().getPluginManager().callEvent(chooseEvent);
            OriginChangeEvent Event = new OriginChangeEvent(p, origin, true);
            getServer().getPluginManager().callEvent(Event);

            SendCharts.originPopularity(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OriginChoose(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() == Material.MAGMA_CREAM) return;
            if (e.getCurrentItem().getItemMeta() == null) return;

            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originChoose"), PersistentDataType.INTEGER) != null) {
                String originTag = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originTag"), PersistentDataType.STRING);
                Origin origin = CraftApoli.getOrigin(originTag);
                Player p = (Player) e.getWhoClicked();

                ScreenConstants.setAttributesToDefault(p);
                OriginPlayerAccessor.setOrigin(p, choosing.get(p), origin);
                choosing.remove(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                        if (phasing.contains(p)) {
                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, true);
                        } else {
                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
                        }
                        ScreenConstants.DefaultChoose(p);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 1);
                if (p.getBedSpawnLocation() != null) {
                    p.teleport(p.getBedSpawnLocation());
                } else {
                    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, "apoli:modify_player_spawn", layer)) {
                            new ModifyPlayerSpawnPower().teleportToModifiedSpawn(((CraftPlayer) p).getHandle(), power);
                        }
                    }
                }
                OriginChangeEvent event = new OriginChangeEvent(p, origin, false);
                event.callEvent();
            }
        }
    }

}
