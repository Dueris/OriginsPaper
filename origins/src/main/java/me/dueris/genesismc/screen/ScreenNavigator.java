package me.dueris.genesismc.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.event.OrbInteractEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.storage.OriginConfiguration;
import me.dueris.genesismc.util.Util;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import static org.bukkit.Bukkit.getServer;

public class ScreenNavigator implements Listener {
	public static final ItemStack NEXT_ITEMSTACK;
	public static final ItemStack BACK_ITEMSTACK;
	public static HashMap<Player, Layer> inChoosingLayer = new HashMap<>();
	public static ArrayList<Player> orbChoosing = new ArrayList<>();
	public static HashMap<Layer, List<ChoosingPage>> layerPages = new HashMap<>();
	public static Object2IntMap<Player> currentDisplayingPage = new Object2IntOpenHashMap<>();
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
			Component.text(!layer.getGuiTitle().isEmpty() ? layer.getGuiTitle().getStringOrDefault("choose_origin", "Choosing - " + layer.getTag()) :
				"Choosing - " + (!layer.getName().equalsIgnoreCase("craftapoli.layer.name.not_found") ? layer.getName() : layer.getTag()))
		);

		gui.setContents(layerPages.get(layer).get(currentDisplayingPage.getInt(player)).createDisplay(player, layer));
		player.getBukkitEntity().openInventory(gui);
	}

	public static void open(org.bukkit.entity.Player player, Layer layer, boolean inOrbChoosing) {
		open(((CraftPlayer) player).getHandle(), layer, inOrbChoosing);
	}

	private static boolean isSimilarEnough(ItemStack a, ItemStack b) {
		return a.getType().equals(b.getType()) && a.getItemMeta().displayName().equals(b.getItemMeta().displayName());
	}

	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
		if (inChoosingLayer.containsKey(getCraftPlayer(e.getPlayer()))) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (e.getInventory().getType().equals(InventoryType.CRAFTING))
						return; // Fixes IllegalArgumentException on player leave
					if (!inChoosingLayer.containsKey(getCraftPlayer(e.getPlayer()))) return; // Check again just in case
					e.getPlayer().openInventory(e.getInventory());
				}
			}.runTaskLater(GenesisMC.getPlugin(), 1);
		}
	}

	private Player getCraftPlayer(HumanEntity p) {
		return ((CraftPlayer) p).getHandle();
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
			if (isSimilarEnough(e.getCurrentItem(), page.getChoosingStack(((CraftPlayer) e.getWhoClicked()).getHandle()))) { // 1.20.5 bug with ItemFlags
				page.onChoose(((CraftPlayer) e.getWhoClicked()).getHandle(), inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())));
				new BukkitRunnable() {
					@Override
					public void run() {
						inChoosingLayer.remove(getCraftPlayer(e.getWhoClicked()));
						e.getWhoClicked().closeInventory();
					}
				}.runTaskLater(GenesisMC.getPlugin(), 1);
			} else {
				if (itemActions.containsKey(e.getCurrentItem())) {
					itemActions.get(e.getCurrentItem()).accept(((CraftPlayer) e.getWhoClicked()).getHandle(), inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())));
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

	@EventHandler
	public void onOrbClick(PlayerInteractEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		if (OriginConfiguration.getConfiguration().getBoolean("orb-of-origins")) {
			if (e.getAction().isRightClick()) {
				if (e.getItem() != null) {
					if ((isSimilarEnough(e.getItem(), OrbOfOrigins.orb)) && e.getItem().getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origins"))) {
						if (!((CraftPlayer) p).getHandle().getAbilities().instabuild) {
							Util.consumeItem(e.getItem());
						}
						for (Layer layer : CraftApoli.getLayersFromRegistry()) {
							PowerHolderComponent.setOrigin(p, layer, CraftApoli.emptyOrigin());
						}
						OrbInteractEvent event = new OrbInteractEvent(p);
						getServer().getPluginManager().callEvent(event);
					}
				}
			}
		}
	}
}
