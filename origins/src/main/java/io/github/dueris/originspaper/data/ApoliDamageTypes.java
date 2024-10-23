package io.github.dueris.originspaper.data;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface ApoliDamageTypes {
	ResourceKey<DamageType> SYNC_DAMAGE_SOURCE = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.apoliIdentifier("sync_damage_source"));
}
