package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
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

import java.util.Optional;

public class ModifyActionType {

	public static void action(Level world, SlotAccess stackReference, ResourceKey<LootItemFunction> itemModifierKey) {

		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		ItemStack oldStack = stackReference.get();
		LootItemFunction itemModifier = serverWorld.getServer().reloadableRegistries()
			.get()
			.registryOrThrow(Registries.ITEM_MODIFIER)
			.getOrThrow(itemModifierKey);

		LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
			.withParameter(LootContextParams.ORIGIN, serverWorld.getSharedSpawnPos().getCenter())
			.withParameter(LootContextParams.TOOL, oldStack)
			.withOptionalParameter(LootContextParams.THIS_ENTITY, ((EntityLinkedItemStack) oldStack).apoli$getEntity())
			.create(ApoliLootContextTypes.ANY);

		ItemStack newStack = itemModifier.apply(oldStack, new LootContext.Builder(lootContextParameterSet).create(Optional.empty()));
		stackReference.set(newStack);

	}

	public static ItemActionTypeFactory getFactory() {
		return new ItemActionTypeFactory(
			OriginsPaper.apoliIdentifier("modify"),
			new SerializableData()
				.add("modifier", SerializableDataTypes.ITEM_MODIFIER),
			(data, worldAndStackRef) -> action(worldAndStackRef.getA(), worldAndStackRef.getB(),
				data.get("modifier")
			)
		);
	}

}
