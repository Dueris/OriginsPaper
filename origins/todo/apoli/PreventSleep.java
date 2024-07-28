package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static org.bukkit.Material.*;

public class PreventSleep extends PowerType {
	public static EnumSet<Material> beds;

	static {
		beds = EnumSet.of(Material.WHITE_BED, Material.LIGHT_GRAY_BED, Material.GRAY_BED, Material.BLACK_BED, Material.BROWN_BED, Material.RED_BED, Material.ORANGE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.GREEN_BED,
			Material.CYAN_BED, Material.LIGHT_BLUE_BED, Material.BLUE_BED, Material.PURPLE_BED, Material.MAGENTA_BED, Material.PINK_BED);
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
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_sleep"))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("message", String.class, "text.apoli.cannot_sleep")
			.add("set_spawn_point", boolean.class, false);
	}

	@EventHandler
	public void runD(@NotNull PlayerInteractEvent e) {
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

				player.sendMessage(message.replace("origins.avian_sleep_fail", "You need fresh air to sleep")
					.replace("text.apoli.cannot_sleep", "You cannot sleep"));
				e.setCancelled(true);
			}
		}
	}
}
