package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RestrictArmorPowerType extends PowerType implements Listener {

	protected final Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions;

	public RestrictArmorPowerType(Power power, LivingEntity entity, Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions) {
		super(power, entity);
		this.armorConditions = armorConditions;
	}

	public static @NotNull PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("restrict_armor"),
			new SerializableData()
				.add("head", ApoliDataTypes.ITEM_CONDITION, null)
				.add("chest", ApoliDataTypes.ITEM_CONDITION, null)
				.add("legs", ApoliDataTypes.ITEM_CONDITION, null)
				.add("feet", ApoliDataTypes.ITEM_CONDITION, null),
			data -> (power, entity) -> {

				Map<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> restrictions = new HashMap<>();

				if (data.isPresent("head")) {
					restrictions.put(EquipmentSlot.HEAD, data.get("head"));
				}

				if (data.isPresent("chest")) {
					restrictions.put(EquipmentSlot.CHEST, data.get("chest"));
				}

				if (data.isPresent("legs")) {
					restrictions.put(EquipmentSlot.LEGS, data.get("legs"));
				}

				if (data.isPresent("feet")) {
					restrictions.put(EquipmentSlot.FEET, data.get("feet"));
				}

				return new RestrictArmorPowerType(power, entity, restrictions);

			}
		);
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
	public void onGained() {
		dropEquippedStacks();
	}

	@Override
	public void tick() {

		dropEquippedStacks();

	}

	public void dropEquippedStacks() {

		for (EquipmentSlot slot : armorConditions.keySet()) {

			ItemStack equippedStack = entity.getItemBySlot(slot);

			if (!equippedStack.isEmpty() && this.shouldDrop(equippedStack, slot)) {
				moveEquipmentInventory((org.bukkit.entity.Player) entity.getBukkitEntity(), CraftEquipmentSlot.getSlot(slot));
			}

		}

	}

	public boolean shouldDrop(ItemStack stack, EquipmentSlot slot) {
		return this.doesRestrict(stack, slot);
	}

	public boolean doesRestrict(ItemStack stack, EquipmentSlot slot) {
		Predicate<Tuple<Level, ItemStack>> armorCondition = armorConditions.get(slot);
		return armorCondition != null && armorCondition.test(new Tuple<>(entity.level(), stack));
	}

	@EventHandler
	public void tickArmorChange(@NotNull PlayerArmorChangeEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		Player nms = ((CraftPlayer) p).getHandle();
		if (entity == nms && isActive() && !e.getNewItem().isEmpty()) {
			ItemStack stack = CraftItemStack.unwrap(e.getNewItem());
			EquipmentSlot slot = switch (e.getSlotType()) {
				case CHEST -> EquipmentSlot.CHEST;
				case LEGS -> EquipmentSlot.LEGS;
				case FEET -> EquipmentSlot.FEET;
				case HEAD -> EquipmentSlot.HEAD;
			};
			if (doesRestrict(stack, slot)) {
				moveEquipmentInventory(p, CraftEquipmentSlot.getSlot(slot));
			}
		}
	}

}

