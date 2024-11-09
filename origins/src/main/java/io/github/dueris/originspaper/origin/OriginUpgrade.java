package io.github.dueris.originspaper.origin;

import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated
public record OriginUpgrade(ResourceLocation advancementCondition, ResourceLocation upgradeToOrigin,
							@Nullable String announcement) {

	public static final CompoundSerializableDataType<OriginUpgrade> DATA_TYPE = SerializableDataType.compound(
		new SerializableData()
			.add("condition", SerializableDataTypes.IDENTIFIER)
			.add("origin", SerializableDataTypes.IDENTIFIER)
			.add("announcement", SerializableDataTypes.STRING, null),
		data -> new OriginUpgrade(
			data.get("condition"),
			data.get("origin"),
			data.get("announcement")
		),
		(originUpgrade, serializableData) -> serializableData.instance()
			.set("condition", originUpgrade.advancementCondition())
			.set("origin", originUpgrade.upgradeToOrigin())
			.set("announcement", originUpgrade.announcement())
	);

}
