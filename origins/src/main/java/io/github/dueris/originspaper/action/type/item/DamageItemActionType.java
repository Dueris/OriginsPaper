package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DamageItemActionType extends ItemActionType {

	public static final TypedDataObjectFactory<DamageItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("amount", SerializableDataTypes.INT, 1)
			.add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false),
		data -> new DamageItemActionType(
			data.get("amount"),
			data.get("ignore_unbreaking")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("amount", actionType.amount)
			.set("ignore_unbreaking", actionType.ignoreUnbreaking)
	);

	private final int amount;
	private final boolean ignoreUnbreaking;

	public DamageItemActionType(int amount, boolean ignoreUnbreaking) {
		this.amount = amount;
		this.ignoreUnbreaking = ignoreUnbreaking;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {

		ItemStack stack = stackReference.get();
		if (world instanceof ServerLevel serverWorld) {

			if (ignoreUnbreaking) {

				if (amount >= stack.getMaxDamage()) {
					stack.shrink(1);
				} else {
					stack.setDamageValue(stack.getDamageValue() + amount);
				}

			} else {
				stack.hurtAndBreak(amount, serverWorld, null, item -> {
				});
			}

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.DAMAGE;
	}

}
