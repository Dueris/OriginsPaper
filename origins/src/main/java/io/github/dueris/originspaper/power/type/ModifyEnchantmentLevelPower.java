package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyEnchantmentLevelPower extends ModifierPower {
	public static Map<ItemEnchantments, ModifyEnchantmentLevelPower> TO_MODIFY = new ConcurrentHashMap<>();
	private final ResourceKey<Enchantment> enchantment;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;

	public ModifyEnchantmentLevelPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ResourceKey<Enchantment> enchantment, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.enchantment = enchantment;
		this.itemCondition = itemCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_enchantment_level"), ModifierPower.getFactory().getSerializableData()
			.add("enchantment", SerializableDataTypes.ENCHANTMENT)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null));
	}

	public static void mark(ItemEnchantments itemEnchantments, ModifyEnchantmentLevelPower power) {
		TO_MODIFY.put(itemEnchantments, power);
	}

	public static void unmark(ItemEnchantments itemEnchantments) {
		TO_MODIFY.remove(itemEnchantments);
	}

	@EventHandler
	public void onStackSwitch(@NotNull PlayerInventorySlotChangeEvent e) {
		ItemStack oldSlot = CraftItemStack.unwrap(e.getOldItemStack());
		ItemStack newStack = CraftItemStack.unwrap(e.getNewItemStack());
		if (TO_MODIFY.containsKey(oldSlot.getEnchantments())) {
			unmark(oldSlot.getEnchantments());
		}

		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && (itemCondition == null || itemCondition.test(new Tuple<>(player.level(), newStack)))) {
			mark(newStack.getEnchantments(), this);
		}
	}

	public ConditionTypeFactory<Tuple<Level, ItemStack>> getItemCondition() {
		return itemCondition;
	}

	public ResourceKey<Enchantment> getEnchantment() {
		return enchantment;
	}
}
