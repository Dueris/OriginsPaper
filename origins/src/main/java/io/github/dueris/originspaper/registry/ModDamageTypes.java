package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {

	public static final ResourceKey<DamageType> NO_WATER_FOR_GILLS = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.originIdentifier("no_water_for_gills"));

}
