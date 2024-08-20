package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.mixin.ItemEnchantmentsMixin;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyEnchantmentLevelPower extends ModifierPower {
	private final ResourceKey<Enchantment> enchantment;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;

	public ModifyEnchantmentLevelPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
									   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ResourceKey<Enchantment> enchantment, ConditionFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.enchantment = enchantment;
		this.itemCondition = itemCondition;
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_enchantment_level"))
			.add("enchantment", SerializableDataTypes.ENCHANTMENT)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null);
	}

	@EventHandler
	public void onStackSwitch(@NotNull PlayerInventorySlotChangeEvent e) {
		ItemStack oldSlot = CraftItemStack.unwrap(e.getOldItemStack());
		ItemStack newStack = CraftItemStack.unwrap(e.getNewItemStack());
		if (ItemEnchantmentsMixin.TO_MODIFY.containsKey(oldSlot.getEnchantments())) {
			ItemEnchantmentsMixin.unmark(oldSlot.getEnchantments());
		}

		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && (itemCondition == null || itemCondition.test(new Tuple<>(player.level(), newStack)))) {
			ItemEnchantmentsMixin.mark(newStack.getEnchantments(), this);
		}
	}

	public ConditionFactory<Tuple<Level, ItemStack>> getItemCondition() {
		return itemCondition;
	}

	public ResourceKey<Enchantment> getEnchantment() {
		return enchantment;
	}
}
