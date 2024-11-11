package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class PreventItemUsePowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventItemUsePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new PreventItemUsePowerType(
			data.get("item_condition"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("item_condition", powerType.itemCondition)
	);

	private final Optional<ItemCondition> itemCondition;

	public PreventItemUsePowerType(Optional<ItemCondition> itemCondition, Optional<EntityCondition> condition) {
		super(condition);
		this.itemCondition = itemCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ITEM_USE;
	}

	public boolean doesPrevent(ItemStack stack) {
		return itemCondition
			.map(condition -> condition.test(getHolder().level(), stack))
			.orElse(true);
	}

}
