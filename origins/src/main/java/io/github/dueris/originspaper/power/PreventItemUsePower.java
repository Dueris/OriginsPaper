package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PreventItemUsePower extends PowerType {
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;

	public PreventItemUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   ConditionFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.itemCondition = itemCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_item_use"))
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null);
	}

	public boolean doesPrevent(ItemStack stack, Entity entity) {
		return itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack));
	}

	@EventHandler
	public void onItemUse(@NotNull PlayerInteractEvent e) {
		if (e.getItem() != null && !e.getItem().isEmpty()) {
			Player player = ((CraftPlayer) e.getPlayer()).getHandle();
			ItemStack stack = CraftItemStack.unwrap(e.getItem());
			if (player.level().isClientSide) {
				return;
			}

			if (getPlayers().contains(player) && isActive(player) && doesPrevent(stack, player)) {
				e.setCancelled(true);
			}

		}
	}
}