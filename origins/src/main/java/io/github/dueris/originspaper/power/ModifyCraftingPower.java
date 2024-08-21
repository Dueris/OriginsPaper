package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.jetbrains.annotations.NotNull;

public class ModifyCraftingPower extends PowerType {
	private final ResourceLocation recipe;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ItemStack result;
	private final ActionFactory<Tuple<Level, SlotAccess>> itemAction;
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Triple<Level, BlockPos, Direction>> blockAction;

	public ModifyCraftingPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   ResourceLocation recipe, ConditionFactory<Tuple<Level, ItemStack>> itemCondition, ItemStack result, ActionFactory<Tuple<Level, SlotAccess>> itemAction, ActionFactory<Entity> entityAction,
							   ActionFactory<Triple<Level, BlockPos, Direction>> blockAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.recipe = recipe;
		this.itemCondition = itemCondition;
		this.result = result;
		this.itemAction = itemAction;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_crafting"))
			.add("recipe", SerializableDataTypes.IDENTIFIER, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("result", SerializableDataTypes.ITEM_STACK, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null);
	}

	@EventHandler
	public void onPrepareCraft(@NotNull PrepareItemCraftEvent e) {
		Player player = ((CraftPlayer) e.getView().getPlayer()).getHandle();
		if (getPlayers().contains(player) && isActive(player)) {

			if (e.getRecipe() == null) {
				return;
			}

			ItemStack currentResult = CraftItemStack.unwrap(e.getRecipe().getResult());
			if ((recipe == null || recipe.toString().equalsIgnoreCase(RecipePower.computeTag(e.getRecipe()))) &&
				(itemCondition == null || itemCondition.test(new Tuple<>(player.level(), currentResult)))) {
				Location bukkitPos = e.getInventory().getLocation();
				BlockPos pos = bukkitPos == null ? player.blockPosition() : CraftLocation.toBlockPosition(bukkitPos);

				ItemStack newResult = (result == null ? currentResult : result).copy();
				if (itemAction != null) {
					itemAction.accept(new Tuple<>(player.level(), Util.createStackReference(newResult.copy())));
				}

				e.getInventory().setResult(newResult.getBukkitStack());

				if (entityAction != null) {
					entityAction.accept(player);
				}

				if (blockAction != null) {
					blockAction.accept(Triple.of(player.level(), pos, Direction.UP));
				}
			}
		}
	}
}
