package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Util;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.BinaryOperator;

public class ModifySwimSpeedPower extends ModifierPower {

	public ModifySwimSpeedPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_swim_speed"));
	}

	@Override
	public void tick(Player p) {
		Block be = p.getLocation().getBlock();
		if (!getPlayers().contains(p) || p.isFlying() || be == null ||
			!p.getLocation().getBlock().isLiquid() || !p.isSwimming()) return;
		float multiplyBy = 0.6F;
		for (Modifier modifier : getModifiers()) {
			Map<String, BinaryOperator<Float>> floatBinaryOperator = Util.getOperationMappingsFloat();
			floatBinaryOperator.get(modifier.operation()).apply(multiplyBy, modifier.value() * 10f);
		}
		p.setVelocity(p.getLocation().getDirection().multiply(multiplyBy));
	}
}
