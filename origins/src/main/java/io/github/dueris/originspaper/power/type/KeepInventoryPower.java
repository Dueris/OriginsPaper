package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeepInventoryPower extends PowerType {
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;
	private final Set<Integer> slots;

	public KeepInventoryPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition, List<Integer> slots) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.itemCondition = itemCondition;
		this.slots = slots == null ? null : new HashSet<>(slots);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("keep_inventory"), PowerType.getFactory().getSerializableData()
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("slots", SerializableDataTypes.INTS, null));
	}

	@EventHandler
	public void onDeath(@NotNull PlayerDeathEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && isActive(player)) {
			e.setKeepInventory(true);
			int containerSize = e.getPlayer().getInventory().getSize();
			ItemStack[] toDrop = new ItemStack[containerSize];
			org.bukkit.inventory.ItemStack[] savedInventory = new org.bukkit.inventory.ItemStack[containerSize];
			for (int i = 0; i < containerSize; i++) {
				org.bukkit.inventory.ItemStack bukkit = e.getPlayer().getInventory().getItem(i);
				if (bukkit == null) continue;
				ItemStack stack = CraftItemStack.unwrap(bukkit);
				if (slots != null && !slots.contains(i)) {
					toDrop[i] = stack;
					continue;
				}
				if (!stack.isEmpty()) {
					if (!(itemCondition == null || itemCondition.test(new Tuple<>(player.level(), stack)))) {
						toDrop[i] = stack;
					} else {
						savedInventory[i] = bukkit;
					}
				}
			}

			e.getDrops().clear();
			for (ItemStack stack : toDrop) {
				if (stack == null) continue;
				player.drop(stack, true, false);
				e.getDrops().add(stack.getBukkitStack());
			}
			e.getPlayer().getInventory().setContents(savedInventory);
		}
	}

	@Nullable
	public Set<Integer> getSlots() {
		return slots;
	}
}
