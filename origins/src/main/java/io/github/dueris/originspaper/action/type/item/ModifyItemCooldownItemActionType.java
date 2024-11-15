package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyItemCooldownItemActionType extends ItemActionType {

	public static final TypedDataObjectFactory<ModifyItemCooldownItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("modifier", Modifier.DATA_TYPE, null)
			.addFunctionedDefault("modifiers", Modifier.LIST_TYPE, data -> Util.singletonListOrNull(data.get("modifier"))),
		data -> new ModifyItemCooldownItemActionType(
			data.get("modifiers")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("modifiers", actionType.modifiers)
	);

	private final List<Modifier> modifiers;

	public ModifyItemCooldownItemActionType(List<Modifier> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {

		ItemStack stack = stackReference.get();
		if (stack.isEmpty() || modifiers.isEmpty() || !(((EntityLinkedItemStack) stack).apoli$getEntity(true) instanceof Player player)) {
			return;
		}

		ItemCooldowns cooldownManager = player.getCooldowns();
		ItemCooldowns.CooldownInstance cooldownEntry = cooldownManager.cooldowns.get(stack.getItem());

		int oldDuration = cooldownEntry != null
			? cooldownEntry.endTime - cooldownEntry.startTime
			: 0;

		cooldownManager.addCooldown(stack.getItem(), (int) ModifierUtil.applyModifiers(player, modifiers, oldDuration));

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.MODIFY_ITEM_COOLDOWN;
	}

}
