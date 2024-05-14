package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ExhaustPower extends PowerType {
	private final int interval;
	private final float exhaustion;

	@Register
	public ExhaustPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, int interval, float exhaustion) {
		super(name, description, hidden, condition, loading_priority);
		this.interval = interval;
		this.exhaustion = exhaustion;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("exhaust"))
			.add("interval", int.class, 20)
			.add("exhaustion", float.class, new RequiredInstance());
	}

	@Override
	public void tick(Player p) {
		if (p.getTicksLived() % interval == 0) {
			if (isActive(p)) {
				if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
					return;
				((CraftPlayer) p).getHandle().causeFoodExhaustion(exhaustion);
			}
		}
	}

	public int getInterval() {
		return interval;
	}

	public float getExhaustion() {
		return exhaustion;
	}
}
