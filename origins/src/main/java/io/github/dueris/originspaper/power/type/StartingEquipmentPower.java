package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StartingEquipmentPower extends PowerType {
	private final boolean recurrent;
	private final List<ItemStack> itemStacks = new LinkedList<>();
	private final HashMap<Integer, ItemStack> slottedStacks = new HashMap<>();

	public StartingEquipmentPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  Tuple<Integer, ItemStack> positionedItemStack, List<Tuple<Integer, ItemStack>> positionedItemStacks, boolean recurrent) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.recurrent = recurrent;

		if (positionedItemStack != null) {

			ItemStack stack = positionedItemStack.getB();
			int slot = positionedItemStack.getA();

			if (slot > Integer.MIN_VALUE) {
				addStack(slot, stack);
			} else {
				addStack(stack);
			}

		}

		if (positionedItemStacks != null) {

			positionedItemStacks.forEach(slotAndStack -> {

				ItemStack stack = slotAndStack.getB();
				int slot = slotAndStack.getA();

				if (slot > Integer.MIN_VALUE) {
					addStack(slot, stack);
				} else {
					addStack(stack);
				}

			});

		}
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("starting_equipment"), PowerType.getFactory().getSerializableData()
			.add("stack", ApoliDataTypes.POSITIONED_ITEM_STACK, null)
			.add("stacks", SerializableDataType.of(ApoliDataTypes.POSITIONED_ITEM_STACK.listOf()), null)
			.add("recurrent", SerializableDataTypes.BOOLEAN, false));
	}

	@Override
	public void onAdded(Player player) {
		giveStacks(player);
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent e) {
		if (!recurrent) return;
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player)) {
			giveStacks(player);
		}
	}

	public void addStack(ItemStack stack) {
		this.itemStacks.add(stack);
	}

	public void addStack(int slot, ItemStack stack) {
		slottedStacks.put(slot, stack);
	}

	private void giveStacks(Entity entity) {
		slottedStacks.forEach((slot, stack) -> {
			if (entity instanceof Player player) {
				Inventory inventory = player.getInventory();
				if (inventory.getItem(slot).isEmpty()) {
					inventory.setItem(slot, stack);
				} else {
					player.addItem(stack);
				}
			} else {
				entity.spawnAtLocation(stack);
			}
		});
		itemStacks.forEach(is -> {
			ItemStack copy = is.copy();
			if (entity instanceof Player player) {
				player.addItem(copy);
			} else {
				entity.spawnAtLocation(copy);
			}
		});
	}
}
