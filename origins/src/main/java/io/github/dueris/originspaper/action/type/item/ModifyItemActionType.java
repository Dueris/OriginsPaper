package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.loot.context.ApoliLootContextTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyItemActionType extends ItemActionType {

	public static final TypedDataObjectFactory<ModifyItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("modifier", SerializableDataTypes.ITEM_MODIFIER),
		data -> new ModifyItemActionType(
			data.get("modifier")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("modifier", actionType.modifier)
	);

	private final ResourceKey<LootItemFunction> modifier;

	public ModifyItemActionType(ResourceKey<LootItemFunction> modifier) {
		this.modifier = modifier;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {

		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		ItemStack oldStack = stackReference.get();
		LootItemFunction itemModifier = serverWorld.getServer().reloadableRegistries()
			.get()
			.registryOrThrow(Registries.ITEM_MODIFIER)
			.getOrThrow(modifier);

		LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
			.withParameter(LootContextParams.ORIGIN, serverWorld.getSharedSpawnPos().getCenter())
			.withParameter(LootContextParams.TOOL, oldStack)
			.withOptionalParameter(LootContextParams.THIS_ENTITY, ((EntityLinkedItemStack) oldStack).apoli$getEntity())
			.create(ApoliLootContextTypes.ANY);

		ItemStack newStack = itemModifier.apply(oldStack, new LootContext.Builder(lootContextParameterSet).create(Optional.empty()));
		stackReference.set(newStack);

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.MODIFY;
	}

}
