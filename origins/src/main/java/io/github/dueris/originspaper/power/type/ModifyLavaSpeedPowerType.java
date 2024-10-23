package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Collectors;

public class ModifyLavaSpeedPowerType extends ConditionedAttributePowerType {
	public static final double MAX_LAVA_SPEED = 5.0D;
	public static final double MIN_LAVA_SPEED = 0.0D;

	public ModifyLavaSpeedPowerType(Power power, LivingEntity entity) {
		super(power, entity, 10, false);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_lava_speed"),
			new SerializableData()
				.add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
				.add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null),
			data -> ModifyLavaSpeedPowerType::new
		).allowCondition();
	}

	@Override
	public void tick() {
		if (!entity.isFallFlying() && entity instanceof Player player && !player.getAbilities().flying) {
			BlockState state = player.getInBlockState();
			if (state.is(Blocks.LAVA)) {
				double newSpeed = ModifierUtil.applyModifiers(player, modifiers.stream().map(AttributedEntityAttributeModifier::modifier).map(ModifierUtil::fromAttributeModifier).collect(Collectors.toSet()), 0);
				player.getBukkitEntity().setVelocity(player.getBukkitEntity().getLocation().getDirection().multiply(newSpeed > MAX_LAVA_SPEED ? MAX_LAVA_SPEED : Math.max(newSpeed, MIN_LAVA_SPEED)));
			}
		}
	}

}
