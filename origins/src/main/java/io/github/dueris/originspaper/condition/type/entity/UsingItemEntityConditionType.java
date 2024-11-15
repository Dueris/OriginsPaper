package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UsingItemEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<UsingItemEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty()),
		data -> new UsingItemEntityConditionType(
			data.get("item_condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("item_condition", conditionType.itemCondition)
	);

	private final Optional<ItemCondition> itemCondition;

	public UsingItemEntityConditionType(Optional<ItemCondition> itemCondition) {
		this.itemCondition = itemCondition;
	}

	@Override
	public boolean test(Entity entity) {

		if (entity instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) {

			InteractionHand activeHand = livingEntity.getUsedItemHand();
			ItemStack stackInHand = livingEntity.getItemInHand(activeHand);

			return itemCondition
				.map(condition -> condition.test(entity.level(), stackInHand))
				.orElse(true);

		} else {
			return false;
		}

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.USING_ITEM;
	}

}
