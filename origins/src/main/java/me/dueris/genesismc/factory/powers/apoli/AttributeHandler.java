package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AttributeExecuteEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.ScreenConstants;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class AttributeHandler extends CraftPower implements Listener {

	public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
		return Utils.getOperationMappingsDouble();
	}

	public static Map<String, BinaryOperator<Long>> getOperationMappingsLong() {
		return Utils.getOperationMappingsLong();
	}

	public static Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
		return Utils.getOperationMappingsInteger();
	}

	public static Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
		return Utils.getOperationMappingsFloat();
	}

	public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, Double value) {
		BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
		if (mathOperator != null) {
			double result = (Double) mathOperator.apply(base_value, value);
			p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
		} else {
			Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
		}
	}


	@EventHandler
	public void respawn(PlayerPostRespawnEvent e) {
		Player p = e.getPlayer();
		ScreenConstants.setAttributesToDefault(p);
		if (attribute.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (power == null) continue;

					for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
						if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
							extra_reach.add(p);
							continue;
						} else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
							extra_reach_attack.add(p);
							continue;
						} else {
							Reach.setFinalReach(p, Reach.getDefaultReach(p));
						}

						try {
							Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

							Object valueObj = modifier.get("value");

							if (valueObj instanceof Number) {
								double value;
								if (valueObj instanceof Integer) {
									value = ((Number) valueObj).intValue();
								} else if (valueObj instanceof Double) {
									value = ((Number) valueObj).doubleValue();
								} else if (valueObj instanceof Float) {
									value = ((Number) valueObj).floatValue();
								} else if (valueObj instanceof Long) {
									value = ((Number) valueObj).longValue();
								} else {
									Objects.requireNonNull(valueObj);
									continue;
								}

								double base_value = p.getAttribute(attribute_modifier).getBaseValue();
								String operation = String.valueOf(modifier.get("operation"));
								executeAttributeModify(operation, attribute_modifier, base_value, p, value);
								AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power.toString(), power);
								Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
								setActive(p, power.getTag(), true);
								p.sendHealthUpdate();
							}
						} catch (Exception ev) {
							ev.printStackTrace();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void ExecuteAttributeModification(OriginChangeEvent e) {
		Player p = e.getPlayer();
		ScreenConstants.setAttributesToDefault(p);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (attribute.contains(p)) {
					for (Layer layer : CraftApoli.getLayersFromRegistry()) {
						for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
							if (power == null) continue;

							for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
								if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
									extra_reach.add(p);
									return;
								} else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
									extra_reach_attack.add(p);
									return;
								} else {
									Reach.setFinalReach(p, Reach.getDefaultReach(p));
								}

								try {
									Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

									System.out.println(attribute_modifier.getKey().asString());
									Object valueObj = modifier.get("value");

									if (valueObj instanceof Number) {
										double value;
										if (valueObj instanceof Integer) {
											value = ((Number) valueObj).intValue();
										} else if (valueObj instanceof Double) {
											value = ((Number) valueObj).doubleValue();
										} else if (valueObj instanceof Float) {
											value = ((Number) valueObj).floatValue();
										} else if (valueObj instanceof Long) {
											value = ((Number) valueObj).longValue();
										} else {
											Objects.requireNonNull(valueObj);
											continue;
										}

										double base_value = p.getAttribute(attribute_modifier).getBaseValue();
										String operation = String.valueOf(modifier.get("operation"));
										executeAttributeModify(operation, attribute_modifier, base_value, p, value);
										AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power.toString(), power);
										Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
										setActive(p, power.getTag(), true);
										p.sendHealthUpdate();
									}
								} catch (Exception ev) {
									ev.printStackTrace();
								}
							}
						}
					}
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 20L);
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:attribute";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return attribute;
	}

	public static class Reach implements Listener {

		private static Block getClosestBlockInSight(Player player, double maxRange, double normalReach) {
			// Get the player's eye location
			Location eyeLocation = player.getEyeLocation();

			// Get the direction the player is looking at
			Vector direction = eyeLocation.getDirection();

			// Iterate through the blocks in the line of sight
			for (double distance = 0.0; distance <= maxRange; distance += 0.1) {
				Location targetLocation = eyeLocation.clone().add(direction.clone().multiply(distance));
				Block targetBlock = targetLocation.getBlock();

				// Check if the block can be broken and it's outside of the normal reach
				if (targetBlock.getType() != Material.AIR && targetBlock.getType().isSolid()
					&& distance > normalReach) {
					return targetBlock;
				}
			}

			return null; // No block in sight within the range
		}

		public static int getDefaultReach(Entity entity) {
			if (entity instanceof Player p) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return 5;
				}
			}
			return 3;
		}

		public static void setFinalReach(Entity p, double value) {
			p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE, value);
		}

		public static double getFinalReach(Entity p) {
			if (p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE)) {
				return p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE);
			} else {
				return getDefaultReach(p);
			}
		}

		@EventHandler
		public void OnClickREACH(PlayerInteractEvent e) {
			Player p = e.getPlayer();
			if (extra_reach_attack.contains(e.getPlayer())) {
				for (Layer layer : CraftApoli.getLayersFromRegistry()) {
					for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, "apoli:attribute", layer)) {
						for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
							if (!e.getAction().isLeftClick()) return;
							String operation = String.valueOf(modifier.get("operation"));

							BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);

							Object valueObj = modifier.get("value");

							double base = getDefaultReach(p);

							if (valueObj instanceof Number) {
								double value;
								if (valueObj instanceof Integer) {
									value = ((Number) valueObj).intValue();
								} else if (valueObj instanceof Double) {
									value = ((Number) valueObj).doubleValue();
								} else if (valueObj instanceof Float) {
									value = ((Number) valueObj).floatValue();
								} else if (valueObj instanceof Long) {
									value = ((Number) valueObj).longValue();
								} else {
									Objects.requireNonNull(valueObj);
									continue;
								}

								if (mathOperator != null) {
									double result = (double) mathOperator.apply(base, value);
									setFinalReach(p, result);
								} else {
									Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
								}

								Location eyeloc = p.getEyeLocation();
								Predicate<Entity> filter = (entity) -> !entity.equals(p);

								RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), getFinalReach(p), FluidCollisionMode.NEVER, false, 0, filter);

								if (traceResult4_5F != null) {
									Entity entity = traceResult4_5F.getHitEntity();
									//entity code -- pvp
									if (entity == null) return;
									Player attacker = p;
									if (entity.isDead() || !(entity instanceof LivingEntity)) return;
									if (entity.isInvulnerable()) return;
									LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
									if (attacker.getLocation().distance(victim.getLocation()) <= getFinalReach(p)) {
										if (entity.getPassengers().contains(p)) return;
										if (!entity.isDead()) {
											LivingEntity ent = (LivingEntity) entity;
											p.attack(ent);
										}
									} else {
										e.setCancelled(true);
									}
								}

							}
						}
					}

				}
			}
		}
	}
}
