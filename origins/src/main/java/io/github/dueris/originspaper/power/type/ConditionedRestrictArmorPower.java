package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConditionedRestrictArmorPower extends RestrictArmorPower {
	private final int tickRate;

	private Integer startTicks;
	private Integer endTicks;

	private boolean wasActive;

	public ConditionedRestrictArmorPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
										 ConditionTypeFactory<Tuple<Level, ItemStack>> head, ConditionTypeFactory<Tuple<Level, ItemStack>> chest, ConditionTypeFactory<Tuple<Level, ItemStack>> legs, ConditionTypeFactory<Tuple<Level, ItemStack>> feet, int tickRate) {
		super(key, type, name, description, hidden, condition, loadingPriority, head, chest, legs, feet);
		this.tickRate = tickRate;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("conditioned_restrict_armor"), RestrictArmorPower.getFactory().getSerializableData()
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 80));
	}

	@Override
	public void tick(Player entity) {

		if (this.isActive(entity)) {

			if (startTicks == null) {

				this.startTicks = entity.tickCount % tickRate;
				this.endTicks = null;

			} else if (entity.tickCount % tickRate == startTicks) {
				dropEquippedStacks(entity);
				wasActive = true;
			}

		} else if (wasActive) {

			if (endTicks == null) {

				this.endTicks = entity.tickCount % tickRate;
				this.startTicks = null;

			} else if (entity.tickCount % tickRate == endTicks) {
				wasActive = false;
			}

		}

	}

	@SuppressWarnings("RedundantMethodOverride")
	@Override
	public boolean shouldDrop(ItemStack stack, EquipmentSlot slot, Entity entity) {
		return super.doesRestrict(stack, slot, entity);
	}

	@Override
	public boolean doesRestrict(ItemStack stack, EquipmentSlot slot, Entity entity) {
		return false;
	}
}
