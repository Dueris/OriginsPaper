package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.AdditionalEntityAttributesHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ModifySwimSpeedPower extends PowerType {
	private final List<Modifier> modifiers = new LinkedList<>();

	public ModifySwimSpeedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								@Nullable AttributeModifier modifier, @Nullable List<AttributeModifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority);

		if (modifier != null) {
			this.modifiers.add(ModifierUtil.fromAttributeModifier(modifier));
		}
		if (modifiers != null) {
			modifiers.stream().map(ModifierUtil::fromAttributeModifier).forEach(this.modifiers::add);
		}
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_swim_speed"), PowerType.getFactory().getSerializableData()
			.add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
			.add("modifiers", SerializableDataType.of(SerializableDataTypes.ATTRIBUTE_MODIFIER.listOf()), null));
	}

	@Override
	public void tick(@NotNull Player entity) {

		AdditionalEntityAttributesHolder holder = AdditionalEntityAttributesHolder.getOrCreateHolder(entity);

		if (entity.tickCount % 10 != 0) {
			if (holder.has(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)) {
				if (entity.getBukkitEntity().isSwimming()) {
					entity.getBukkitEntity().setVelocity(entity.getBukkitEntity().getLocation().getDirection().multiply(holder.get(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)));
				}
			}
			return;
		}

		if (this.isActive(entity)) {
			holder.set(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED, (float) ModifierUtil.applyModifiers(entity, this.modifiers, 0.4D));
		} else {
			holder.clear(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED);
		}

	}
}
