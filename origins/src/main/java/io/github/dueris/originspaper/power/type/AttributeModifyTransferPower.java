package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttributeModifyTransferPower extends PowerType {
	private final Holder<Attribute> attribute;
	private final double multiplier;
	private final Class<? extends PowerType> powerTypeClass;

	public AttributeModifyTransferPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
										@NotNull String stringClass, Holder<Attribute> attribute, double multiplier) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.attribute = attribute;
		this.multiplier = multiplier;
		this.powerTypeClass = switch (stringClass) {
			case "modify_air_speed" -> ModifyAirSpeedPower.class;
			case "modify_break_speed" -> ModifyBreakSpeedPower.class;
			case "modify_crafting" -> ModifyCraftingPower.class;
			case "modify_damage_dealt" -> ModifyDamageDealtPower.class;
			case "modify_damage_taken" -> ModifyDamageTakenPower.class;
			case "modify_exhaustion" -> ModifyExhaustionPower.class;
			case "modify_xp_gain" -> ModifyExperiencePower.class;
			case "modify_jump" -> ModifyJumpPower.class;
			case "modify_lava_speed" -> ModifyLavaSpeedPower.class;
			case "modify_projectile_damage" -> ModifyProjectileDamagePower.class;
			case "modify_status_effect_amplifier" -> ModifyStatusEffectAmplifierPower.class;
			case "modify_status_effect_duration" -> ModifyStatusEffectDurationPower.class;
			case "modify_swim_speed" -> ModifySwimSpeedPower.class;
			default -> throw new IllegalArgumentException("Unknown modifier type: " + type);
		};
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("attribute_modify_transfer"))
			.add("class", SerializableDataTypes.STRING)
			.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY)
			.add("multiplier", SerializableDataTypes.DOUBLE, 1.0);
	}

	public boolean doesApply(@NotNull Class<?> clz) {
		return clz.equals(powerTypeClass);
	}

	public void addModifiers(List<Modifier> modifiers, @NotNull LivingEntity entity) {
		AttributeMap attrContainer = entity.getAttributes();
		if (attrContainer.hasAttribute(attribute)) {
			AttributeInstance attributeInstance = attrContainer.getInstance(attribute);
			attributeInstance.getModifiers().forEach(mod -> {
				AttributeModifier transferMod =
					new AttributeModifier(mod.id(), mod.amount() * multiplier, mod.operation());
				modifiers.add(ModifierUtil.fromAttributeModifier(transferMod));
			});
		}
	}

	public void apply(List<AttributeModifier> modifiers, @NotNull LivingEntity entity) {
		AttributeMap attrContainer = entity.getAttributes();
		if (attrContainer.hasAttribute(attribute)) {
			AttributeInstance attributeInstance = attrContainer.getInstance(attribute);
			attributeInstance.getModifiers().forEach(mod -> {
				AttributeModifier transferMod =
					new AttributeModifier(mod.id(), mod.amount() * multiplier, mod.operation());
				modifiers.add(transferMod);
			});
		}
	}
}
