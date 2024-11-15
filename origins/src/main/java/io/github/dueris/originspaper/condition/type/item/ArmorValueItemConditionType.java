package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ArmorValueItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<ArmorValueItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new ArmorValueItemConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final int compareTo;

	public ArmorValueItemConditionType(Comparison comparison, int compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		return stack.getItem() instanceof ArmorItem armorItem
			&& comparison.compare(armorItem.getDefense(), compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.ARMOR_VALUE;
	}

}
