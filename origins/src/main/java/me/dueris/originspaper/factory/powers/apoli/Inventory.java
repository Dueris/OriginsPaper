package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.KeybindTriggerEvent;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.ContainerType;
import me.dueris.originspaper.factory.data.types.JsonKeybind;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.KeybindUtil;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.entity.PlayerManager;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
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

public class Inventory extends PowerType implements KeyedPower {

	private final String title;
	private final ContainerType containerType;
	private final boolean dropOnDeath;
	private final FactoryJsonObject dropOnDeathFilter;
	private final boolean recoverable;
	private final JsonKeybind keybind;

	public Inventory(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String title, ContainerType containerType, boolean dropOnDeath, FactoryJsonObject dropOnDeathFilter, boolean recoverable, FactoryElement key) {
		super(name, description, hidden, condition, loading_priority);
		this.title = title;
		this.containerType = containerType;
		this.dropOnDeath = dropOnDeath;
		this.dropOnDeathFilter = dropOnDeathFilter;
		this.recoverable = recoverable;
		this.keybind = JsonKeybind.createJsonKeybind(key);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("inventory"))
			.add("title", String.class, "container.inventory")
			.add("container_type", ContainerType.class, ContainerType.DROPPER)
			.add("drop_on_death", boolean.class, false)
			.add("drop_on_death_filter", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("recoverable", boolean.class, true)
			.add("key", FactoryElement.class, new FactoryElement(new Gson().fromJson("{\"key\": \"key.origins.primary_active\"}", JsonElement.class)));
	}

	public static void saveInNbtIO(String tag, String data, @NotNull Player player) {
//        ServerPlayer p = ((CraftPlayer) player).getHandle();
//        CompoundTag compoundRoot = p.saveWithoutId(new CompoundTag());
//        if (!compoundRoot.contains("OriginData")) {
//            compoundRoot.put("OriginData", new CompoundTag());
//        }
//        CompoundTag originData = compoundRoot.getCompound("OriginData");
//        if (!originData.contains("InventoryData")) {
//            originData.put("InventoryData", new CompoundTag());
//        }
//        CompoundTag inventoryData = originData.getCompound("InventoryData");
//        inventoryData.putString(tag, data);
//        p.load(compoundRoot);
		PersistentDataContainer container = player.getPersistentDataContainer();
		container.set(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("inventorydata_" + format(tag))), PersistentDataType.STRING, data);
		player.saveData();
	}

	private static String getInNbtIO(String tag, @NotNull Player player) {
//        ServerPlayer p = ((CraftPlayer) player).getHandle();
//        CompoundTag compoundRoot = p.saveWithoutId(new CompoundTag());
//        if (!compoundRoot.contains("OriginData")) {
//            compoundRoot.put("OriginData", new CompoundTag());
//        }
//        CompoundTag originData = compoundRoot.getCompound("OriginData");
//        if (!originData.contains("InventoryData")) {
//            originData.put("InventoryData", new CompoundTag());
//            return "";
//        } else {
//            return originData.getCompound("InventoryData").getString(tag);
//        }
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

	public static void storeItems(@NotNull List<ItemStack> items, @NotNull Player p, String tag) {
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

	public static @NotNull ArrayList<ItemStack> getItems(Player p, String tag) {
		ArrayList<ItemStack> items = new ArrayList<>();

		String encodedItems = getInNbtIO(tag, p);

		if (!encodedItems.isEmpty()) {
			byte[] rawData = Base64.getDecoder().decode(encodedItems);

			try {
				ByteArrayInputStream io = new ByteArrayInputStream(rawData);
				BukkitObjectInputStream in = new BukkitObjectInputStream(io);

				int itemsCount = in.readInt();

				for (int i = 0; i < itemsCount; i++) {
					ItemStack item = (ItemStack) in.readObject();
					// Add AIR for empty slots
					items.add(Objects.requireNonNullElseGet(item, () -> new ItemStack(Material.AIR)));
				}

				in.close();
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}

		return items;
	}

	@Override
	public void bootstrapUnapply(Player player) {
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
	public void keytrigger(@NotNull KeybindTriggerEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (isActive(e.getPlayer())) {
				if (KeybindUtil.isKeyActive(getJsonKey().key(), e.getPlayer())) {
					ArrayList<ItemStack> vaultItems = getItems(e.getPlayer(), getTag());
					org.bukkit.inventory.Inventory vault = containerType.createInventory(e.getPlayer(), Util.createIfPresent(title));
					vaultItems.forEach(vault::addItem);
					e.getPlayer().openInventory(vault);
				}
			}
		}
	}

	@EventHandler
	public void deathTIMEEE(@NotNull PlayerDeathEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (dropOnDeath) {
				dropItems(e.getPlayer());
			}
		}
	}

	private void dropItems(Player p) {
		ArrayList<ItemStack> vaultItems = getItems(p, getTag());
		for (ItemStack item : new ArrayList<>(vaultItems)) {
			if (item != null && item.getType() != Material.AIR && ConditionExecutor.testItem(dropOnDeathFilter, item)) {
				p.getWorld().dropItemNaturally(p.getLocation(), item);
				vaultItems.remove(item);
			}
		}

		storeItems(vaultItems, p, getTag());
	}

	@EventHandler
	public void onInventoryClose(@NotNull InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();

		for (Inventory power : PowerHolderComponent.getPowers(p, Inventory.class)) {
			if (matches(e.getView(), power)) {
				ArrayList<ItemStack> prunedItems = new ArrayList<>();

				Arrays.stream(e.getInventory().getContents())
					.filter(Objects::nonNull)
					.forEach(prunedItems::add);
				storeItems(prunedItems, p, power.getTag());
			}
		}

	}

	private boolean matches(@NotNull InventoryView inventory, @NotNull Inventory power) {
		String title = power.getTitle();
		return inventory.getTitle().equalsIgnoreCase(title) && inventory.getTopInventory().getType().equals(power.getContainerType().getBukkit());
	}

	@Override
	public JsonKeybind getJsonKey() {
		return keybind;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public boolean isRecoverable() {
		return recoverable;
	}

	public boolean isDropOnDeath() {
		return dropOnDeath;
	}

	public FactoryJsonObject getDropOnDeathFilter() {
		return dropOnDeathFilter;
	}

	public String getTitle() {
		return title;
	}
}

