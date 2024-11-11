package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.originspaper.component.item.ItemPowersComponent;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HasPowerItemConditionType extends ItemConditionType {

    public static final TypedDataObjectFactory<HasPowerItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT.optional(), Optional.empty())
            .add("power", ApoliDataTypes.POWER_REFERENCE),
        data -> new HasPowerItemConditionType(
            data.get("slot"),
            data.get("power")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("slot", conditionType.slot)
            .set("power", conditionType.power)
    );

    private final Optional<EquipmentSlotGroup> slot;
    private final PowerReference power;

    public HasPowerItemConditionType(Optional<EquipmentSlotGroup> slot, PowerReference power) {
        this.slot = slot;
        this.power = power;
    }

    @Override
    public boolean test(Level world, ItemStack stack) {
        ItemPowersComponent component = new ItemPowersComponent(stack);
        return component.size() > 0 && component.getReferences().stream().map(ItemPowersComponent.Entry::powerId).toList().contains(power.id());
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ItemConditionTypes.HAS_POWER;
    }

}
