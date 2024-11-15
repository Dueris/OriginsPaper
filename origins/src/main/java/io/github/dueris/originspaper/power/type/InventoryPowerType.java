package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliContainerTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.ContainerType;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class InventoryPowerType extends PowerType implements Active, Container {

	public static final TypedDataObjectFactory<InventoryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("title", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, Component.translatable("container.inventory"))
			.add("container_type", ApoliDataTypes.CONTAINER_TYPE, ApoliContainerTypes.DROPPER)
			.add("drop_on_death_filter", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key())
			.add("drop_on_death", SerializableDataTypes.BOOLEAN, false)
			.add("recoverable", SerializableDataTypes.BOOLEAN, true),
		(data, condition) -> new InventoryPowerType(
			data.get("title"),
			data.get("container_type"),
			data.get("drop_on_death_filter"),
			data.get("key"),
			data.get("drop_on_death"),
			data.get("recoverable"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("title", powerType.containerTitle)
			.set("container_type", powerType.containerType)
			.set("drop_on_death_filter", powerType.dropOnDeathFilter)
			.set("key", powerType.getKey())
			.set("drop_on_death", powerType.shouldDropOnDeath)
			.set("recoverable", powerType.recoverable)
	);

	private final Component containerTitle;
	private final ContainerType containerType;

	private final Optional<ItemCondition> dropOnDeathFilter;
	private final Key key;

	private final boolean shouldDropOnDeath;
	private final boolean recoverable;

	private final MenuConstructor containerHandlerFactory;
	private final NonNullList<ItemStack> container;
	public List<HumanEntity> transaction = new ArrayList<>();

	private boolean dirty;

	public InventoryPowerType(Component containerTitle, ContainerType containerType, Optional<ItemCondition> dropOnDeathFilter, Key key, boolean shouldDropOnDeath, boolean recoverable, Optional<EntityCondition> condition) {
		super(condition);
		this.containerTitle = containerTitle;
		this.containerType = containerType;
		this.dropOnDeathFilter = dropOnDeathFilter;
		this.key = key;
		this.shouldDropOnDeath = shouldDropOnDeath;
		this.recoverable = recoverable;
		this.containerHandlerFactory = containerType.create(this);
		this.container = NonNullList.withSize(containerType.size(), ItemStack.EMPTY);
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.INVENTORY;
	}

	@Override
	public void onLost() {

		if (recoverable) {
			dropItemsOnLost();
		}

	}

	@Override
	public void onUse() {

		if (this.isActive() && getHolder() instanceof Player player) {
			player.openMenu(new SimpleMenuProvider(containerHandlerFactory, containerTitle));
		}

	}

	@Override
	public void serverTick() {

		if (dirty) {
			PowerHolderComponent.syncPower(getHolder(), getPower());
		}

		this.dirty = false;

	}

	@Override
	public CompoundTag toTag() {

		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, container, getHolder().registryAccess());

		return tag;

	}

	@Override
	public void fromTag(Tag tag) {

		if (!(tag instanceof CompoundTag rootNbt)) {
			return;
		}

		this.clearContent();
		ContainerHelper.loadAllItems(rootNbt, container, getHolder().registryAccess());

	}

	@Override
	public int getContainerSize() {
		return container.size();
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
		return container.size();
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
		return player == getHolder();

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
		return (InventoryHolder) this.getHolder().getBukkitLivingEntity();
	}

	@Override
	public @NotNull Location getLocation() {
		return getHolder().getBukkitLivingEntity().getLocation();
	}

	@Override
	public void clearContent() {
		this.container.clear();
		this.setChanged();
	}

	@Override
	public Key getKey() {
		return key;
	}

	public NonNullList<ItemStack> getContainer() {
		return container;
	}

	public Component getContainerTitle() {
		return containerTitle;
	}

	public MenuConstructor getContainerHandlerFactory() {
		return containerHandlerFactory;
	}

	public boolean shouldDropOnDeath() {
		return shouldDropOnDeath;
	}

	public boolean shouldDropOnDeath(ItemStack stack) {
		return shouldDropOnDeath()
			&& dropOnDeathFilter.map(condition -> condition.test(getHolder().level(), stack)).orElse(true);
	}

	public void dropItemsOnDeath() {

		if (!(getHolder() instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < container.size(); ++i) {

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

		if (!(getHolder() instanceof Player playerEntity)) {
			return;
		}

		for (int i = 0; i < container.size(); ++i) {
			playerEntity.getInventory().placeItemBackInInventory(this.getItem(i));
		}

	}

}
