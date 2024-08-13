package me.dueris.originspaper.data.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GuiTitle(@Nullable Component viewOrigin, @Nullable Component chooseOrigin) {

	public static final InstanceDefiner DATA = InstanceDefiner.instanceDefiner()
		.add("view_origin", SerializableDataTypes.KYORI_COMPONENT, null)
		.add("choose_origin", SerializableDataTypes.KYORI_COMPONENT, null);

	@Contract("_ -> new")
	public static @NotNull GuiTitle fromData(@NotNull DeserializedFactoryJson data) {
		return new GuiTitle(data.get("view_origin"), data.get("choose_origin"));
	}

}
