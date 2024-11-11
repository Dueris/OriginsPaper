package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.IndexedStack;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StartingEquipmentPowerType extends PowerType {

	public static final TypedDataObjectFactory<StartingEquipmentPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("stack", IndexedStack.DATA_TYPE, null)
			.addFunctionedDefault("stacks", IndexedStack.DATA_TYPE.list(1, Integer.MAX_VALUE), data -> Util.singletonListOrNull(data.get("stack")))
			.add("recurrent", SerializableDataTypes.BOOLEAN, false)
			.validate(Util.validateAnyFieldsPresent("stack", "stacks")),
		(data, condition) -> new StartingEquipmentPowerType(
			data.get("stacks"),
			data.get("recurrent"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("stacks", powerType.indexedStacks)
			.set("recurrent", powerType.recurrent)
	);

	private final List<IndexedStack> indexedStacks;
	private final boolean recurrent;

	public StartingEquipmentPowerType(List<IndexedStack> indexedStacks, boolean recurrent, Optional<EntityCondition> condition) {
		super(condition);
		this.indexedStacks = indexedStacks;
		this.recurrent = recurrent;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.STARTING_EQUIPMENT;
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

		LivingEntity holder = getHolder();

		for (IndexedStack indexedStack : indexedStacks) {

			ItemStack stack = indexedStack.stack();
			int slotId = indexedStack.slotId();

			SlotAccess stackReference = holder.getSlot(slotId);

			if (stackReference.get().isEmpty()) {
				stackReference.set(stack);
			}

			else if (holder instanceof Player player) {
				player.getInventory().placeItemBackInInventory(stack);
			}

			else {
				InventoryUtil.throwItem(holder, stack, true, true, 0);
			}

		}

	}

}
