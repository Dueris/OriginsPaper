package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.Material.AIR;

public class Climbing extends CraftPower {

	public ArrayList<Player> active_climbing = new ArrayList<>();


	public boolean isActiveClimbing(Player player) {
		return active_climbing.contains(player);
	}

	public ArrayList<Player> getActiveClimbingMap() {
		return active_climbing;
	}

	@Override
	public void run(Player p) {
		if (climbing.contains(p)) {
			if ((p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
				p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
				p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
				p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid() ||
				p.getEyeLocation().add(0, 1, 0).getBlock().getType().isSolid() ||
				p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
				p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
				p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
				p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) && (

				p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isCollidable() ||
					p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isCollidable() ||
					p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isCollidable() ||
					p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isCollidable() ||
					p.getEyeLocation().add(0, 1, 0).getBlock().getType().isCollidable() ||
					p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isCollidable() ||
					p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isCollidable() ||
					p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isCollidable() ||
					p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isCollidable()
			)) {
				Block block = p.getTargetBlock(null, 2);
				for (Layer layer : CraftApoli.getLayersFromRegistry()) {
					for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
						ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
						if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
							setActive(p, power.getTag(), true);
							if (!p.isSneaking() && block.getType() != AIR) return;
							p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
							getActiveClimbingMap().add(p);
							new BukkitRunnable() {
								@Override
								public void run() {
									getActiveClimbingMap().remove(p);
								}
							}.runTaskLater(GenesisMC.getPlugin(), 1L);
						} else {
							setActive(p, power.getTag(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:climbing";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return climbing;
	}
}
