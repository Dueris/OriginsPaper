package io.github.dueris.originspaper.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.component.item.ItemPowersComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.EnumSet;
import java.util.List;

public class AddPowerLootFunction extends LootItemConditionalFunction {

	public static final MapCodec<AddPowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
		SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT_SET.codec().optionalFieldOf("slot", EnumSet.of(EquipmentSlotGroup.ANY)).forGetter(AddPowerLootFunction::slots),
		ApoliDataTypes.POWER_REFERENCE.codec().fieldOf("power").forGetter(AddPowerLootFunction::power),
		Codec.BOOL.optionalFieldOf("hidden", false).forGetter(AddPowerLootFunction::hidden),
		Codec.BOOL.optionalFieldOf("negative", false).forGetter(AddPowerLootFunction::negative)
	)).apply(instance, AddPowerLootFunction::new));

	private final EnumSet<EquipmentSlotGroup> slots;
	private final PowerReference power;

	private final boolean hidden;
	private final boolean negative;

	private AddPowerLootFunction(List<LootItemCondition> conditions, EnumSet<EquipmentSlotGroup> slots, PowerReference power, boolean hidden, boolean negative) {
		super(conditions);
		this.slots = slots;
		this.power = power;
		this.hidden = hidden;
		this.negative = negative;
	}

	@Override
	public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
		return ApoliLootFunctionTypes.ADD_POWER;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {

		power().getOptionalReference().ifPresent(power -> {

			/* ItemPowersComponent itemPowers = stack.getOrDefault(PowerHolderComponent.POWERS, ItemPowersComponent.DEFAULT);
			stack.set(PowerHolderComponent.POWERS, ItemPowersComponent.builder(itemPowers)
				.add(slots(), power.getId(), hidden(), negative())
				.build()); */
			// Is it possible to fake data to the client to prevent kick? - Dueris
			ItemPowersComponent component = new ItemPowersComponent(stack);
			for (EquipmentSlotGroup slot : slots) {
				component.mutate().add(power().getId(), slot);
			}
		});

		return stack;

	}

	public EnumSet<EquipmentSlotGroup> slots() {
		return slots;
	}

	public PowerReference power() {
		return power;
	}

	public boolean hidden() {
		return hidden;
	}

	public boolean negative() {
		return negative;
	}

}
