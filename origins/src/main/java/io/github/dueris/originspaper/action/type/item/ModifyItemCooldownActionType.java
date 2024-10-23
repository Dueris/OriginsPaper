package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

public class ModifyItemCooldownActionType {

	public static void action(ItemStack stack, Collection<Modifier> modifiers) throws Exception {

		if (stack.isEmpty() || modifiers.isEmpty() || !(((EntityLinkedItemStack) stack).apoli$getEntity(true) instanceof Player player)) {
			return;
		}

		ItemCooldowns cooldownManager = player.getCooldowns();
		ItemCooldowns.CooldownInstance cooldownEntry = cooldownManager.cooldowns.get(stack.getItem());

		Field field = ItemCooldowns.CooldownInstance.class.getDeclaredField("startTime");
		field.setAccessible(true);
		int startTime = field.getInt(cooldownEntry);

		int oldDuration = cooldownEntry != null
			? cooldownEntry.endTime - startTime
			: 0;

		cooldownManager.addCooldown(stack.getItem(), (int) ModifierUtil.applyModifiers(player, modifiers, oldDuration));

	}

	public static ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("modify_item_cooldown"),
			new SerializableData()
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			(data, worldAndStack) -> {

				Collection<Modifier> modifiers = new LinkedList<>();

				data.ifPresent("modifier", modifiers::add);
				data.ifPresent("modifiers", modifiers::addAll);

				try {
					action(worldAndStack.getB(), modifiers);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		);
	}

}
