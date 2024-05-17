package me.dueris.genesismc.factory.conditions;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConditionExecutor {
	public static BiEntityConditions biEntityCondition = new BiEntityConditions();
	public static BiomeConditions biomeCondition = new BiomeConditions();
	public static BlockConditions blockCondition = new BlockConditions();
	public static DamageConditions damageCondition = new DamageConditions();
	public static EntityConditions entityCondition = new EntityConditions();
	public static FluidConditions fluidCondition = new FluidConditions();
	public static ItemConditions itemCondition = new ItemConditions();

	public static void registerAll() {
		biEntityCondition.prep();
		biomeCondition.prep();
		blockCondition.prep();
		damageCondition.prep();
		entityCondition.prep();
		fluidCondition.prep();
		itemCondition.prep();

		ConditionTypes.ConditionFactory.addMetaConditions();
	}

	private static boolean isMetaCondition(FactoryJsonObject condition) {
		String c = condition.getString("type");
		return (c.equals("apoli:and") || c.equals("apoli:chance") || c.equals("apoli:constant")
			|| c.equals("apoli:not") || c.equals("apoli:or"));
	}

	private static boolean chance(FactoryJsonObject condition) {
		float chance = condition.getNumber("chance").getFloat();
		if (chance > 1f) {
			chance = 1f;
		}
		return new Random().nextFloat(1.0f) <= chance;
	}

	public static boolean testBiEntity(FactoryJsonObject condition, Entity actor, Entity target) {
		return testBiEntity(condition, (CraftEntity) actor, (CraftEntity) target);
	}

	public static boolean testBiEntity(FactoryJsonObject condition, CraftEntity actor, CraftEntity target) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		Pair entityPair = new Pair<CraftEntity, CraftEntity>() {
			@Override
			public CraftEntity left() {
				return actor;
			}

			@Override
			public CraftEntity right() {
				return target;
			}
		};
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
							BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiEntity(obj, (CraftEntity) entityPair.first(), (CraftEntity) entityPair.second())));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
							BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiEntity(obj, (CraftEntity) entityPair.first(), (CraftEntity) entityPair.second())));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
			BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, entityPair));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testBiome(FactoryJsonObject condition, org.bukkit.block.Biome biome, Location blockPos) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
							BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiome(obj, biome, blockPos)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
							BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBiome(obj, biome, blockPos)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
			BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, new oshi.util.tuples.Pair<Biome, BlockPos>(CraftBiome.bukkitToMinecraft(biome), CraftLocation.toBlockPosition(blockPos))));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testBlock(FactoryJsonObject condition, Block block) {
		return testBlock(condition, CraftBlock.at(((CraftWorld) block.getWorld()).getHandle(), CraftLocation.toBlockPosition(block.getLocation())));
	}

	@SuppressWarnings("index out of bounds")
	public static boolean testBlock(FactoryJsonObject condition, CraftBlock block) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
							BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBlock(obj, block)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
							BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testBlock(obj, block)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
			BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, block));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testDamage(FactoryJsonObject condition, EntityDamageEvent event) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
							DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testDamage(obj, event)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
							DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testDamage(obj, event)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
			DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, event));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testEntity(FactoryJsonObject condition, Entity entity) {
		return testEntity(condition, (CraftEntity) entity);
	}

	public static boolean testEntity(FactoryJsonObject condition, CraftEntity entity) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
							EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testEntity(obj, entity)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
							EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testEntity(obj, entity)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
			EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, entity));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testItem(FactoryJsonObject condition, ItemStack itemStack) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
							ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testItem(obj, itemStack)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
							ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testItem(obj, itemStack)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
			ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, itemStack));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	public static boolean testFluid(FactoryJsonObject condition, net.minecraft.world.level.material.Fluid fluid) {
		if (condition == null || condition.isEmpty()) return true; // Empty condition, do nothing
		if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
							FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(testFluid(obj, fluid));
							} else {
								cons.add(true); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (!b) return false;
					}
					return true;
				}
				case "apoli:or" -> {
					FactoryJsonArray array = condition.getJsonArray("conditions");
					List<Boolean> cons = new ArrayList<>();
					array.forEach(object -> {
						if (object.isJsonObject()) {
							FactoryJsonObject obj = object.toJsonObject();
							Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
							FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.getString("type")));
							if (con != null) {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), testFluid(obj, fluid)));
							} else {
								cons.add(getPossibleInvert(condition.getBooleanOrDefault("inverted", false), true)); // Condition null or not found.
							}
						}
					});
					for (boolean b : cons) {
						if (b) return true;
					}
					return false;
				}
				case "apoli:constant" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), condition.getBoolean("value"));
				}
				case "apoli:chance" -> {
					return getPossibleInvert(condition.getBooleanOrDefault("inverted", false), chance(condition));
				}
			}
		} else {
			// return the condition
			Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
			FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.getString("type")));
			boolean invert = condition.getBooleanOrDefault("inverted", false);
			if (con != null) {
				return getPossibleInvert(invert, con.test(condition, fluid));
			} else {
				return getPossibleInvert(invert, true); // Condition null or not found.
			}
		}
		return false;
	}

	protected static boolean getPossibleInvert(boolean inverted, boolean original) {
		return inverted != original;
	}
}
