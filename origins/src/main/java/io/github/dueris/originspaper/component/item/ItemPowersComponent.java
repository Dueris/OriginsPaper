package io.github.dueris.originspaper.component.item;

import com.google.common.collect.ImmutableSet;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemPowersComponent {
	private static final NamespacedKey KEY = new NamespacedKey("apoli", "powers");
	private final Set<Entry> references;
	private final ItemStack stack;

	public ItemPowersComponent(@NotNull ItemStack stack) {
		references = new LinkedHashSet<>();
		this.stack = stack;

		if (stack.getBukkitStack().getItemMeta() != null && stack.getBukkitStack().getItemMeta().getPersistentDataContainer().has(KEY)) {
			String cached = stack.getBukkitStack().getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
			Mutable mutable = this.mutate();
			if (cached != null) {
				for (Entry reference : Mutable.deserializeSet(cached)) {
					mutable.add(reference.powerId, reference.slot);
				}
			}

		}
	}

	public static void onChangeEquipment(LivingEntity entity, @NotNull EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {

		ResourceLocation sourceId = OriginsPaper.apoliIdentifier("item/" + equipmentSlot.getName());
		if (ItemStack.matches(previousStack, currentStack)) {
			return;
		}

		List<Power> revokedPowers = new ObjectArrayList<>();
		ItemPowersComponent prevStackPowers = new ItemPowersComponent(previousStack);

		for (Entry prevEntry : prevStackPowers.references) {
			PowerReference.of(prevEntry.powerId()).getOptionalReference()
				.filter(power -> prevEntry.slot().test(equipmentSlot))
				.ifPresent(revokedPowers::add);
		}

		List<Power> grantedPowers = new ObjectArrayList<>();
		ItemPowersComponent currStackPowers = new ItemPowersComponent(currentStack);

		for (Entry currEntry : currStackPowers.references) {
			PowerManager.getOptional(currEntry.powerId())
				.filter(power -> currEntry.slot().test(equipmentSlot))
				.ifPresent(grantedPowers::add);
		}

		if (!revokedPowers.isEmpty()) {
			PowerHolderComponent.revokePowers(entity, Map.of(sourceId, revokedPowers), true);
		}

		if (!grantedPowers.isEmpty()) {
			PowerHolderComponent.grantPowers(entity, Map.of(sourceId, grantedPowers), true);
		}

	}

	public Mutable mutate() {
		return new Mutable(this, stack);
	}

	public int size() {
		return references.size();
	}

	public ItemStack getStack() {
		return stack;
	}

	@Unmodifiable
	public Set<Entry> getReferences() {
		return ImmutableSet.copyOf(references);
	}

	public Stream<Entry> stream() {
		return references.stream();
	}

	/**
	 * Made for MODIFYING the component on the stack, not for getting values.
	 *
	 * @param component
	 * @param stack
	 */
	public record Mutable(ItemPowersComponent component, ItemStack stack) {

		private static @NotNull String serializeSet(@NotNull Set<Entry> set) {
			return set.stream()
				.map(entry -> entry.powerId().toString() + "|" + entry.slot().name())
				.collect(Collectors.joining(","));
		}

		public static Set<Entry> deserializeSet(@NotNull String str) {
			return Arrays.stream(str.split(","))
				.map(s -> {
					String[] parts = s.split("\\|");
					ResourceLocation powerId = ResourceLocation.parse(parts[0]);
					EquipmentSlotGroup slot = EquipmentSlotGroup.valueOf(parts[1]);
					return new Entry(powerId, slot);
				})
				.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		public void add(ResourceLocation reference, EquipmentSlotGroup slotGroup) {
			Entry powerReference = new Entry(reference, slotGroup);

			if (!component.references.contains(powerReference)) {
				component.references.add(powerReference);

				serialize(stack.getBukkitStack().getItemMeta());
			}
		}

		public void remove(ResourceLocation reference, EquipmentSlotGroup slotGroup) {
			Entry powerReference = new Entry(reference, slotGroup);
			if (component.references.contains(powerReference)) {
				component.references.remove(powerReference);

				serialize(stack.getBukkitStack().getItemMeta());
			}

		}

		private void serialize(ItemMeta meta) {
			if (meta != null) {
				PersistentDataContainer container = meta.getPersistentDataContainer();

				container.set(KEY, PersistentDataType.STRING, serializeSet(component.references));
				stack.getBukkitStack().setItemMeta(meta);
			}
		}

	}

	public record Entry(ResourceLocation powerId, EquipmentSlotGroup slot) {
	}
}
