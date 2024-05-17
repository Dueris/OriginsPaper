package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;

import static org.bukkit.Material.*;

public class PreventSleep extends PowerType {
	public static EnumSet<Material> beds;

	static {
		beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
			CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
	}

	private final FactoryJsonObject blockCondition;
	private final String message;
	private final boolean setSpawnPoint;

	public PreventSleep(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject blockCondition, String message, boolean setSpawnPoint) {
		super(name, description, hidden, condition, loading_priority);
		this.blockCondition = blockCondition;
		this.message = message;
		this.setSpawnPoint = setSpawnPoint;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_sleep"))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("message", String.class, "text.apoli.cannot_sleep")
			.add("set_spawn_point", boolean.class, false);
	}

	@EventHandler
	public void runD(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (e.getAction().isLeftClick()) return;
		if (!getPlayers().contains(e.getPlayer())) return;
		if (beds.contains(e.getClickedBlock().getType())) {
			Player player = e.getPlayer();
			Block clickedBlock = e.getClickedBlock();
			Location blockLocation = clickedBlock.getLocation();
			boolean meetsCondition = ConditionExecutor.testBlock(blockCondition, (CraftBlock) player.getLocation().getBlock());

			if (meetsCondition) {
				if (setSpawnPoint) {
					player.setBedSpawnLocation(blockLocation);
				}

				player.sendMessage(message.equalsIgnoreCase("origins.avian_sleep_fail") ? "You need fresh air to sleep" :
					message.equalsIgnoreCase("text.apoli.cannot_sleep") ? "You cannot sleep" : "text.apoli.cannot_sleep");
				e.setCancelled(true);
			}
		}
	}
}
