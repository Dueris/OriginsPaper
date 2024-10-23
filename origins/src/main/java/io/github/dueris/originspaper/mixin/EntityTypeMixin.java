package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.EntityLinkedType;
import io.github.dueris.originspaper.power.type.ModifyTypeTagPowerType;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements EntityLinkedType {

	@Unique
	private Entity apoli$currentEntity;

	@Override
	public Entity apoli$getEntity() {
		return apoli$currentEntity;
	}

	@Override
	public void apoli$setEntity(Entity entity) {
		this.apoli$currentEntity = entity;
	}

	@ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("RETURN"))
	private boolean apoli$inTagProxy(boolean original, TagKey<EntityType<?>> tag) {
		return original
			|| ModifyTypeTagPowerType.doesApply(this.apoli$getEntity(), tag);
	}

	@ModifyReturnValue(method = "is(Lnet/minecraft/core/HolderSet;)Z", at = @At("RETURN"))
	private boolean apoli$inTagEntryListProxy(boolean original, HolderSet<EntityType<?>> entryList) {
		return original
			|| ModifyTypeTagPowerType.doesApply(this.apoli$getEntity(), entryList);
	}

}
