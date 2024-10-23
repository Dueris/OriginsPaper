package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class PreventItemUsePowerType extends PowerType {

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	public PreventItemUsePowerType(Power power, LivingEntity entity, Predicate<Tuple<Level, ItemStack>> itemCondition) {
		super(power, entity);
		this.itemCondition = itemCondition;
	}

	public boolean doesPrevent(ItemStack stack) {
		return itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack));
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_item_use"),
			new SerializableData()
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
			data -> (power, entity) -> new PreventItemUsePowerType(power, entity,
				data.get("item_condition")
			)
		).allowCondition();
	}

}
