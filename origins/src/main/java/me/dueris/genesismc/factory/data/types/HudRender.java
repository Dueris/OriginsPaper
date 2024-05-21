package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;

public record HudRender(boolean shouldRender, String spriteLocation, int barIndex, FactoryJsonObject condition) {

	public static HudRender createHudRender(FactoryJsonObject jsonObject) {
		return new HudRender(
			jsonObject.getBooleanOrDefault("should_render", true),
			jsonObject.getStringOrDefault("sprite_location", "origins:textures/gui/resource_bar.png"),
			jsonObject.getNumberOrDefault("bar_index", 0).getInt(),
			jsonObject.getJsonObject("condition")
		);
	}


}
