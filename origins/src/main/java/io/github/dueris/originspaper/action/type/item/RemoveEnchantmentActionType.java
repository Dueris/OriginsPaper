package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class RemoveEnchantmentActionType {

	public static void action(RegistryAccess registryManager, @NotNull ItemStack stack, Collection<ResourceKey<Enchantment>> enchantmentKeys, @Nullable Integer levels, boolean resetRepairCost) {

		if (!stack.isEnchanted()) {
			return;
		}

		ItemEnchantments oldEnchantments = stack.getEnchantments();
		ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(oldEnchantments);

		boolean hasKeys = false;
		for (ResourceKey<Enchantment> enchantmentKey : enchantmentKeys) {

			//  Since the registry keys are already validated, this should be fine.
			Holder<Enchantment> enchantment = registryManager.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantmentKey);
			hasKeys = true;

			if (oldEnchantments.keySet().contains(enchantment)) {
				newEnchantments.set(enchantment, levels != null ? oldEnchantments.getLevel(enchantment) - levels : 0);
			}

		}

		for (Holder<Enchantment> oldEnchantment : oldEnchantments.keySet()) {

			if (hasKeys) {
				break;
			} else {
				newEnchantments.set(oldEnchantment, levels != null ? oldEnchantments.getLevel(oldEnchantment) - levels : 0);
			}

		}

		stack.set(DataComponents.ENCHANTMENTS, newEnchantments.toImmutable());
		if (resetRepairCost && !stack.isEnchanted()) {
			stack.set(DataComponents.REPAIR_COST, 0);
		}

	}

	public static @NotNull ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("remove_enchantment"),
			new SerializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("enchantments", SerializableDataType.of(SerializableDataTypes.ENCHANTMENT.listOf()), null)
				.add("levels", SerializableDataTypes.INT, null)
				.add("reset_repair_cost", SerializableDataTypes.BOOLEAN, false),
			(data, worldAndStack) -> {

				Collection<ResourceKey<Enchantment>> enchantmentKeys = new HashSet<>();

				data.ifPresent("enchantment", enchantmentKeys::add);
				data.ifPresent("enchantments", enchantmentKeys::addAll);

				action(worldAndStack.getA().registryAccess(), worldAndStack.getB(),
					enchantmentKeys,
					data.get("levels"),
					data.get("reset_repair_cost")
				);

			}
		);
	}

}
