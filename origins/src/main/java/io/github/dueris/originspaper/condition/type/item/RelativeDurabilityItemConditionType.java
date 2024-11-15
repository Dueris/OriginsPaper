package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RelativeDurabilityItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<RelativeDurabilityItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F)),
		data -> new RelativeDurabilityItemConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final float compareTo;

	public RelativeDurabilityItemConditionType(Comparison comparison, float compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {

		if (stack.isDamageableItem()) {
			return comparison.compare(Math.abs((float) (stack.getMaxDamage() - stack.getDamageValue())) / (float) stack.getMaxDamage(), compareTo);
		} else {
			return false;
		}

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.RELATIVE_DURABILITY;
	}

}
