package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RestrictArmorPowerType extends PowerType {

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
		setTicking(true);
	}

	public RestrictArmorPowerType(EnumMap<EquipmentSlot, Optional<ItemCondition>> armorConditions) {
		this(armorConditions, Optional.empty());
	}

	protected static SerializableData createSerializableData() {

		SerializableData serializableData = new SerializableData();
		Arrays.stream(EquipmentSlot.values())
			.filter(EquipmentSlot::isArmor)
			.forEach(slot -> serializableData.add(slot.getName(), ItemCondition.DATA_TYPE.optional(), Optional.empty()));

		return serializableData;

	}

	public static void moveEquipmentInventory(@NotNull org.bukkit.entity.Player player, org.bukkit.inventory.EquipmentSlot equipmentSlot) {
		org.bukkit.inventory.ItemStack item = player.getInventory().getItem(equipmentSlot);
		int emptySlot = player.getInventory().firstEmpty();
		if (emptySlot != -1) {
			player.getInventory().setItem(equipmentSlot, null);
			player.getInventory().setItem(emptySlot, item);
		} else {
			player.getWorld().dropItem(player.getLocation(), item);
			player.getInventory().setItem(equipmentSlot, null);
		}
		player.updateInventory();
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

			if (itemCondition.map(condition -> condition.test(getHolder().level(), equippedStack)).orElse(false)) {
				moveEquipmentInventory((org.bukkit.entity.Player) getHolder().getBukkitEntity(), CraftEquipmentSlot.getSlot(equipmentSlot));
			}

		}

	}
}
