package io.github.dueris.originspaper.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface AttributeModifying {

	List<AttributedEntityAttributeModifier> attributedModifiers();

	boolean shouldUpdateHealth();

	default void applyTempModifiers(LivingEntity entity) {

		if (entity.level().isClientSide()) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousMaxHealthPercent = entity.getHealth() / previousMaxHealth;

		attributedModifiers().stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && !pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().addTransientModifier(pair.getFirst().modifier()));

		float currentMaxHealth = entity.getMaxHealth();

		if (shouldUpdateHealth() && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousMaxHealthPercent);
		}

	}

	default void removeTempModifiers(LivingEntity entity) {

		if (entity.level().isClientSide()) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousMaxHealthPercent = entity.getHealth() / previousMaxHealth;

		attributedModifiers().stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().removeModifier(pair.getFirst().modifier()));

		float currentMaxHealth = entity.getMaxHealth();

		if (shouldUpdateHealth() && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousMaxHealthPercent);
		}

	}

}
