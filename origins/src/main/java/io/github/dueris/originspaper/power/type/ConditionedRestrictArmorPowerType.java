package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ConditionedRestrictArmorPowerType extends RestrictArmorPowerType {

	private final int tickRate;

	public ConditionedRestrictArmorPowerType(Power power, LivingEntity entity, Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions, int tickRate) {
		super(power, entity, armorConditions);
		this.tickRate = tickRate;
		this.setTicking();
	}

	public static @NotNull PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("conditioned_restrict_armor"),
			new SerializableData()
				.add("head", ApoliDataTypes.ITEM_CONDITION, null)
				.add("chest", ApoliDataTypes.ITEM_CONDITION, null)
				.add("legs", ApoliDataTypes.ITEM_CONDITION, null)
				.add("feet", ApoliDataTypes.ITEM_CONDITION, null)
				.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 80),
			data -> (power, entity) -> {

				Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> restrictions = new HashMap<>();

				if (data.isPresent("head")) {
					restrictions.put(EquipmentSlot.HEAD, data.get("head"));
				}

				if (data.isPresent("chest")) {
					restrictions.put(EquipmentSlot.CHEST, data.get("chest"));
				}

				if (data.isPresent("legs")) {
					restrictions.put(EquipmentSlot.LEGS, data.get("legs"));
				}

				if (data.isPresent("feet")) {
					restrictions.put(EquipmentSlot.FEET, data.get("feet"));
				}

				return new ConditionedRestrictArmorPowerType(power, entity, restrictions, data.getInt("tick_rate"));

			}
		).allowCondition();
	}

	@Override
	public void tick() {

		if (this.isActive()) {
			if (entity.tickCount % tickRate == 0) {
				dropEquippedStacks();
			}
		}

	}

	@SuppressWarnings("RedundantMethodOverride")
	@Override
	public boolean shouldDrop(ItemStack stack, EquipmentSlot slot) {
		return super.doesRestrict(stack, slot);
	}

	@Override
	public boolean doesRestrict(ItemStack stack, EquipmentSlot slot) {
		return false;
	}

}

