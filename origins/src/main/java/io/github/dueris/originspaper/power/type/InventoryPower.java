package io.github.dueris.originspaper.power.type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.LangFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InventoryPower extends PowerType {
	private final String title;
	private final boolean dropOnDeath;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> dropOnDeathFilter;
	private final boolean recoverable;
	private final Keybind keybind;

	private final int containerSize;
	private final MenuConstructor containerScreen;
	private final HashMap<Entity, InventoryContainer> container;

	public InventoryPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						  String title, @NotNull ContainerType containerType, boolean dropOnDeath, ConditionTypeFactory<Tuple<Level, ItemStack>> dropOnDeathFilter, Keybind keybind, boolean recoverable) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.title = title;
		switch (containerType) {
			case DOUBLE_CHEST:
				containerSize = 54;
				this.containerScreen = (i, playerInventory, playerEntity) -> new ChestMenu(MenuType.GENERIC_9x6, i,
					playerInventory, buildContainer(playerEntity, this, containerSize), 6);
				break;
			case CHEST:
				containerSize = 27;
				this.containerScreen = (i, playerInventory, playerEntity) -> new ChestMenu(MenuType.GENERIC_9x3, i,
					playerInventory, buildContainer(playerEntity, this, containerSize), 3);
				break;
			case HOPPER:
				containerSize = 5;
				this.containerScreen = (i, playerInventory, playerEntity) -> new HopperMenu(i, playerInventory, buildContainer(playerEntity, this, containerSize));
				break;
			case DROPPER, DISPENSER:
			default:
				containerSize = 9;
				this.containerScreen = (i, playerInventory, playerEntity) -> new DispenserMenu(i, playerInventory, buildContainer(playerEntity, this, containerSize));
				break;
		}
		this.container = new HashMap<>();
		this.dropOnDeath = dropOnDeath;
		this.dropOnDeathFilter = dropOnDeathFilter;
		this.recoverable = recoverable;
		this.keybind = keybind;
	}

	private static @NotNull InventoryContainer buildContainer(Player playerEntity, @NotNull InventoryPower power, int containerSize) {
		if (!power.container.containsKey(playerEntity)) {
			InventoryContainer container = new InventoryContainer(containerSize, playerEntity);
			power.container.put(playerEntity, container);
			return container;
		}
		return power.container.get(playerEntity);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("inventory"))
			.add("title", SerializableDataTypes.STRING, "container.inventory")
			.add("container_type", SerializableDataTypes.enumValue(ContainerType.class), ContainerType.DROPPER)
			.add("drop_on_death", SerializableDataTypes.BOOLEAN, false)
			.add("drop_on_death_filter", ApoliDataTypes.ITEM_CONDITION, null)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND)
			.add("recoverable", SerializableDataTypes.BOOLEAN, true);
	}

	@Override
	public void onLost(Player player) {
		if (recoverable) {
			dropItemsOnLost(player);
		}
	}

	/**
	 * We do this on add so that the container is built so its updated upon server startup from the player PDC
	 */
	@Override
	public void onAdded(Player player) {
		buildContainer(player, this, containerSize);
	}

	@EventHandler
	public void onUse(@NotNull KeybindTriggerEvent e) {
		if (e.getKey().equals(this.keybind.key()) && getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle())) {
			if (this.isActive(((CraftPlayer) e.getPlayer()).getHandle())) {
				if (container.containsKey(((CraftPlayer) e.getPlayer()).getHandle())) {
					container.get(((CraftPlayer) e.getPlayer()).getHandle()).fromTag(
						SerializableDataTypes.NBT_COMPOUND.deserialize(new Gson().fromJson(
							e.getPlayer().getPersistentDataContainer().get(Objects.requireNonNull(NamespacedKey.fromString("inventory____" + getTag())),
								PersistentDataType.STRING), JsonElement.class
						)), ((CraftPlayer) e.getPlayer()).getHandle()
					);
				}
				((CraftPlayer) e.getPlayer()).getHandle().openMenu(new SimpleMenuProvider(containerScreen, getContainerTitle()));
			}
		}
	}

	@Override
	public void tick(Player player) {
		if (container.containsKey(player) && container.get(player).dirty) {
			CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
			craftPlayer.getPersistentDataContainer().set(
				Objects.requireNonNull(NamespacedKey.fromString("inventory____" + getTag())), PersistentDataType.STRING, this.container.get(player).toTag(player).toString()
			);

			container.get(player).dirty = false;
		}

	}

	@EventHandler
	public void onDeath(@NotNull PlayerDeathEvent e) {
		if (getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle()) && shouldDropOnDeath() && isActive(((CraftPlayer) e.getPlayer()).getHandle())) {
			dropItemsOnDeath(((CraftPlayer) e.getPlayer()).getHandle());
		}
	}

	public MutableComponent getContainerTitle() {
		return LangFile.translatable(title).copy();
	}

	public boolean shouldDropOnDeath() {
		return dropOnDeath;
	}

	public boolean shouldDropOnDeath(ItemStack stack, Entity entity) {
		return dropOnDeath
			&& (dropOnDeathFilter == null || dropOnDeathFilter.test(new Tuple<>(entity.level(), stack)));
	}

	public void dropItemsOnDeath(Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < containerSize; ++i) {

			ItemStack currentStack = this.container.get(entity).getItem(i).copy();
			if (!this.shouldDropOnDeath(currentStack, entity)) {
				continue;
			}

			this.container.get(entity).removeItemNoUpdate(i);
			if (!EnchantmentHelper.has(currentStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
				playerEntity.drop(currentStack, true, false);
			}

		}

	}

	public void dropItemsOnLost(Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < containerSize; ++i) {
			playerEntity.getInventory().placeItemBackInInventory(this.container.get(entity).getItem(i));
		}

	}

	public enum ContainerType {
		CHEST,
		DOUBLE_CHEST,
		DROPPER,
		DISPENSER,
		HOPPER
	}

	public static class InventoryContainer implements Container {

		private final NonNullList<ItemStack> container;
		private final int containerSize;
		private final Entity entity;
		private final List<HumanEntity> transaction = new ArrayList<>();
		protected boolean dirty;

		public InventoryContainer(int containerSize, Entity entity) {
			this.containerSize = containerSize;
			this.entity = entity;
			this.container = NonNullList.withSize(containerSize, ItemStack.EMPTY);
		}

		public static void saveAllItems(CompoundTag nbt, NonNullList<ItemStack> stacks, HolderLookup.Provider registries) {
			saveAllItems(nbt, stacks, true, registries);
		}

		public static void saveAllItems(CompoundTag nbt, @NotNull NonNullList<ItemStack> stacks, boolean setIfEmpty, HolderLookup.Provider registries) {
			ListTag listTag = new ListTag();

			for (int i = 0; i < stacks.size(); i++) {
				ItemStack itemStack = stacks.get(i);
				if (!itemStack.isEmpty()) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.putInt("Slot", i);
					listTag.add(itemStack.save(registries, compoundTag));
				}
			}

			if (!listTag.isEmpty() || setIfEmpty) {
				nbt.put("Items", listTag);
			}

		}

		public CompoundTag toTag(@NotNull Entity entity) {

			CompoundTag tag = new CompoundTag();
			saveAllItems(tag, container, entity.registryAccess());

			return tag;

		}

		public void fromTag(@NotNull CompoundTag tag, @NotNull Entity entity) {
			this.clearContent();
			ListTag listTag = tag.getList("Items", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				int j = compoundTag.getInt("Slot");
				if (j < container.size()) {
					container.set(j, ItemStack.parse(entity.registryAccess(), compoundTag).orElse(ItemStack.EMPTY));
				}
			}
		}

		@Override
		public int getContainerSize() {
			return containerSize;
		}

		@Override
		public boolean isEmpty() {
			return container.isEmpty();
		}

		@Override
		public @NotNull ItemStack getItem(int slot) {
			return container.get(slot);
		}

		@Override
		public @NotNull ItemStack removeItem(int slot, int amount) {

			ItemStack stack = ContainerHelper.removeItem(container, slot, amount);
			if (!stack.isEmpty()) {
				this.setChanged();
			}

			return stack;

		}

		@Override
		public @NotNull ItemStack removeItemNoUpdate(int slot) {

			ItemStack prevStack = this.getItem(slot);
			this.setItem(slot, ItemStack.EMPTY);

			return prevStack;

		}

		@Override
		public void setItem(int slot, @NotNull ItemStack stack) {

			container.set(slot, stack);
			if (!stack.isEmpty()) {
				stack.setCount(Math.min(stack.getCount(), this.getMaxStackSize()));
			}

			this.setChanged();

		}

		@Override
		public int getMaxStackSize() {
			return 99;
		}

		@Override
		public void setMaxStackSize(int size) {
		}

		@Override
		public @NotNull Location getLocation() {
			return entity.getBukkitEntity().getLocation();
		}

		@Override
		public void setChanged() {
			this.dirty = true;
		}

		@Override
		public boolean stillValid(@NotNull Player player) {
			return entity == player;
		}

		@Override
		public @NotNull List<ItemStack> getContents() {
			return container;
		}

		@Override
		public void onOpen(@NotNull CraftHumanEntity who) {
			this.transaction.add(who);
		}

		@Override
		public void onClose(@NotNull CraftHumanEntity who) {
			this.transaction.remove(who);
		}

		@Override
		public @NotNull List<HumanEntity> getViewers() {
			return transaction;
		}

		@Override
		public @Nullable InventoryHolder getOwner() {
			return null;
		}

		@Override
		public void clearContent() {
			this.container.clear();
			this.setChanged();
		}

		public SlotAccess getStackReference(int slot) {
			return new SlotAccess() {

				@Override
				public @NotNull ItemStack get() {
					return InventoryContainer.this.getItem(slot);
				}

				@Override
				public boolean set(@NotNull ItemStack stack) {
					InventoryContainer.this.setItem(slot, stack);
					return true;
				}

			};
		}
	}

}
