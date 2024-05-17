package me.dueris.genesismc.factory.powers.test;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;

@RequiresPlugin(pluginName = "skinsrestorer")
public class TestPluginRequirementPower extends TestPower {

	public TestPluginRequirementPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String test) {
		super(name, description, hidden, condition, loading_priority, test);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(GenesisMC.apoliIdentifier("requires_plugin"));
	}

	@Override
	public void tick() {
		System.out.println("not ticked unless plugin is present");
		System.out.println("plugin present is : " + this.getClass().getAnnotation(RequiresPlugin.class).pluginName());
	}
}
