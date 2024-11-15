package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

public class PreventEntityUsePowerType extends InteractionPowerType {

	public static final TypedDataObjectFactory<PreventEntityUsePowerType> DATA_FACTORY = InteractionPowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, condition) -> new PreventEntityUsePowerType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			heldItemAction,
			heldItemCondition,
			resultItemAction,
			resultStack,
			hands,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("bientity_condition", powerType.biEntityCondition)
	);

	private final Optional<BiEntityAction> biEntityAction;
	private final Optional<BiEntityCondition> biEntityCondition;

	public PreventEntityUsePowerType(Optional<BiEntityAction> biEntityAction, Optional<BiEntityCondition> biEntityCondition, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, Optional<EntityCondition> condition) {
		super(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, InteractionResult.FAIL, condition);
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ENTITY_USE;
	}

	public boolean doesApply(Entity other, InteractionHand hand, ItemStack heldStack) {
		return shouldExecute(hand, heldStack)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), other)).orElse(true);
	}

	public InteractionResult executeAction(Entity other, InteractionHand hand) {

		LivingEntity holder = getHolder();
		biEntityAction.ifPresent(action -> action.execute(holder, other));

		if (holder instanceof Player player) {
			this.performActorItemStuff(player, hand);
		}

		return this.getActionResult();

	}

}
