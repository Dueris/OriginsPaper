package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ModifyHarvestPower extends PowerType implements Listener {

	private final FactoryJsonObject blockCondition;
	private final boolean allow;

	public ModifyHarvestPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject blockCondition, boolean allow) {
		super(name, description, hidden, condition, loading_priority);
		this.blockCondition = blockCondition;
		this.allow = allow;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_harvest"))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("allow", boolean.class, new RequiredInstance());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void runD(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) return;
			if (isActive(p) && ConditionExecutor.testBlock(blockCondition, e.getBlock())) {
				if (e.isCancelled()) return;
				boolean willDrop = ((CraftPlayer) p).getHandle().hasCorrectToolForDrops(((CraftBlock) e.getBlock()).getNMS());
				if (allow && !willDrop) {
					e.getBlock().getDrops().forEach((itemStack -> p.getWorld().dropItemNaturally(e.getBlock().getLocation(), itemStack)));
				}
			}
		}
	}

}
