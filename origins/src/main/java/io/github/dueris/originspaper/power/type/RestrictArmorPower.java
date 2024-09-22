package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RestrictArmorPower extends PowerType {
	protected final Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions;

	public RestrictArmorPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ConditionTypeFactory<Tuple<Level, ItemStack>> head, ConditionTypeFactory<Tuple<Level, ItemStack>> chest, ConditionTypeFactory<Tuple<Level, ItemStack>> legs, ConditionTypeFactory<Tuple<Level, ItemStack>> feet) {
		super(key, type, name, description, hidden, condition, loadingPriority);

		Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> restrictions = new HashMap<>();

		if (head != null) {
			restrictions.put(EquipmentSlot.HEAD, head);
		}

		if (chest != null) {
			restrictions.put(EquipmentSlot.CHEST, chest);
		}

		if (legs != null) {
			restrictions.put(EquipmentSlot.LEGS, legs);
		}

		if (feet != null) {
			restrictions.put(EquipmentSlot.FEET, feet);
		}

		this.armorConditions = restrictions;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("restrict_armor"), PowerType.getFactory().getSerializableData()
			.add("head", ApoliDataTypes.ITEM_CONDITION, null)
			.add("chest", ApoliDataTypes.ITEM_CONDITION, null)
			.add("legs", ApoliDataTypes.ITEM_CONDITION, null)
			.add("feet", ApoliDataTypes.ITEM_CONDITION, null));
	}

	private static void moveEquipmentInventory(@NotNull org.bukkit.entity.Player player, org.bukkit.inventory.EquipmentSlot equipmentSlot) {
		org.bukkit.inventory.ItemStack item = player.getInventory().getItem(equipmentSlot);
		if (item.getType() != Material.AIR) {
			int emptySlot = player.getInventory().firstEmpty();
			if (emptySlot != -1) {
				player.getInventory().setItem(equipmentSlot, null);
				player.getInventory().setItem(emptySlot, item);
			} else {
				player.getWorld().dropItem(player.getLocation(), item);
				player.getInventory().setItem(equipmentSlot, null);
			}
		}
	}

	@Override
	public void onAdded(Player player) {
		dropEquippedStacks(player);
	}

	public void dropEquippedStacks(Player entity) {

		for (EquipmentSlot slot : armorConditions.keySet()) {

			ItemStack equippedStack = entity.getItemBySlot(slot);

			if (!equippedStack.isEmpty() && this.shouldDrop(equippedStack, slot, entity)) {
				moveEquipmentInventory((org.bukkit.entity.Player) entity.getBukkitEntity(), CraftEquipmentSlot.getSlot(slot));
			}

		}

	}

	public boolean shouldDrop(ItemStack stack, EquipmentSlot slot, Entity entity) {
		return this.doesRestrict(stack, slot, entity);
	}

	public boolean doesRestrict(ItemStack stack, EquipmentSlot slot, Entity entity) {
		Predicate<Tuple<Level, ItemStack>> armorCondition = armorConditions.get(slot);
		return armorCondition != null && armorCondition.test(new Tuple<>(entity.level(), stack));
	}

	@Override
	public void tick(Player player) {
		dropEquippedStacks(player);
	}

	@EventHandler
	public void tickArmorChange(@NotNull PlayerArmorChangeEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		Player nms = ((CraftPlayer) p).getHandle();
		if (isActive(nms) && !e.getNewItem().isEmpty()) {
			ItemStack stack = CraftItemStack.unwrap(e.getNewItem());
			EquipmentSlot slot = switch (e.getSlotType()) {
				case CHEST -> EquipmentSlot.CHEST;
				case LEGS -> EquipmentSlot.LEGS;
				case FEET -> EquipmentSlot.FEET;
				case HEAD -> EquipmentSlot.HEAD;
			};
			if (doesRestrict(stack, slot, nms)) {
				moveEquipmentInventory(p, CraftEquipmentSlot.getSlot(slot));
			}
		}
	}
}
