package me.dueris.genesismc.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.dueris.genesismc.registry.registries.Layer;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.player.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ScreenNavigator implements Listener {
    public static HashMap<Player, Layer> inChoosingLayer = new HashMap<>();
    public static ArrayList<Player> orbChoosing = new ArrayList<>();
    public static HashMap<Layer, List<ChoosingPage>> layerPages = new HashMap<>();
    public static Object2IntMap<Player> currentDisplayingPage = new Object2IntOpenHashMap<>();
    public static final ItemStack NEXT_ITEMSTACK;
    public static final ItemStack BACK_ITEMSTACK;
    public static HashMap<ItemStack, BiConsumer<Player, Layer>> itemActions = new HashMap<>();
    public static ArrayList<HumanEntity> tickCooldown = new ArrayList<>();

    static {
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nmeta = next.getItemMeta();
        nmeta.setDisplayName("Next Origin");
        next.setItemMeta(nmeta);
        NEXT_ITEMSTACK = next;

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bmeta = next.getItemMeta();
        bmeta.setDisplayName("Previous Origin");
        back.setItemMeta(bmeta);
        BACK_ITEMSTACK = back;

        itemActions.put(NEXT_ITEMSTACK, (player, layer) -> {
            if (currentDisplayingPage.getInt(player) >= (layerPages.get(layer).size() - 1)) { // Go to beginning
                currentDisplayingPage.put(player, 0);
                player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(0).createDisplay(player, layer));
            } else { // Increment
                int nextPage = currentDisplayingPage.getInt(player) + 1;
                currentDisplayingPage.put(player, nextPage);
                player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(nextPage).createDisplay(player, layer));
            }
        });
        itemActions.put(BACK_ITEMSTACK, (player, layer) -> {
            if (currentDisplayingPage.getInt(player) <= 0) { // Set to top/end
                int top = layerPages.get(layer).size() - 1;
                currentDisplayingPage.put(player, top);
                player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(top).createDisplay(player, layer));
            } else {
                int nextPage = currentDisplayingPage.getInt(player) - 1;
                currentDisplayingPage.put(player, nextPage);
                player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(nextPage).createDisplay(player, layer));
            }
        });
    }

    public static void open(Player player, Layer layer, boolean inOrbChoosing) {
        inChoosingLayer.put(player, layer);
        currentDisplayingPage.put(player, 0);
        if (inOrbChoosing) orbChoosing.add(player);

        @NotNull Inventory gui = Bukkit.createInventory(player.getBukkitEntity(), 54,
            Component.text(layer.isPresent("gui_title") ? layer.getString("gui_title") :
            "Choosing - " + (layer.isPresent("name") ? layer.getString("name") : layer.getTag()))
        );

        gui.setContents(layerPages.get(layer).get(currentDisplayingPage.getInt(player)).createDisplay(player, layer));
        player.getBukkitEntity().openInventory(gui);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (inChoosingLayer.containsKey(getCraftPlayer(e.getPlayer()))) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!inChoosingLayer.containsKey(getCraftPlayer(e.getPlayer()))) return; // Check again just in case
                    e.getPlayer().openInventory(e.getInventory());
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }
    }

    private Player getCraftPlayer(HumanEntity p) {
        return ((CraftPlayer)p).getHandle();
    }

    @EventHandler
    public void clickAction(InventoryClickEvent e) {
        if (inChoosingLayer.containsKey(getCraftPlayer(e.getWhoClicked())) && e.getCurrentItem() != null) {
            e.setCancelled(true);
            if (tickCooldown.contains(e.getWhoClicked())) return;
            e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
            if (layerPages.get(inChoosingLayer.get(getCraftPlayer(e.getWhoClicked()))).isEmpty()) return;
            ChoosingPage page = layerPages.get(inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())))
                .get(currentDisplayingPage.getInt(getCraftPlayer(e.getWhoClicked())));
            if (e.getCurrentItem().isSimilar(page.getChoosingStack(((CraftPlayer)e.getWhoClicked()).getHandle()))) {
                page.onChoose(((CraftPlayer)e.getWhoClicked()).getHandle(), inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inChoosingLayer.remove(getCraftPlayer(e.getWhoClicked()));
                        e.getWhoClicked().closeInventory();
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 1);
            } else {
                if (itemActions.containsKey(e.getCurrentItem())) {
                    itemActions.get(e.getCurrentItem()).accept(((CraftPlayer)e.getWhoClicked()).getHandle(), inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())));
                }
            }

            tickCooldown.add(e.getWhoClicked());
            new BukkitRunnable() {
                @Override
                public void run() {
                    tickCooldown.remove(e.getWhoClicked());
                }
            }.runTaskLater(GenesisMC.getPlugin(), 3);
        }
    }

    public static void open(org.bukkit.entity.Player player, Layer layer, boolean inOrbChoosing) {
        open(((CraftPlayer)player).getHandle(), layer, inOrbChoosing);
    }
}
