package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RelativeItemCooldownItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<RelativeItemCooldownItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F)),
		data -> new RelativeItemCooldownItemConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final float compareTo;

	public RelativeItemCooldownItemConditionType(Comparison comparison, float compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {

		if (!stack.isEmpty() && ((EntityLinkedItemStack) stack).apoli$getEntity(true) instanceof Player player) {

			ItemCooldowns cooldownManager = player.getCooldowns();
			float cooldownProgress = cooldownManager.getCooldownPercent(stack.getItem(), 0F);

			return comparison.compare(cooldownProgress, compareTo);

		} else {
			return false;
		}

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.RELATIVE_ITEM_COOLDOWN;
	}

}
