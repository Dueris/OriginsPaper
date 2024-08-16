package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.action.ItemActionFactory;
import net.minecraft.core.Holder;
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

import java.util.LinkedList;
import java.util.List;

public class RemoveEnchantmentAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ItemStack stack = worldAndStack.getB();
		Level world = worldAndStack.getA();

		if (!stack.isEnchanted()) {
			return;
		}

		List<ResourceKey<Enchantment>> enchantmentKeys = new LinkedList<>();

		data.<ResourceKey<Enchantment>>ifPresent("enchantment", enchantmentKeys::add);
		data.<List<ResourceKey<Enchantment>>>ifPresent("enchantments", enchantmentKeys::addAll);

		ItemEnchantments component = stack.getEnchantments();
		ItemEnchantments.Mutable componentBuilder = new ItemEnchantments.Mutable(component);

		Integer levels = data.isPresent("levels")
			? data.getInt("levels")
			: null;

		if (!enchantmentKeys.isEmpty()) {

			for (ResourceKey<Enchantment> enchantmentKey : enchantmentKeys) {

				Holder<Enchantment> enchantmentEntry = world.registryAccess()
					.registryOrThrow(Registries.ENCHANTMENT)
					.getHolder(enchantmentKey)
					.orElseThrow();

				if (component.keySet().contains(enchantmentEntry)) {
					componentBuilder.set(enchantmentEntry, levels != null ? component.getLevel(enchantmentEntry) - levels : 0);
				}

			}

		} else {

			for (Holder<Enchantment> enchantment : component.keySet()) {
				componentBuilder.set(enchantment, levels != null ? component.getLevel(enchantment) - levels : 0);
			}

		}

		stack.set(DataComponents.ENCHANTMENTS, componentBuilder.toImmutable());
		if (data.getBoolean("reset_repair_cost") && !stack.isEnchanted()) {
			stack.set(DataComponents.REPAIR_COST, 0);
		}

	}

	public static @NotNull ActionFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("remove_enchantment"),
			SerializableData.serializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("enchantments", SerializableDataTypes.list(SerializableDataTypes.ENCHANTMENT), null)
				.add("levels", SerializableDataTypes.INT, null)
				.add("reset_repair_cost", SerializableDataTypes.BOOLEAN, false),
			RemoveEnchantmentAction::action
		);
	}
}
