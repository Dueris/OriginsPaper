package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.OwnableAttributeInstance;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyAttributePowerType;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements OwnableAttributeInstance {

	@Unique
	@Nullable
	Entity apoli$owner;

	@Shadow
	public abstract Set<AttributeModifier> getModifiers();

	@Shadow
	public abstract double getBaseValue();

	@Shadow
	public abstract Holder<Attribute> getAttribute();

	@Override
	public void apoli$setOwner(Entity owner) {
		apoli$owner = owner;
	}

	@Override
	public Entity apoli$getOwner() {
		return apoli$owner;
	}

	@ModifyReturnValue(method = "getValue", at = @At("RETURN"))
	private double apoli$modifyAttribute(double original) {

		List<Modifier> powerModifiers = PowerHolderComponent.getPowerTypes(this.apoli$getOwner(), ModifyAttributePowerType.class)
			.stream()
			.filter(p -> p.getAttribute() == this.getAttribute())
			.flatMap(p -> p.getModifiers().stream())
			.toList();

		if (powerModifiers.isEmpty()) {
			return original;
		}

		List<Modifier> vanillaModifiers = this.getModifiers()
			.stream()
			.map(ModifierUtil::fromAttributeModifier)
			.toList();

		return ModifierUtil.applyModifiers(this.apoli$getOwner(), Stream.concat(powerModifiers.stream(), vanillaModifiers.stream()).toList(), this.getBaseValue());

	}

}

