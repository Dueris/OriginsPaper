package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class BurnPower extends PowerType {
	private final int interval;
	private final int burnDuration;

	public BurnPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, int interval, int burnDuration) {
		super(name, description, hidden, condition, loading_priority);
		this.interval = interval;
		this.burnDuration = burnDuration;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("burn"))
			.add("interval", int.class, new RequiredInstance())
			.add("burn_duration", int.class, new RequiredInstance());
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getServer().getCurrentTick() % interval == 0) {
			if (p.isInWaterOrRainOrBubbleColumn()) return;
			if (p.getGameMode() == GameMode.CREATIVE) return;
			if (isActive(p)) {
				p.setFireTicks(burnDuration * 20);
			}

		}
	}

	public int getInterval() {
		return interval;
	}

	public int getBurnDuration() {
		return burnDuration;
	}
}
