package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.PriorityPhase;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBeingUsedPowerType extends ActiveInteractionPowerType {

	public static final TypedDataObjectFactory<ActionOnBeingUsedPowerType> DATA_FACTORY = ActiveInteractionPowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, condition) -> new ActionOnBeingUsedPowerType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			heldItemAction,
			heldItemCondition,
			resultItemAction,
			resultStack,
			hands,
			actionResult,
			priority,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("bientity_condition", powerType.biEntityCondition)
	);

	private final Optional<BiEntityAction> biEntityAction;
	private final Optional<BiEntityCondition> biEntityCondition;

	public ActionOnBeingUsedPowerType(Optional<BiEntityAction> biEntityAction, Optional<BiEntityCondition> biEntityCondition, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, int priority, Optional<EntityCondition> condition) {
		super(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, condition);
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_BEING_USED;
	}

	public boolean shouldExecute(Player other, InteractionHand hand, ItemStack heldStack, PriorityPhase priorityPhase) {
		return priorityPhase.test(this.getPriority())
			&& super.shouldExecute(hand, heldStack)
			&& biEntityCondition.map(condition -> condition.test(other, getHolder())).orElse(true);
	}

	public InteractionResult executeAction(Player other, InteractionHand hand) {

		biEntityAction.ifPresent(action -> action.execute(other, getHolder()));
		performActorItemStuff(other, hand);

		return getActionResult();

	}

}
