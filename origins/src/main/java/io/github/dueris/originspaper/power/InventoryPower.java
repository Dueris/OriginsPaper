package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ContainerType;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.KeybindUtil;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.entity.PlayerManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class InventoryPower extends PowerType {
	private final String title;
	private final ContainerType containerType;
	private final boolean dropOnDeath;
	private final ConditionFactory<Tuple<Level, net.minecraft.world.item.ItemStack>> dropOnDeathFilter;
	private final boolean recoverable;
	private final Keybind keybind;

	public InventoryPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						  String title, ContainerType containerType, boolean dropOnDeath, ConditionFactory<Tuple<Level, net.minecraft.world.item.ItemStack>> dropOnDeathFilter, Keybind keybind, boolean recoverable) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.title = title;
		this.containerType = containerType;
		this.dropOnDeath = dropOnDeath;
		this.dropOnDeathFilter = dropOnDeathFilter;
		this.recoverable = recoverable;
		this.keybind = keybind;
	}

	public static void saveInNbtIO(String tag, String data, @NotNull org.bukkit.entity.Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		container.set(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("inventorydata_" + format(tag))), PersistentDataType.STRING, data);
		player.saveData();
	}

	private static String getInNbtIO(String tag, @NotNull org.bukkit.entity.Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		NamespacedKey formattedTag = CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("inventorydata_" + format(tag)));
		if (!container.has(formattedTag, PersistentDataType.STRING)) {
			return "";
		}
		return container.get(formattedTag, PersistentDataType.STRING);
	}

	private static @NotNull String format(@NotNull String tag) {
		return tag.replace(" ", "_").replace(":", "_").replace("/", "_").replace("\\", "_");
	}

	public static void storeItems(@NotNull List<ItemStack> items, @NotNull org.bukkit.entity.Player p, String tag) {
		PersistentDataContainer data = p.getPersistentDataContainer();

		if (items.size() == 0) {
			saveInNbtIO(tag, "", p);
		} else {
			try {
				ByteArrayOutputStream io = new ByteArrayOutputStream();
				BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

				os.writeInt(items.size());

				for (ItemStack item : items) {
					if (item != null) {
						os.writeObject(item.clone()); // Clone the item to keep the original item intact
					} else {
						os.writeObject(null); // Write null for empty slots
					}
				}

				os.flush();

				byte[] rawData = io.toByteArray();

				String encodedData = Base64.getEncoder().encodeToString(rawData);

				saveInNbtIO(tag, encodedData, p);

				os.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static @NotNull ArrayList<ItemStack> getItems(org.bukkit.entity.Player p, String tag) {
		ArrayList<ItemStack> items = new ArrayList<>();

		String encodedItems = getInNbtIO(tag, p);

		if (!encodedItems.isEmpty()) {
			byte[] rawData = Base64.getDecoder().decode(encodedItems);

			try {
				ByteArrayInputStream io = new ByteArrayInputStream(rawData);
				BukkitObjectInputStream in = new BukkitObjectInputStream(io);

				int itemsCount = in.readInt();

				for (int i = 0; i < itemsCount; i++) {
					org.bukkit.inventory.ItemStack item = (org.bukkit.inventory.ItemStack) in.readObject();
					// Add AIR for empty slots
					items.add(Objects.requireNonNullElseGet(item, () -> new org.bukkit.inventory.ItemStack(Material.AIR)));
				}

				in.close();
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}

		return items;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("inventory"))
			.add("title", SerializableDataTypes.STRING, "container.inventory")
			.add("container_type", SerializableDataTypes.enumValue(ContainerType.class), ContainerType.DROPPER)
			.add("drop_on_death", SerializableDataTypes.BOOLEAN, false)
			.add("drop_on_death_filter", ApoliDataTypes.ITEM_CONDITION, null)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND)
			.add("recoverable", SerializableDataTypes.BOOLEAN, true);
	}

	@Override
	public void onLost(Player p) {
		org.bukkit.entity.Player player = (org.bukkit.entity.Player) p.getBukkitEntity();
		if (!PlayerManager.playersLeaving.contains(player)) {
			ArrayList<ItemStack> vaultItems = getItems(player, getTag());
			for (ItemStack item : new ArrayList<>(vaultItems)) {
				if (item != null && item.getType() != Material.AIR) {
					if (recoverable) {
						player.getWorld().dropItemNaturally(player.getLocation(), item);
					}
					vaultItems.remove(item);
				}
			}

			storeItems(new ArrayList<>(), player, getTag());
		}
	}

	@EventHandler
	public void onUse(@NotNull KeybindTriggerEvent e) {
		if (getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle())) {
			if (isActive(((CraftPlayer) e.getPlayer()).getHandle())) {
				if (KeybindUtil.isKeyActive(keybind.key(), e.getPlayer())) {
					ArrayList<ItemStack> vaultItems = getItems(e.getPlayer(), getTag());
					org.bukkit.inventory.Inventory vault = containerType.createInventory(Util.createIfPresent(title));
					vaultItems.forEach(vault::addItem);
					e.getPlayer().openInventory(vault);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(@NotNull PlayerDeathEvent e) {
		if (getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle())) {
			if (dropOnDeath) {
				dropItems(e.getPlayer());
			}
		}
	}

	private void dropItems(org.bukkit.entity.Player p) {
		ArrayList<ItemStack> vaultItems = getItems(p, getTag());
		for (ItemStack item : new ArrayList<>(vaultItems)) {
			if (item != null && item.getType() != Material.AIR && dropOnDeathFilter.test(new Tuple<>(((CraftPlayer) p).getHandle().level(), CraftItemStack.unwrap(item)))) {
				p.getWorld().dropItemNaturally(p.getLocation(), item);
				vaultItems.remove(item);
			}
		}

		storeItems(vaultItems, p, getTag());
	}

	@EventHandler
	public void onInventoryClose(@NotNull InventoryCloseEvent e) {
		org.bukkit.entity.Player p = (org.bukkit.entity.Player) e.getPlayer();

		for (InventoryPower power : PowerHolderComponent.getPowers(p, InventoryPower.class)) {
			if (matches(e.getView(), power)) {
				ArrayList<ItemStack> prunedItems = new ArrayList<>();

				Arrays.stream(e.getInventory().getContents())
					.filter(Objects::nonNull)
					.forEach(prunedItems::add);
				storeItems(prunedItems, p, power.getTag());
			}
		}

	}

	private boolean matches(@NotNull InventoryView inventory, @NotNull InventoryPower power) {
		String title = power.title;
		return inventory.getTitle().equalsIgnoreCase(title) && inventory.getTopInventory().getType().equals(power.containerType.getBukkit());
	}
}
