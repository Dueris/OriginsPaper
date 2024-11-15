package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RemoveEnchantmentItemActionType extends ItemActionType {

	public static final TypedDataObjectFactory<RemoveEnchantmentItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("enchantment", SerializableDataTypes.ENCHANTMENT.optional(), Optional.empty())
			.add("enchantments", SerializableDataTypes.ENCHANTMENT.list().optional(), Optional.empty())
			.add("levels", SerializableDataTypes.INT.optional(), Optional.empty())
			.add("reset_repair_cost", SerializableDataTypes.BOOLEAN, false),
		data -> new RemoveEnchantmentItemActionType(
			data.get("enchantment"),
			data.get("enchantments"),
			data.get("levels"),
			data.get("reset_repair_cost")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("enchantment", actionType.enchantmentKey)
			.set("enchantments", actionType.enchantmentKeys)
			.set("levels", actionType.levels)
			.set("reset_repair_cost", actionType.resetRepairCost)
	);

	private final Optional<ResourceKey<Enchantment>> enchantmentKey;
	private final Optional<List<ResourceKey<Enchantment>>> enchantmentKeys;

	private final List<ResourceKey<Enchantment>> allEnchantmentKeys;

	private final Optional<Integer> levels;
	private final boolean resetRepairCost;

	public RemoveEnchantmentItemActionType(Optional<ResourceKey<Enchantment>> enchantmentKey, Optional<List<ResourceKey<Enchantment>>> enchantmentKeys, Optional<Integer> levels, boolean resetRepairCost) {

		this.enchantmentKey = enchantmentKey;
		this.enchantmentKeys = enchantmentKeys;

		this.allEnchantmentKeys = new ObjectArrayList<>();

		this.enchantmentKey.ifPresent(this.allEnchantmentKeys::add);
		this.enchantmentKeys.ifPresent(this.allEnchantmentKeys::addAll);

		this.levels = levels;
		this.resetRepairCost = resetRepairCost;

	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {

		ItemStack stack = stackReference.get();
		RegistryAccess dynamicRegistries = world.registryAccess();

		if (!stack.isEnchanted()) {
			return;
		}

		ItemEnchantments oldEnchantments = stack.getEnchantments();
		ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(oldEnchantments);

		Registry<Enchantment> enchantmentRegistry = dynamicRegistries.registryOrThrow(Registries.ENCHANTMENT);
		for (ResourceKey<Enchantment> enchantmentKey : allEnchantmentKeys) {

			//  Since the registry keys are already validated, this should be fine.
			Holder<Enchantment> enchantment = enchantmentRegistry.getHolderOrThrow(enchantmentKey);

			if (oldEnchantments.keySet().contains(enchantment)) {
				newEnchantments.set(enchantment, levels.map(lvl -> oldEnchantments.getLevel(enchantment) - lvl).orElse(0));
			}

		}

		for (Holder<Enchantment> oldEnchantment : oldEnchantments.keySet()) {

			if (!allEnchantmentKeys.isEmpty()) {
				break;
			} else {
				newEnchantments.set(oldEnchantment, levels.map(lvl -> oldEnchantments.getLevel(oldEnchantment) - lvl).orElse(0));
			}

		}

		stack.set(DataComponents.ENCHANTMENTS, newEnchantments.toImmutable());
		if (resetRepairCost && !stack.isEnchanted()) {
			stack.set(DataComponents.REPAIR_COST, 0);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.REMOVE_ENCHANTMENT;
	}

}
