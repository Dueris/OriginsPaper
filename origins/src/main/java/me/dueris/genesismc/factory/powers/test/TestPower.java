package me.dueris.genesismc.factory.powers.test;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;

import org.bukkit.craftbukkit.entity.CraftPlayer;

public class TestPower extends PowerType {
	private final String test;

	@Register
	public TestPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String test) {
		super(name, description, hidden, condition, loading_priority);
		this.test = test;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(GenesisMC.apoliIdentifier("testing"))
			.add("test", String.class, "AHHHH");
	}

	public String getTest() {
		return test;
	}

	@Override
	public void tick() {
		System.out.println(this.test + " WOW. players: [");
		this.getPlayers().stream().map(CraftPlayer::getName).forEach(System.out::println);
		System.out.println("]");
	}
}
