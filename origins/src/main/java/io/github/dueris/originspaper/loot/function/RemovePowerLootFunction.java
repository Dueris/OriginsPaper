package io.github.dueris.originspaper.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.storage.ItemPowersComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class RemovePowerLootFunction extends LootItemConditionalFunction {

	public static final MapCodec<RemovePowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
		SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT_SET.optionalFieldOf("slot", EnumSet.allOf(EquipmentSlotGroup.class)).forGetter(RemovePowerLootFunction::slots),
		ResourceLocation.CODEC.fieldOf("power").forGetter(RemovePowerLootFunction::powerId)
	)).apply(instance, RemovePowerLootFunction::new));

	private final EnumSet<EquipmentSlotGroup> slots;
	private final ResourceLocation powerId;

	private RemovePowerLootFunction(List<LootItemCondition> conditions, EnumSet<EquipmentSlotGroup> slots, ResourceLocation powerId) {
		super(conditions);
		this.slots = slots;
		this.powerId = powerId;
	}

	@Override
	public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
		return ApoliLootFunctionTypes.REMOVE_POWER;
	}

	@Override
	public @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {

		ItemPowersComponent component = new ItemPowersComponent(stack);
		for (EquipmentSlotGroup slot : slots) {
			component.mutate().remove(powerId(), slot);
		}
		return stack;

	}

	public EnumSet<EquipmentSlotGroup> slots() {
		return slots;
	}

	public ResourceLocation powerId() {
		return powerId;
	}

}
