package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ActiveInteractionPowerType extends InteractionPowerType implements Prioritized<ActiveInteractionPowerType> {

	private final int priority;

	public ActiveInteractionPowerType(Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, int priority, Optional<EntityCondition> condition) {
		super(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, condition);
		this.priority = priority;
	}

	public ActiveInteractionPowerType(Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, int priority) {
		this(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, Optional.empty());
	}

	public int getPriority() {
		return priority;
	}

	public static <T extends ActiveInteractionPowerType> TypedDataObjectFactory<T> createConditionedDataFactory(SerializableData serializableData, ActiveInteractionPowerType.FromData<T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return InteractionPowerType.createConditionedDataFactory(
			serializableData
				.add("priority", SerializableDataTypes.INT, 0),
			(data, heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, condition) -> fromData.apply(
				data,
				heldItemAction,
				heldItemCondition,
				resultItemAction,
				resultStack,
				hands,
				actionResult,
				data.get("priority"),
				condition
			),
			(t, _serializableData) -> toData.apply(t, _serializableData)
				.set("priority", t.getPriority())
		);
	}

	@FunctionalInterface
	public interface FromData<T extends ActiveInteractionPowerType> {
		T apply(SerializableData.Instance data, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, int priority, Optional<EntityCondition> condition);
	}

}
