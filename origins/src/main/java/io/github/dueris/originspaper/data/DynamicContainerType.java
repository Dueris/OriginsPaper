package io.github.dueris.originspaper.data;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.util.TextAlignment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;

public record DynamicContainerType(TextAlignment titleAlignment, ResourceLocation texture, int columns, int rows) implements ContainerType {

	public static final TypedDataObjectFactory<DynamicContainerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("title_alignment", ApoliDataTypes.TEXT_ALIGNMENT, TextAlignment.CENTER)
			.add("texture", SerializableDataTypes.IDENTIFIER)
			.add("columns", SerializableDataTypes.POSITIVE_INT)
			.add("rows", SerializableDataTypes.POSITIVE_INT),
		data -> new DynamicContainerType(
			data.get("title_alignment"),
			data.get("texture"),
			data.get("columns"),
			data.get("rows")
		),
		(containerType, serializableData) -> serializableData.instance()
			.set("title_alignment", containerType.titleAlignment())
			.set("texture", containerType.texture())
			.set("columns", containerType.columns())
			.set("rows", containerType.rows())
	);

	public DynamicContainerType {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

	@Override
	public MenuConstructor create(Container inventory) {
		throw new UnsupportedOperationException("Dynamic container types are currently not supported!");
	}

}
