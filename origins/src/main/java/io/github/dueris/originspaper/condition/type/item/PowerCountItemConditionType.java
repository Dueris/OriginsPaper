package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.originspaper.component.item.ItemPowersComponent;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PowerCountItemConditionType extends ItemConditionType {

    public static final TypedDataObjectFactory<PowerCountItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT.optional(), Optional.empty())
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
        data -> new PowerCountItemConditionType(
            data.get("slot"),
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("slot", conditionType.slot)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Optional<EquipmentSlotGroup> slot;

    private final Comparison comparison;
    private final int compareTo;

    public PowerCountItemConditionType(Optional<EquipmentSlotGroup> slot, Comparison comparison, int compareTo) {
        this.slot = slot;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Level world, ItemStack stack) {

        ItemPowersComponent itemPowers = new ItemPowersComponent(stack);
        int powers;

        if (slot.isPresent()) {
            powers = (int) itemPowers
                .stream()
                .filter(entry -> entry.slot().equals(slot))
                .count();
        } else {
            powers = itemPowers.size();
        }

        return comparison.compare(powers, compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ItemConditionTypes.POWER_COUNT;
    }

}
