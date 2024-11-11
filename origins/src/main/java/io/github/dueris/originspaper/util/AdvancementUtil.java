package io.github.dueris.originspaper.util;

import io.github.dueris.originspaper.mixin.AdvancementCommandsAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdvancementUtil {

	public static List<AdvancementHolder> selectEntries(AdvancementTree advancementManager, AdvancementHolder advancementEntry, AdvancementCommands.Mode selection) {

		AdvancementNode placedAdvancement = advancementManager.get(advancementEntry);
		if (placedAdvancement == null) {
			return List.of(advancementEntry);
		}

		List<AdvancementHolder> advancementEntries = new ArrayList<>();
		if (selection.parents) {

			for (AdvancementNode parent = placedAdvancement.parent(); parent != null; parent = parent.parent()) {
				advancementEntries.add(parent.holder());
			}

		}

		advancementEntries.add(advancementEntry);
		if (selection.children) {
			AdvancementCommandsAccessor.callAddChildren(placedAdvancement, advancementEntries);
		}

		return advancementEntries;

	}

	public static void processCriteria(AdvancementHolder advancementEntry, Collection<String> criteria, AdvancementCommands.Action operation, ServerPlayer serverPlayerEntity) {
		for (String criterion : criteria.stream().filter(c -> advancementEntry.value().criteria().containsKey(c)).toList()) {
			operation.performCriterion(serverPlayerEntity, advancementEntry, criterion);
		}
	}

	public static void processAdvancements(Collection<AdvancementHolder> advancementEntries, AdvancementCommands.Action operation, ServerPlayer serverPlayerEntity) {
		for (AdvancementHolder advancementEntry : advancementEntries) {
			operation.perform(serverPlayerEntity, advancementEntry);
		}
	}

}
