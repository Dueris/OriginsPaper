package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConditionedAttributePower extends AttributePower {
	private final int tickRate;

	public ConditionedAttributePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition,
									 int loadingPriority, @Nullable AttributedEntityAttributeModifier modifier, @Nullable List<AttributedEntityAttributeModifier> modifiers, boolean updateHealth, int tickRate) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers, updateHealth);
		this.tickRate = tickRate;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("conditioned_attribute"), AttributePower.getFactory().getSerializableData()
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 20));
	}

	@Override
	public void tick(@NotNull Player entity) {

		if (entity.tickCount % tickRate != 0) {
			return;
		}

		if (this.isActive(entity)) {
			this.applyTempMods(entity);
		} else {
			this.removeTempMods(entity);
		}

	}
}
