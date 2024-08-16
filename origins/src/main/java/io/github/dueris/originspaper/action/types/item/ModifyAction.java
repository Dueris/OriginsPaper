package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ItemActionFactory;
import io.github.dueris.originspaper.util.ApoliLootContextTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Level, SlotAccess> worldAndStack) {

		if (!(worldAndStack.getA() instanceof ServerLevel serverWorld)) {
			return;
		}

		ResourceKey<LootItemFunction> itemModifierKey = data.get("modifier");
		LootItemFunction itemModifier = serverWorld.getServer().reloadableRegistries()
				.get()
				.registryOrThrow(Registries.ITEM_MODIFIER)
				.getOrThrow(itemModifierKey);

		SlotAccess stack = worldAndStack.getB();

		LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
				.withParameter(LootContextParams.ORIGIN, serverWorld.getSharedSpawnPos().getCenter())
				.withParameter(LootContextParams.TOOL, stack.get())
				.create(ApoliLootContextTypes.ANY);
		LootContext lootContext = new LootContext.Builder(lootContextParameterSet).create(Optional.empty());

		ItemStack newStack = itemModifier.apply(stack.get(), lootContext);
		stack.set(newStack);

	}

	public static @NotNull ItemActionFactory getFactory() {
		return new ItemActionFactory(
				OriginsPaper.apoliIdentifier("modify"),
				SerializableData.serializableData()
						.add("modifier", SerializableDataTypes.ITEM_MODIFIER),
				ModifyAction::action
		);
	}
}
