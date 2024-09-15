package io.github.dueris.originspaper.storage;

import com.google.common.collect.ImmutableSet;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.PowerUtils;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

	public static void merge(@NotNull Set<Entry> references, @NotNull ItemPowersComponent instance) {
		Mutable mutable = instance.mutate();
		for (Entry reference : references) {
			mutable.add(reference.powerId, reference.slot);
		}
	}

	public static void onChangeEquipment(LivingEntity entity, @NotNull EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {

		ResourceLocation sourceId = OriginsPaper.apoliIdentifier("item/" + equipmentSlot.getName());
		if (ItemStack.matches(previousStack, currentStack)) {
			return;
		}

		List<PowerType> revokedPowers = new ObjectArrayList<>();
		ItemPowersComponent prevStackPowers = new ItemPowersComponent(previousStack);

		for (Entry prevEntry : prevStackPowers.references) {
			new PowerReference(prevEntry.powerId).getOptionalReference()
				.filter(power -> prevEntry.slot().test(equipmentSlot))
				.ifPresent(revokedPowers::add);
		}

		List<PowerType> grantedPowers = new ObjectArrayList<>();
		ItemPowersComponent currStackPowers = new ItemPowersComponent(currentStack);

		for (Entry currEntry : currStackPowers.references) {
			new PowerReference(currEntry.powerId).getOptionalReference()
				.filter(power -> currEntry.slot().test(equipmentSlot))
				.ifPresent(grantedPowers::add);
		}

		if (!revokedPowers.isEmpty()) {
			for (PowerType revokedPower : revokedPowers) {
				PowerUtils.removePower(
					entity.getBukkitEntity(), revokedPower, entity.getBukkitEntity(), sourceId, true
				);
			}
		}

		if (!grantedPowers.isEmpty()) {
			for (PowerType grantedPower : grantedPowers) {
				PowerUtils.grantPower(
					entity.getBukkitEntity(), grantedPower, entity.getBukkitEntity(), sourceId, true
				);
			}
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

				ItemMeta meta = stack.getBukkitStack().getItemMeta();

				if (meta != null) {
					PersistentDataContainer container = meta.getPersistentDataContainer();

					container.set(KEY, PersistentDataType.STRING, serializeSet(component.references));
					stack.getBukkitStack().setItemMeta(meta);
				}
			}
		}

		public void remove(ResourceLocation reference, EquipmentSlotGroup slotGroup) {
			Entry powerReference = new Entry(reference, slotGroup);
			if (component.references.contains(powerReference)) {
				component.references.remove(powerReference);

				ItemMeta meta = stack.getBukkitStack().getItemMeta();

				if (meta != null) {
					PersistentDataContainer container = meta.getPersistentDataContainer();

					container.set(KEY, PersistentDataType.STRING, serializeSet(component.references));
					stack.getBukkitStack().setItemMeta(meta);
				}
			}

		}

	}

	public record Entry(ResourceLocation powerId, EquipmentSlotGroup slot) {
	}
}
