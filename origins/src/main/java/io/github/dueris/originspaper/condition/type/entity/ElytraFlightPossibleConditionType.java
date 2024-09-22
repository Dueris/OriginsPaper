package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.type.ElytraFlightPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraFlightPossibleConditionType {

	public static boolean condition(Entity entity, boolean checkState, boolean checkAbility) {

		if (!(entity instanceof LivingEntity living)) {
			return false;
		}

		boolean state = true;
		boolean ability = true;

		if (checkAbility) {
			ItemStack equippedChestStack = living.getItemBySlot(EquipmentSlot.CHEST);
			ability = (equippedChestStack.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(equippedChestStack) ||
				PowerHolderComponent.doesHaveConditionedPower(living.getBukkitEntity(), ElytraFlightPower.class, (p) -> p.getGlidingPlayers().contains(living.getBukkitEntity())));
		}

		if (checkState) {
			state = !living.onGround()
				&& !living.isFallFlying()
				&& !living.isInWater()
				&& !living.hasEffect(MobEffects.LEVITATION);
		}

		return ability && state;

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(OriginsPaper.apoliIdentifier("elytra_flight_possible"),
			new SerializableData()
				.add("check_state", SerializableDataTypes.BOOLEAN, false)
				.add("check_ability", SerializableDataTypes.BOOLEAN, true),
			(data, entity) -> condition(entity,
				data.get("check_state"),
				data.get("check_ability")
			)
		);
	}
}
