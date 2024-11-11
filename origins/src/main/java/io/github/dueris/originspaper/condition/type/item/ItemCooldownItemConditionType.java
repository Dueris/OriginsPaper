package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemCooldownItemConditionType extends ItemConditionType {

    public static final TypedDataObjectFactory<ItemCooldownItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
        data -> new ItemCooldownItemConditionType(
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Comparison comparison;
    private final int compareTo;

    public ItemCooldownItemConditionType(Comparison comparison, int compareTo) {
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Level world, ItemStack stack) {

        if (!stack.isEmpty() && ((EntityLinkedItemStack) stack).apoli$getEntity(true) instanceof Player player) {

            ItemCooldowns.CooldownInstance cooldownEntry = player.getCooldowns().cooldowns.get(stack.getItem());
            int cooldown = cooldownEntry != null
                ? Math.abs(cooldownEntry.endTime - cooldownEntry.startTime)
                : 0;

            return comparison.compare(cooldown, compareTo);

        }

        else {
            return false;
        }

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ItemConditionTypes.ITEM_COOLDOWN;
    }

}
