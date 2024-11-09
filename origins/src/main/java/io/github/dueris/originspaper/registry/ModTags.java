package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

	public static final TagKey<Item> MEAT = TagKey.create(Registries.ITEM, OriginsPaper.originIdentifier("meat"));
	public static final TagKey<Block> UNPHASABLE = TagKey.create(Registries.BLOCK, OriginsPaper.originIdentifier("unphasable"));
	public static final TagKey<Block> NATURAL_STONE = TagKey.create(Registries.BLOCK, OriginsPaper.originIdentifier("natural_stone"));
	public static final TagKey<Item> RANGED_WEAPONS = TagKey.create(Registries.ITEM, OriginsPaper.originIdentifier("ranged_weapons"));

	public static void register() {

	}

}
