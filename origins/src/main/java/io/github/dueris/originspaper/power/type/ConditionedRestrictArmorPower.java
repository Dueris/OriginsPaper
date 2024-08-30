package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
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
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Predicate;

public class ConditionedRestrictArmorPower extends PowerType {
	private final HashMap<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions;
	private final int tickRate;

	public ConditionedRestrictArmorPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
										 ConditionTypeFactory<Tuple<Level, ItemStack>> head, ConditionTypeFactory<Tuple<Level, ItemStack>> chest, ConditionTypeFactory<Tuple<Level, ItemStack>> legs, ConditionTypeFactory<Tuple<Level, ItemStack>> feet, int tickRate) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.tickRate = tickRate;

		HashMap<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> restrictions = new HashMap<>();
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

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("conditioned_restrict_armor"))
			.add("head", ApoliDataTypes.ITEM_CONDITION, null)
			.add("chest", ApoliDataTypes.ITEM_CONDITION, null)
			.add("legs", ApoliDataTypes.ITEM_CONDITION, null)
			.add("feet", ApoliDataTypes.ITEM_CONDITION, null)
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 80);
	}

	private static void moveEquipmentInventory(@NotNull org.bukkit.entity.Player player, org.bukkit.inventory.EquipmentSlot equipmentSlot) {
		org.bukkit.inventory.ItemStack item = player.getInventory().getItem(equipmentSlot);
		if (item != null && item.getType() != Material.AIR) {
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

	public boolean canEquip(ItemStack itemStack, EquipmentSlot slot, @NotNull Entity entity) {
		return !armorConditions.get(slot).test(new Tuple<>(entity.level(), itemStack));
	}

	@EventHandler
	public void tickArmorChange(@NotNull PlayerArmorChangeEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		Player nms = ((CraftPlayer) p).getHandle();
		if (getPlayers().contains(nms)) {
			if (isActive(nms)) {
				tickPower(nms);
			}
		}
	}

	@Override
	public void tick(@NotNull Player entity) {
		if (entity.tickCount % tickRate == 0 && this.isActive(entity)) {
			tickPower(entity);
		}
	}

	private void tickPower(@NotNull Player entity) {
		for (EquipmentSlot slot : armorConditions.keySet()) {
			ItemStack equippedItem = entity.getItemBySlot(slot);
			if (!equippedItem.isEmpty()) {
				if (!canEquip(equippedItem, slot, entity)) {
					moveEquipmentInventory((org.bukkit.entity.Player) entity.getBukkitEntity(), CraftEquipmentSlot.getSlot(slot));
				}
			}
		}
	}
}
