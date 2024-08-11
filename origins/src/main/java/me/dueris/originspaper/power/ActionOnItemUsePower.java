package me.dueris.originspaper.power;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.registry.registries.PowerType;
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
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> itemAction;

	public ActionOnItemUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								ConditionFactory<Tuple<Level, ItemStack>> itemCondition, ActionFactory<Entity> entityAction, ActionFactory<Tuple<Level, SlotAccess>> itemAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.itemCondition = itemCondition;
		this.entityAction = entityAction;
		this.itemAction = itemAction;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("action_on_item_use"))
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
