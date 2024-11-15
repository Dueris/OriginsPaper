package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConditionedRestrictArmorPowerType extends RestrictArmorPowerType {

	public static final TypedDataObjectFactory<ConditionedRestrictArmorPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		createSerializableData()
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 20),
		(data, condition) -> {

			EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions = Arrays.stream(EquipmentSlot.values())
				.filter(EquipmentSlot::isArmor)
				.collect(Collectors.toMap(Function.identity(), slot -> data.get(slot.getName()), (o1, o2) -> o2, () -> new EnumMap<>(EquipmentSlot.class)));

			return new ConditionedRestrictArmorPowerType(
				armorConditions,
				data.get("tick_rate"),
				condition
			);

		},
		(powerType, serializableData) -> {

			SerializableData.Instance data = serializableData.instance();
			powerType.armorConditions.forEach((equipmentSlot, itemCondition) -> data.set(equipmentSlot.getName(), itemCondition));

			return data.set("tick_rate", powerType.tickRate);

		}
	);

	protected final int tickRate;

	private Integer startTicks;
	private Integer endTicks;

	private boolean wasActive;

	public ConditionedRestrictArmorPowerType(EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions, int tickRate, Optional<EntityCondition> condition) {
		super(armorConditions, condition);
		this.tickRate = tickRate;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.CONDITIONED_RESTRICT_ARMOR;
	}

	@Override
	public void serverTick() {

		LivingEntity holder = getHolder();
		if (this.isActive()) {
			if (holder.tickCount % tickRate == 0) {
				dropEquippedStacks();
			}
		}

	}

}
