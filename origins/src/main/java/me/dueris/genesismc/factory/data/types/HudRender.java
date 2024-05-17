package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;

public class HudRender {
	private final boolean shouldRender;
	private final String spriteLocation;
	private final int barIndex;
	private final FactoryJsonObject condition;

	public HudRender(boolean shouldRender, String spriteLocation, int barIndex, FactoryJsonObject condition) {
		this.shouldRender = shouldRender;
		this.spriteLocation = spriteLocation;
		this.barIndex = barIndex;
		this.condition = condition;
	}

	public static HudRender createHudRender(FactoryJsonObject jsonObject) {
		return new HudRender(
			jsonObject.getBooleanOrDefault("should_render", true),
			jsonObject.getStringOrDefault("sprite_location", "origins:textures/gui/resource_bar.png"),
			jsonObject.getNumberOrDefault("bar_index", 0).getInt(),
			jsonObject.getJsonObject("condition")
		);
	}

	public boolean getShouldRender() {
		return shouldRender;
	}

	public String getSpriteLocation() {
		return spriteLocation;
	}

	public int getBarIndex() {
		return barIndex;
	}

	public FactoryJsonObject getCondition() {
		return condition;
	}
}
