package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StartingEquipmentPowerType extends PowerType {

	private final List<ItemStack> itemStacks = new LinkedList<>();
	private final HashMap<Integer, ItemStack> slottedStacks = new HashMap<>();
	private boolean recurrent;

	public StartingEquipmentPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("starting_equipment"),
			new SerializableData()
				.add("stack", ApoliDataTypes.POSITIONED_ITEM_STACK, null)
				.add("stacks", ApoliDataTypes.POSITIONED_ITEM_STACKS, null)
				.add("recurrent", SerializableDataTypes.BOOLEAN, false),
			data -> (power, entity) -> {

				StartingEquipmentPowerType powerType = new StartingEquipmentPowerType(power, entity);

				if (data.isPresent("stack")) {

					Tuple<Integer, ItemStack> slotAndStack = data.get("stack");

					ItemStack stack = slotAndStack.getB();
					int slot = slotAndStack.getA();

					if (slot > Integer.MIN_VALUE) {
						powerType.addStack(slot, stack);
					} else {
						powerType.addStack(stack);
					}

				}

				if (data.isPresent("stacks")) {

					List<Tuple<Integer, ItemStack>> stacks = data.get("stacks");
					stacks.forEach(slotAndStack -> {

						ItemStack stack = slotAndStack.getB();
						int slot = slotAndStack.getA();

						if (slot > Integer.MIN_VALUE) {
							powerType.addStack(slot, stack);
						} else {
							powerType.addStack(stack);
						}

					});

				}

				powerType.setRecurrent(data.getBoolean("recurrent"));
				return powerType;

			}
		);
	}

	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}

	public void addStack(ItemStack stack) {
		this.itemStacks.add(stack);
	}

	public void addStack(int slot, ItemStack stack) {
		slottedStacks.put(slot, stack);
	}

	@Override
	public void onGained() {
		giveStacks();
	}

	@Override
	public void onRespawn() {
		if (recurrent) {
			giveStacks();
		}
	}

	private void giveStacks() {
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
