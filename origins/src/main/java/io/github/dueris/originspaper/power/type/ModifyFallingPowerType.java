package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyFallingPowerType extends ModifierPower {
	private final boolean takeFallDamage;

	public ModifyFallingPowerType(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, double velocity, boolean takeFallDamage) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.takeFallDamage = takeFallDamage;

		this.addModifier(Modifier.of(ModifierOperation.SET_TOTAL, velocity));
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_falling"))
			.add("velocity", SerializableDataTypes.DOUBLE)
			.add("take_fall_damage", SerializableDataTypes.BOOLEAN, true);
	}

	public boolean shouldTakeFallDamage() {
		return takeFallDamage;
	}
}
