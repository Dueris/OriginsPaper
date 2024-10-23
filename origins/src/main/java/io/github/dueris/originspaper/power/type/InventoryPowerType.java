package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class InventoryPowerType extends PowerType implements Active, Container {

	private final NonNullList<ItemStack> container;
	private final MutableComponent containerTitle;
	private final MenuConstructor containerScreen;
	private final Predicate<Tuple<Level, ItemStack>> dropOnDeathFilter;
	private final Key key;

	private final boolean shouldDropOnDeath;
	private final boolean recoverable;

	private final int containerSize;
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private boolean dirty;

	public InventoryPowerType(Power power, LivingEntity entity, String containerTitle, @NotNull ContainerType containerType, boolean shouldDropOnDeath, Predicate<Tuple<Level, ItemStack>> dropOnDeathFilter, Key key, boolean recoverable) {
		super(power, entity);
		switch (containerType) {
			case DOUBLE_CHEST:
				containerSize = 54;
				this.containerScreen = (i, playerInventory, playerEntity) -> new ChestMenu(MenuType.GENERIC_9x6, i,
					playerInventory, this, 6);
				break;
			case CHEST:
				containerSize = 27;
				this.containerScreen = (i, playerInventory, playerEntity) -> new ChestMenu(MenuType.GENERIC_9x3, i,
					playerInventory, this, 3);
				break;
			case HOPPER:
				containerSize = 5;
				this.containerScreen = (i, playerInventory, playerEntity) -> new HopperMenu(i, playerInventory, this);
				break;
			case DROPPER, DISPENSER:
			default:
				containerSize = 9;
				this.containerScreen = (i, playerInventory, playerEntity) -> new DispenserMenu(i, playerInventory, this);
				break;
		}
		this.container = NonNullList.withSize(containerSize, ItemStack.EMPTY);
		this.containerTitle = Component.translatable(containerTitle);
		this.shouldDropOnDeath = shouldDropOnDeath;
		this.dropOnDeathFilter = dropOnDeathFilter;
		this.key = key;
		this.recoverable = recoverable;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("inventory"),
			new SerializableData()
				.add("title", SerializableDataTypes.STRING, "container.inventory")
				.add("container_type", SerializableDataType.enumValue(ContainerType.class), ContainerType.DROPPER)
				.add("drop_on_death", SerializableDataTypes.BOOLEAN, false)
				.add("drop_on_death_filter", ApoliDataTypes.ITEM_CONDITION, null)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
				.add("recoverable", SerializableDataTypes.BOOLEAN, true),
			data -> (power, entity) -> new InventoryPowerType(power, entity,
				data.getString("title"),
				data.get("container_type"),
				data.get("drop_on_death"),
				data.get("drop_on_death_filter"),
				data.get("key"),
				data.getBoolean("recoverable")
			)
		).allowCondition();
	}

	@Override
	public void onLost() {
		if (recoverable) {
			dropItemsOnLost();
		}
	}

	@Override
	public void onUse() {

		if (this.isActive() && entity instanceof Player player) {
			player.openMenu(new SimpleMenuProvider(containerScreen, containerTitle));
		}

	}

	@Override
	public void tick() {

		if (dirty) {
			PowerHolderComponent.syncPower(entity, power);
		}

		this.dirty = false;

	}

	@Override
	public CompoundTag toTag() {

		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, container, entity.registryAccess());

		return tag;

	}

	@Override
	public void fromTag(Tag tag) {

		if (!(tag instanceof CompoundTag rootNbt)) {
			return;
		}

		this.clearContent();
		ContainerHelper.loadAllItems(rootNbt, container, entity.registryAccess());

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
		return containerSize;
	}

	@Override
	public void setMaxStackSize(int size) {
		// We arent gonna let people modify this...
	}

	@Override
	public void setChanged() {
		this.dirty = true;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return player == this.entity;
	}

	@Override
	public @NotNull List<ItemStack> getContents() {
		return this.container;
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
		return this.transaction;
	}

	@Override
	public @Nullable InventoryHolder getOwner() {
		return (InventoryHolder) this.entity.getBukkitLivingEntity();
	}

	@Override
	public @NotNull Location getLocation() {
		return entity.getBukkitLivingEntity().getLocation();
	}

	@Override
	public void clearContent() {
		this.container.clear();
		this.setChanged();
	}

	@Deprecated(forRemoval = true)
	public SlotAccess getStackReference(int slot) {
		return new SlotAccess() {

			@Override
			public @NotNull ItemStack get() {
				return InventoryPowerType.this.getItem(slot);
			}

			@Override
			public boolean set(@NotNull ItemStack stack) {
				InventoryPowerType.this.setItem(slot, stack);
				return true;
			}

		};
	}

	public NonNullList<ItemStack> getContainer() {
		return container;
	}

	public MutableComponent getContainerTitle() {
		return containerTitle;
	}

	public MenuConstructor getContainerScreen() {
		return containerScreen;
	}

	public boolean shouldDropOnDeath() {
		return shouldDropOnDeath;
	}

	public boolean shouldDropOnDeath(ItemStack stack) {
		return shouldDropOnDeath
			&& (dropOnDeathFilter == null || dropOnDeathFilter.test(new Tuple<>(entity.level(), stack)));
	}

	public void dropItemsOnDeath() {

		if (!(entity instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < containerSize; ++i) {

			ItemStack currentStack = this.getItem(i).copy();
			if (!this.shouldDropOnDeath(currentStack)) {
				continue;
			}

			this.removeItemNoUpdate(i);
			if (!EnchantmentHelper.has(currentStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
				playerEntity.drop(currentStack, true, false);
			}

		}

	}

	public void dropItemsOnLost() {

		if (!(entity instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < containerSize; ++i) {
			playerEntity.getInventory().placeItemBackInInventory(this.getItem(i));
		}

	}


	@Override
	public Key getKey() {
		return key;
	}


	public enum ContainerType {
		CHEST,
		DOUBLE_CHEST,
		DROPPER,
		DISPENSER,
		HOPPER
	}
}

