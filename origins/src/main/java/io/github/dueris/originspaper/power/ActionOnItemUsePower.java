package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ActionOnItemUsePower extends PowerType {
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ActionTypeFactory<Entity> entityAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction;

	public ActionOnItemUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition, ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.itemCondition = itemCondition;
		this.entityAction = entityAction;
		this.itemAction = itemAction;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_item_use"))
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null);
	}

	@EventHandler
	public void onItemUse(@NotNull PlayerInteractEvent e) {
		if (e.getItem() != null && !e.getItem().isEmpty()) {
			Player player = ((CraftPlayer) e.getPlayer()).getHandle();
			ItemStack stack = CraftItemStack.unwrap(e.getItem());
			if (player.level().isClientSide) {
				return;
			}
			if (doesApply(stack, player) && getPlayers().contains(player) && isActive(player)) {
				executeActions(
					SlotAccess.forEquipmentSlot(player, player.getEquipmentSlotForItem(stack)),
					player
				);
			}
		}
	}

	public boolean doesApply(ItemStack stack, Entity entity) {
		return (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)));
	}

	public void executeActions(SlotAccess stack, Entity entity) {

		if (itemAction != null) {
			itemAction.accept(new Tuple<>(entity.level(), stack));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
