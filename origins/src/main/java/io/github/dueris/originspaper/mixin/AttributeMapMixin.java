package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.access.OwnableAttributeContainer;
import io.github.dueris.originspaper.access.OwnableAttributeInstance;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeMap.class)
public abstract class AttributeMapMixin implements OwnableAttributeContainer {

	@Shadow
	@Final
	private AttributeSupplier supplier;
	@Unique
	@Nullable
	private Entity apoli$owner;

	@Override
	@Nullable
	public Entity apoli$getOwner() {
		return apoli$owner;
	}

	@Override
	public void apoli$setOwner(Entity owner) {
		this.apoli$owner = owner;
	}

	@Inject(method = "getInstance", at = @At("RETURN"))
	private void apoli$setCustomAttributeInstanceOwner(Holder<Attribute> attribute, CallbackInfoReturnable<AttributeInstance> cir) {

		if (cir.getReturnValue() instanceof OwnableAttributeInstance ownableAttributeInstance) {
			ownableAttributeInstance.apoli$setOwner(this.apoli$getOwner());
		}

	}

	@Inject(method = "getValue", at = @At("RETURN"))
	private void apoli$setAttributeInstanceOwner(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir, @Local AttributeInstance attributeInstance) {

		if (attributeInstance instanceof OwnableAttributeInstance ownableAttributeInstance) {
			ownableAttributeInstance.apoli$setOwner(this.apoli$getOwner());
		}

		if (this.supplier instanceof OwnableAttributeContainer ownableAttributeContainer) {
			ownableAttributeContainer.apoli$setOwner(this.apoli$getOwner());
		}

	}

}
