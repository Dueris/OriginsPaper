package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.util.HudRender;
import org.bukkit.boss.KeyedBossBar;

import java.util.concurrent.atomic.AtomicReference;

public interface HudRendered {
	AtomicReference<KeyedBossBar> backboard = new AtomicReference<>(null);

	HudRender getRenderSettings();

	float getFill();

	boolean shouldRender();

	int getRuntimeMax();

}
