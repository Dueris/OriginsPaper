package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;

public class ItemCooldownConditionType {

	public static boolean condition(ItemStack stack, Comparison comparison, int compareTo) throws Exception {

		if (stack.isEmpty() || !(((EntityLinkedItemStack) stack).apoli$getEntity() instanceof Player player)) {
			return false;
		}

		ItemCooldowns.CooldownInstance cooldownEntry = player.getCooldowns().cooldowns.get(stack.getItem());
		Field field = ItemCooldowns.CooldownInstance.class.getDeclaredField("startTime");
		field.setAccessible(true);
		int startTime = field.getInt(cooldownEntry);

		int cooldown = cooldownEntry != null
			? cooldownEntry.endTime - startTime
			: 0;

		return comparison.compare(cooldown, compareTo);

	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("item_cooldown"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, worldAndStack) -> {
				try {
					return condition(worldAndStack.getB(),
						data.get("comparison"),
						data.get("compare_to")
					);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		);
	}

}
