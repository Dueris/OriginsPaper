package io.github.dueris.originspaper.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.content.OrbOfOrigins;
import io.github.dueris.originspaper.event.OrbInteractEvent;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
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
import org.bukkit.persistence.PersistentDataType;
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
	public static HashMap<Player, OriginLayer> inChoosingLayer = new HashMap<>();
	public static ArrayList<Player> orbChoosing = new ArrayList<>();
	public static HashMap<OriginLayer, List<ChoosingPage>> layerPages = new HashMap<>();
	public static Object2IntMap<Player> currentDisplayingPage = new Object2IntOpenHashMap();
	public static HashMap<ItemStack, BiConsumer<Player, OriginLayer>> itemActions = new HashMap<>();
	public static ArrayList<HumanEntity> tickCooldown = new ArrayList<>();

	static {
		ItemStack next = new ItemStack(Material.ARROW);
		ItemMeta nmeta = next.getItemMeta();
		nmeta.displayName(
			Component.text("Next Origin")
				.decoration(TextDecoration.ITALIC, false)
		);
		next.setItemMeta(nmeta);
		NEXT_ITEMSTACK = next;
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta bmeta = next.getItemMeta();
		bmeta.displayName(
			Component.text("Previous Origin")
				.decoration(TextDecoration.ITALIC, false)
		);
		back.setItemMeta(bmeta);
		BACK_ITEMSTACK = back;
		itemActions.put(NEXT_ITEMSTACK, (player, layer) -> {
			if (currentDisplayingPage.getInt(player) >= layerPages.get(layer).size() - 1) {
				currentDisplayingPage.put(player, 0);
				player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(0).createDisplay(player, layer));
			} else {
				int nextPage = currentDisplayingPage.getInt(player) + 1;
				currentDisplayingPage.put(player, nextPage);
				player.getBukkitEntity().getOpenInventory().getTopInventory().setContents(layerPages.get(layer).get(nextPage).createDisplay(player, layer));
			}
		});
		itemActions.put(BACK_ITEMSTACK, (player, layer) -> {
			if (currentDisplayingPage.getInt(player) <= 0) {
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

	public static void open(Player player, OriginLayer layer, boolean inOrbChoosing) {
		inChoosingLayer.put(player, layer);
		currentDisplayingPage.put(player, 0);
		if (inOrbChoosing) {
			orbChoosing.add(player);
		}

		Inventory gui = Bukkit.createInventory(
			player.getBukkitEntity(),
			54,
			Component.text(
				layer.getGuiTitle() != null
					? PlainTextComponentSerializer.plainText().serialize(layer.getGuiTitle().chooseOrigin())
					: "Choosing - " + (layer.getTag())
			)
		);
		gui.setContents(layerPages.get(layer).get(currentDisplayingPage.getInt(player)).createDisplay(player, layer));
		OriginsPaper.scheduler.parent.scheduleMainThreadCall(() -> player.getBukkitEntity().openInventory(gui));
	}

	public static void open(org.bukkit.entity.Player player, OriginLayer layer, boolean inOrbChoosing) {
		open(((CraftPlayer) player).getHandle(), layer, inOrbChoosing);
	}

	private static boolean isSimilarEnough(ItemStack a, ItemStack b, boolean cD) {
		if (b == null && a != null) {
			return false;
		} else if (a == null && b != null) {
			return false;
		} else {
			return a == null && b == null || a.getType().equals(b.getType())
				&& (!cD || a.displayName() != null && b.displayName() != null && a.getItemMeta().displayName().equals(b.getItemMeta().displayName()));
		}
	}

	@EventHandler
	public void inventoryClose(final @NotNull InventoryCloseEvent e) {
		if (inChoosingLayer.containsKey(this.getCraftPlayer(e.getPlayer()))) {
			(new BukkitRunnable() {
				public void run() {
					if (!e.getInventory().getType().equals(InventoryType.CRAFTING)) {
						if (ScreenNavigator.inChoosingLayer.containsKey(ScreenNavigator.this.getCraftPlayer(e.getPlayer()))) {
							e.getPlayer().openInventory(e.getInventory());
						}
					}
				}
			}).runTaskLater(OriginsPaper.getPlugin(), 1L);
		}
	}

	private Player getCraftPlayer(HumanEntity p) {
		return ((CraftPlayer) p).getHandle();
	}

	@EventHandler
	public void clickAction(final @NotNull InventoryClickEvent e) {
		if (inChoosingLayer.containsKey(this.getCraftPlayer(e.getWhoClicked())) && e.getCurrentItem() != null) {
			e.setCancelled(true);
			if (tickCooldown.contains(e.getWhoClicked())) return;
			e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
			if (layerPages.get(inChoosingLayer.get(getCraftPlayer(e.getWhoClicked()))).isEmpty()) return;
			ChoosingPage page = layerPages.get(inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())))
				.get(currentDisplayingPage.getInt(getCraftPlayer(e.getWhoClicked())));
			if (isSimilarEnough(e.getCurrentItem(), page.getChoosingStack(((CraftPlayer) e.getWhoClicked()).getHandle()), true)) { // 1.20.5 bug with ItemFlags
				page.onChoose(((CraftPlayer) e.getWhoClicked()).getHandle(), inChoosingLayer.get(getCraftPlayer(e.getWhoClicked())));
				new BukkitRunnable() {
					@Override
					public void run() {
						ScreenNavigator.inChoosingLayer.remove(ScreenNavigator.this.getCraftPlayer(e.getWhoClicked()));
						e.getWhoClicked().closeInventory();
					}
				}.runTaskLater(OriginsPaper.getPlugin(), 1);
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
			}.runTaskLater(OriginsPaper.getPlugin(), 3);
		}
	}

	@EventHandler
	public void onOrbClick(@NotNull PlayerInteractEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		if (OriginConfiguration.getConfiguration().getBoolean("orb-of-origins")) {
			if (e.getAction().isRightClick()) {
				if (e.getItem() != null) {
					if ((isSimilarEnough(e.getItem(), OrbOfOrigins.orb, false)) &&
						e.getItem().getItemMeta().getPersistentDataContainer().has(CraftNamespacedKey.fromMinecraft(OriginsPaper.identifier("origins"))) &&
						e.getItem().getItemMeta().getPersistentDataContainer().get(CraftNamespacedKey.fromMinecraft(OriginsPaper.identifier("origins")), PersistentDataType.STRING).equalsIgnoreCase("orb_of_origin")) {
						if (!((CraftPlayer) p).getHandle().getAbilities().instabuild) {
							Util.consumeItem(e.getItem());
						}
						OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).values().forEach(layer -> {
							PowerHolderComponent.unloadPowers(p, layer);
							PowerHolderComponent.setOrigin(p, layer, OriginsPaper.EMPTY_ORIGIN);
						});
						OrbInteractEvent event = new OrbInteractEvent(p);
						getServer().getPluginManager().callEvent(event);
					}
				}
			}
		}
	}
}
