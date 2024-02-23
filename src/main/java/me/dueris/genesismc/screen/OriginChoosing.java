package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OrbInteractEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.ModifyPlayerSpawnPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.SendCharts;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static me.dueris.genesismc.content.OrbOfOrigins.orb;
import static me.dueris.genesismc.factory.powers.ApoliPower.phasing;
import static me.dueris.genesismc.util.ColorConstants.AQUA;
import static org.bukkit.Bukkit.getServer;

public class OriginChoosing implements Listener {

    public static HashMap<Player, Layer> choosing = new HashMap<>();

    @EventHandler
    public void onOrbClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (GenesisConfigs.getMainConfig().getString("orb-of-origins").equalsIgnoreCase("true")) {
            if (e.getAction().isRightClick()) {
                ItemStack item = orb;
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(item)) {
                        if(!((CraftPlayer)p).getHandle().getAbilities().instabuild){
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
            List<Origin> origins = CraftApoli.getOriginsFromRegistry();
            List<Layer> layers = CraftApoli.getLayersFromRegistry();
            Random random = new Random();
            Origin origin = origins.get(random.nextInt(origins.size()));
            for (Layer layer : layers) {
                OriginPlayerAccessor.setOrigin(p, layer, origin);
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "misc.randomOrigins").replace("%layer%", layer.getTag()).replace("%originName%", origin.getName())).color(TextColor.fromHexString(AQUA)));
            }

            e.setCancelled(true);
            p.closeInventory();

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 2);
            p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
            p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 6);
            p.setCustomNameVisible(false);
            p.setHealthScaled(false);

            OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
            getServer().getPluginManager().callEvent(chooseEvent);
            OriginChangeEvent Event = new OriginChangeEvent(p, origin);
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
                ModifyPlayerSpawnPower power = new ModifyPlayerSpawnPower();
                power.runHandle(p);
                OriginChangeEvent event = new OriginChangeEvent(p, origin);
                event.callEvent();
            }
        }
    }

}
