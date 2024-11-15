package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyCraftingPowerType extends PowerType implements Prioritized<ModifyCraftingPowerType> {

	public static final TypedDataObjectFactory<ModifyCraftingPowerType> DATA_FACTORY = createConditionedDataFactory(
		new SerializableData()
			.add("recipe", SerializableDataTypes.IDENTIFIER.optional(), Optional.empty())
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_action_after_crafting", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("result", SerializableDataTypes.ITEM_STACK.optional(), Optional.empty())
			.addFunctionedDefault("result_stack", SerializableDataTypes.ITEM_STACK.optional(), data -> data.get("result"))
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new ModifyCraftingPowerType(
			data.get("recipe"),
			data.get("entity_action"),
			data.get("block_action"),
			data.get("item_action"),
			data.get("item_action_after_crafting"),
			data.get("item_condition"),
			data.get("result_stack"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("recipe", powerType.recipeId)
			.set("entity_action", powerType.entityAction)
			.set("block_action", powerType.blockAction)
			.set("item_action", powerType.itemAction)
			.set("item_action_after_crafting", powerType.itemActionAfterCrafting)
			.set("item_condition", powerType.itemCondition)
			.set("result_stack", powerType.resultStack)
			.set("priority", powerType.getPriority())
	);

	private final Optional<ResourceLocation> recipeId;

	private final Optional<EntityAction> entityAction;
	private final Optional<BlockAction> blockAction;

	private final Optional<ItemAction> itemAction;
	private final Optional<ItemAction> itemActionAfterCrafting;

	private final Optional<ItemCondition> itemCondition;

	private final Optional<ItemStack> resultStack;
	private final int priority;

	public ModifyCraftingPowerType(Optional<ResourceLocation> recipeId, Optional<EntityAction> entityAction, Optional<BlockAction> blockAction, Optional<ItemAction> itemAction, Optional<ItemAction> itemActionAfterCrafting, Optional<ItemCondition> itemCondition, Optional<ItemStack> resultStack, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.recipeId = recipeId;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.itemAction = itemAction;
		this.itemActionAfterCrafting = itemActionAfterCrafting;
		this.itemCondition = itemCondition;
		this.resultStack = resultStack;
		this.priority = priority;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_CRAFTING;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(@NotNull ResourceLocation targetRecipeId, ItemStack originalResultStack) {
		return recipeId.map(targetRecipeId::equals).orElse(true)
			&& itemCondition.map(condition -> condition.test(getHolder().level(), originalResultStack)).orElse(true);
	}

	public void executeActions(@NotNull Optional<BlockPos> craftingBlockPos) {
		craftingBlockPos.ifPresent(pos -> blockAction.ifPresent(action -> action.execute(getHolder().level(), pos, Optional.empty())));
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

	public Optional<BlockAction> getBlockAction() {
		return blockAction;
	}

	public Optional<EntityAction> getEntityAction() {
		return entityAction;
	}

	public Optional<ItemAction> getItemActionAfterCrafting() {
		return itemActionAfterCrafting;
	}

	public Optional<ItemStack> getResultStack() {
		return resultStack;
	}

	public Optional<ItemAction> getItemAction() {
		return itemAction;
	}
}
