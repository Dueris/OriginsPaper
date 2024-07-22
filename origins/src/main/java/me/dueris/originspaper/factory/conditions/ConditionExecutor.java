package me.dueris.originspaper.factory.conditions;

import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.holders.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.types.*;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConditionExecutor {
	public static BiEntityConditions biEntityConditions = new BiEntityConditions();
	public static BiomeConditions biomeConditions = new BiomeConditions();
	public static BlockConditions blockConditions = new BlockConditions();
	public static DamageConditions damageConditions = new DamageConditions();
	public static EntityConditions entityConditions = new EntityConditions();
	public static FluidConditions fluidConditions = new FluidConditions();
	public static ItemConditions itemConditions = new ItemConditions();

	public static void registerAll() {
		biEntityConditions.registerConditions();
		biomeConditions.registerConditions();
		blockConditions.registerConditions();
		damageConditions.registerConditions();
		entityConditions.registerConditions();
		fluidConditions.registerConditions();
		itemConditions.registerConditions();
		ConditionTypes.ConditionFactory.addMetaConditions();
	}

	private static boolean isMetaCondition(@NotNull FactoryJsonObject condition) {
		String c = condition.getString("type");
		return c.equals("apoli:and") || c.equals("apoli:chance") || c.equals("apoli:constant") || c.equals("apoli:not") || c.equals("apoli:or");
	}

	private static boolean chance(@NotNull FactoryJsonObject condition) {
		float chance = condition.getNumber("chance").getFloat();
		if (chance > 1.0F) {
			chance = 1.0F;
		}

		return new Random().nextFloat(1.0F) <= chance;
	}

	public static boolean testBiEntity(FactoryJsonObject condition, Entity actor, Entity target) {
		return testBiEntity(condition, (CraftEntity) actor, (CraftEntity) target);
	}

	public static boolean testBiEntity(FactoryJsonObject condition, CraftEntity actor, CraftEntity target) {
		if (condition != null && !condition.isEmpty()) {
			Pair<CraftEntity, CraftEntity> entityPair = new Pair<>(actor, target);
			if (isMetaCondition(condition)) {
				String type = condition.getString("type");
				switch (type) {
					case "apoli:and" -> {
						FactoryJsonArray array = condition.getJsonArray("conditions");
						List<Boolean> cons = new ArrayList<>();
						array.forEach(
							object -> {
								if (object.isJsonObject()) {
									FactoryJsonObject obj = object.toJsonObject();
									Registrar<BiEntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
									BiEntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
									if (conx != null) {
										cons.add(
											getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiEntity(obj, entityPair.first(), entityPair.second()))
										);
									} else {
										cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
									}
								}
							}
						);

						for (boolean bx : cons) {
							if (!bx) {
								return false;
							}
						}

						return true;
					}
					case "apoli:or" -> {
						FactoryJsonArray array = condition.getJsonArray("conditions");
						List<Boolean> cons = new ArrayList<>();
						array.forEach(
							object -> {
								if (object.isJsonObject()) {
									FactoryJsonObject obj = object.toJsonObject();
									Registrar<BiEntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
									BiEntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
									if (conx != null) {
										cons.add(
											getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiEntity(obj, entityPair.first(), entityPair.second()))
										);
									} else {
										cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
									}
								}
							}
						);

						for (boolean b : cons) {
							if (b) {
								return true;
							}
						}

						return false;
					}
					case "apoli:constant" -> {
						return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
					}
					case "apoli:chance" -> {
						return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
					}
					default -> {
						return false;
					}
				}
			} else {
				Registrar<BiEntityConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
				BiEntityConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
				boolean invert = condition.getBooleanOrDefault("inverted", false);
				return con != null ? getPossibleInvert(invert, con.test(condition, entityPair)) : getPossibleInvert(invert, false);
			}
		} else {
			return true;
		}
	}

	public static boolean testBiome(FactoryJsonObject condition, BlockPos blockPos, ServerLevel level) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiomeConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
							BiomeConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiome(obj, blockPos, level)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiomeConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
							BiomeConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiome(obj, blockPos, level)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<BiomeConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
			BiomeConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, level.getBiomeManager().getBiome(blockPos))) : getPossibleInvert(invert, false);
		}
	}

	public static boolean testBlock(FactoryJsonObject condition, Block block) {
		return testBlock(condition, CraftBlock.at(((CraftWorld) block.getWorld()).getHandle(), CraftLocation.toBlockPosition(block.getLocation())));
	}

	public static boolean testBlock(FactoryJsonObject condition, CraftBlock block) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BlockConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
							BlockConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBlock(obj, block)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BlockConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
							BlockConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBlock(obj, block)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<BlockConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
			BlockConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, block)) : getPossibleInvert(invert, false);
		}
	}

	public static boolean testDamage(FactoryJsonObject condition, EntityDamageEvent event) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<DamageConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
							DamageConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testDamage(obj, event)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<DamageConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
							DamageConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testDamage(obj, event)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<DamageConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
			DamageConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, event)) : getPossibleInvert(invert, false);
		}
	}

	public static boolean testEntity(FactoryJsonObject condition, Entity entity) {
		return testEntity(condition, (CraftEntity) entity);
	}

	public static boolean testEntity(FactoryJsonObject condition, CraftEntity entity) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<EntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
							EntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testEntity(obj, entity)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<EntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
							EntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testEntity(obj, entity)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<EntityConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
			EntityConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, entity)) : getPossibleInvert(invert, false);
		}
	}

	public static boolean testItem(FactoryJsonObject condition, ItemStack itemStack) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<ItemConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
							ItemConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testItem(obj, itemStack)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<ItemConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
							ItemConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testItem(obj, itemStack)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<ItemConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
			ItemConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, itemStack)) : getPossibleInvert(invert, false);
		}
	}

	public static boolean testFluid(FactoryJsonObject condition, Fluid fluid) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<FluidConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
							FluidConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(testFluid(obj, fluid));
							} else {
								cons.add(true);
							}
						}
					});

					for (boolean bx : cons) {
						if (!bx) {
							return false;
						}
					}

					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<FluidConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
							FluidConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
							if (conx != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testFluid(obj, fluid)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true));
							}
						}
					});

					for (boolean b : cons) {
						if (b) {
							return true;
						}
					}

					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
				default -> {
					return false;
				}
			}
		} else {
			Registrar<FluidConditions.ConditionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
			FluidConditions.ConditionFactory con = factory.get(ResourceLocation.parse(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			return con != null ? getPossibleInvert(invert, con.test(condition, fluid)) : getPossibleInvert(invert, false);
		}
	}

	protected static boolean getPossibleInvert(boolean inverted, boolean original) {
		return inverted != original;
	}
}
