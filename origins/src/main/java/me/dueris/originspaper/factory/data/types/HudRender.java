package me.dueris.originspaper.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record HudRender(boolean shouldRender, String spriteLocation, int barIndex, FactoryJsonObject condition) {
	@Contract("_ -> new")
	public static @NotNull HudRender createHudRender(@NotNull FactoryJsonObject jsonObject) {
		return new HudRender(
			jsonObject.getBooleanOrDefault("should_render", true),
			jsonObject.getStringOrDefault("sprite_location", "origins:textures/gui/resource_bar.png"),
			jsonObject.getNumberOrDefault("bar_index", 0).getInt(),
			jsonObject.getJsonObject("condition")
		);
	}

}
