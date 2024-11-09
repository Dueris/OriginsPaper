package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

	public static final ResourceKey<Enchantment> WATER_PROTECTION = ResourceKey.create(Registries.ENCHANTMENT, OriginsPaper.originIdentifier("water_protection"));

}