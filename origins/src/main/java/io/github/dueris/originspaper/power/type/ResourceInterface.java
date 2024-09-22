package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.data.types.HudRender;
import net.kyori.adventure.text.TextComponent;

public interface ResourceInterface {
	HudRender getHudRender();

	TextComponent name();

	String getTag();
}
