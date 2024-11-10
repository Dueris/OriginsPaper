package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModBlocks {

	/* public static final TemporaryCobwebBlock TEMPORARY_COBWEB = register("temporary_cobweb", false, () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.of()
		.mapColor(MapColor.WOOL)
		.strength(4.0F)
		.requiresCorrectToolForDrops()
		.noCollission()
		.forceSolidOn())); */ // TODO

	public static void register() {

	}

	private static <B extends Block> @NotNull B register(String name, boolean withBlockItem, @NotNull Supplier<B> blockSupplier) {

		ResourceLocation blockId = OriginsPaper.originIdentifier(name);
		B block = Registry.register(BuiltInRegistries.BLOCK, blockId, blockSupplier.get());

		if (withBlockItem) {
			Registry.register(BuiltInRegistries.ITEM, blockId, new BlockItem(block, new Item.Properties()));
		}

		return block;

	}

}
