package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.InventoryUtil;
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

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RestrictArmorPowerType extends PowerType implements Listener {

	public static final TypedDataObjectFactory<RestrictArmorPowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		createSerializableData(),
		data -> {

			EnumMap<EquipmentSlot, Optional<ItemCondition>> conditions = Arrays.stream(EquipmentSlot.values())
				.filter(EquipmentSlot::isArmor)
				.collect(Collectors.toMap(Function.identity(), slot -> data.get(slot.getName()), (o1, o2) -> o2, () -> new EnumMap<>(EquipmentSlot.class)));

			return new RestrictArmorPowerType(conditions);

		},
		(powerType, serializableData) -> {

			SerializableData.Instance data = serializableData.instance();
			powerType.armorConditions.forEach((equipmentSlot, itemCondition) -> data.set(equipmentSlot.getName(), itemCondition));

			return data;

		}
	);

	protected final EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions;

	public RestrictArmorPowerType(EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions, Optional<EntityCondition> condition) {
		super(condition);
		this.armorConditions = armorConditions;
	}

	public RestrictArmorPowerType(EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions) {
		this(armorConditions, Optional.empty());
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.RESTRICT_ARMOR;
	}

	@Override
	public void onGained() {
		dropEquippedStacks();
	}

	@Override
	public void serverTick() {

		dropEquippedStacks();

	}

	public boolean doesRestrict(ItemStack stack, EquipmentSlot slot) {
		return armorConditions.getOrDefault(slot, Optional.empty())
			.map(condition -> condition.test(getHolder().level(), stack))
			.orElse(false);
	}

	public void dropEquippedStacks() {

		for (Map.Entry<EquipmentSlot, Optional<ItemCondition>> armorConditionEntry : armorConditions.entrySet()) {

			EquipmentSlot equipmentSlot = armorConditionEntry.getKey();
			Optional<ItemCondition> itemCondition = armorConditionEntry.getValue();

			ItemStack equippedStack = getHolder().getItemBySlot(equipmentSlot);

			if (equippedStack.isEmpty() && itemCondition.map(condition -> condition.test(getHolder().level(), equippedStack)).orElse(false)) {
				moveEquipmentInventory((org.bukkit.entity.Player) getHolder().getBukkitEntity(), CraftEquipmentSlot.getSlot(equipmentSlot));
			}

		}

	}

	@EventHandler
	public void tickArmorChange(@NotNull PlayerArmorChangeEvent e) {
		org.bukkit.entity.Player p = e.getPlayer();
		Player nms = ((CraftPlayer) p).getHandle();
		if (getHolder() == nms && isActive() && !e.getNewItem().isEmpty()) {
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

	protected static SerializableData createSerializableData() {

		SerializableData serializableData = new SerializableData();
		Arrays.stream(EquipmentSlot.values())
			.filter(EquipmentSlot::isArmor)
			.forEach(slot -> serializableData.add(slot.getName(), ItemCondition.DATA_TYPE.optional(), Optional.empty()));

		return serializableData;

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
}
