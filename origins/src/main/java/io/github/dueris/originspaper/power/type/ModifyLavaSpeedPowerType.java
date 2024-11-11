package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModifyLavaSpeedPowerType extends ConditionedAttributePowerType {
	public static final double MAX_LAVA_SPEED = 5.0D;
	public static final double MIN_LAVA_SPEED = 0.0D;

	public static final TypedDataObjectFactory<ModifyLavaSpeedPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
			.addFunctionedDefault("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIER.list(1, Integer.MAX_VALUE), data -> Util.singletonListOrNull(data.get("modifier")))
			.validate(Util.validateAnyFieldsPresent("modifier", "modifiers")),
		(data, condition) -> new ModifyLavaSpeedPowerType(
			data.get("modifiers"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("modifiers", powerType.attributeModifiers)
	);

	protected final List<AttributeModifier> attributeModifiers;

	public ModifyLavaSpeedPowerType(List<AttributeModifier> attributeModifiers, Optional<EntityCondition> condition) {
		super(List.of(), false, 10, condition);
		this.attributeModifiers = attributeModifiers;
	}

	@Override
	public void serverTick() {
		if (!getHolder().isFallFlying() && getHolder() instanceof Player player && !player.getAbilities().flying) {
			BlockState state = player.getInBlockState();
			if (state.is(Blocks.LAVA)) {
				double newSpeed = ModifierUtil.applyModifiers(player, attributeModifiers.stream().map(ModifierUtil::fromAttributeModifier).collect(Collectors.toSet()), 0);
				player.getBukkitEntity().setVelocity(player.getBukkitEntity().getLocation().getDirection().multiply(newSpeed > MAX_LAVA_SPEED ? MAX_LAVA_SPEED : Math.max(newSpeed, MIN_LAVA_SPEED)));
			}
		}
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_LAVA_SPEED;
	}

}
