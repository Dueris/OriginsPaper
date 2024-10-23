package io.github.dueris.originspaper.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.LinkedList;
import java.util.List;

public class AttributePowerType extends PowerType {

	protected final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();
	protected final boolean updateHealth;

	public AttributePowerType(Power power, LivingEntity entity, boolean updateHealth) {
		super(power, entity);
		this.updateHealth = updateHealth;
	}

	public AttributePowerType(Power power, LivingEntity entity, boolean updateHealth, Holder<Attribute> attribute, AttributeModifier modifier) {
		this(power, entity, updateHealth);
		addModifier(attribute, modifier);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("attribute"),
			new SerializableData()
				.add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
				.add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
				.add("update_health", SerializableDataTypes.BOOLEAN, true),
			data -> (power, entity) -> {

				AttributePowerType attributePower = new AttributePowerType(
					power,
					entity,
					data.get("update_health")
				);

				data.<AttributedEntityAttributeModifier>ifPresent("modifier", attributePower::addModifier);
				data.<List<AttributedEntityAttributeModifier>>ifPresent("modifiers", mods -> mods.forEach(attributePower::addModifier));

				return attributePower;

			}
		);
	}

	public AttributePowerType addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
		AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
		this.modifiers.add(mod);
		return this;
	}

	public AttributePowerType addModifier(AttributedEntityAttributeModifier modifier) {
		this.modifiers.add(modifier);
		return this;
	}

	@Override
	public void onAdded() {
		this.applyTempMods();
	}

	@Override
	public void onRemoved() {
		this.removeTempMods();
	}

	protected void applyTempMods() {

		if (entity.level().isClientSide) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousHealthPercent = entity.getHealth() / previousMaxHealth;

		modifiers.stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && !pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().addTransientModifier(pair.getFirst().modifier()));

		float currentMaxHealth = entity.getMaxHealth();
		if (updateHealth && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousHealthPercent);
		}

	}

	protected void removeTempMods() {

		if (entity.level().isClientSide) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousHealthPercent = entity.getHealth() / previousMaxHealth;

		modifiers.stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().removeModifier(pair.getFirst().modifier().id()));

		float currentMaxHealth = entity.getMaxHealth();
		if (updateHealth && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousHealthPercent);
		}

	}

}

