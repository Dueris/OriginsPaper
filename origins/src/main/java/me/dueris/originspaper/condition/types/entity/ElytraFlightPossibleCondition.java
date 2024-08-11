package me.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ElytraFlightPossibleCondition {

	public static boolean condition(DeserializedFactoryJson data, Entity entity) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return false;
		}
		boolean ability = true;
		if (data.getBoolean("check_ability")) {
			ItemStack equippedChestItem = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
			ability = equippedChestItem.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(equippedChestItem);
		}
		boolean state = true;
		if (data.getBoolean("check_state")) {
			state = !livingEntity.onGround() && !livingEntity.isFallFlying() && !livingEntity.isInWater() && !livingEntity.hasEffect(MobEffects.LEVITATION);
		}
		return ability && state;
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(OriginsPaper.apoliIdentifier("elytra_flight_possible"),
			InstanceDefiner.instanceDefiner()
				.add("check_state", SerializableDataTypes.BOOLEAN, false)
				.add("check_ability", SerializableDataTypes.BOOLEAN, true),
			ElytraFlightPossibleCondition::condition
		);
	}
}
