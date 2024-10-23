package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.dueris.originspaper.access.OwnableAttributeContainer;
import io.github.dueris.originspaper.access.OwnableAttributeInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeSupplier.class)
public abstract class AttributeSupplierMixin implements OwnableAttributeContainer {

	@Unique
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

	@ModifyExpressionValue(method = "getValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;getAttributeInstance(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;"))
	private AttributeInstance apoli$setAttributeInstanceOwner(AttributeInstance original) {

		if (original instanceof OwnableAttributeInstance ownableAttributeInstance) {
			ownableAttributeInstance.apoli$setOwner(this.apoli$getOwner());
		}

		return original;

	}

}
