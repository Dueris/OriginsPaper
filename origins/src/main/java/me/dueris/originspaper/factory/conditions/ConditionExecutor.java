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
						return evaluateBiEntityConditions(condition.getJsonArray("conditions"), entityPair, true);
					}
					case "apoli:or" -> {
						return evaluateBiEntityConditions(condition.getJsonArray("conditions"), entityPair, false);
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

	private static boolean evaluateBiEntityConditions(FactoryJsonArray conditions, Pair<CraftEntity, CraftEntity> entityPair, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<BiEntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
				BiEntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testBiEntity(obj, entityPair.first(), entityPair.second()));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
	}


	public static boolean testBiome(FactoryJsonObject condition, BlockPos blockPos, ServerLevel level) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					return evaluateBiomeConditions(condition.getJsonArray("conditions"), blockPos, level, true);
				}
				case "apoli:or" -> {
					return evaluateBiomeConditions(condition.getJsonArray("conditions"), blockPos, level, false);
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

	private static boolean evaluateBiomeConditions(FactoryJsonArray conditions, BlockPos blockPos, ServerLevel level, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<BiomeConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
				BiomeConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testBiome(obj, blockPos, level));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
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
					return evaluateBlockConditions(condition.getJsonArray("conditions"), block, true);
				}
				case "apoli:or" -> {
					return evaluateBlockConditions(condition.getJsonArray("conditions"), block, false);
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

	private static boolean evaluateBlockConditions(FactoryJsonArray conditions, Block block, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<BlockConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
				BlockConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testBlock(obj, block));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
	}

	public static boolean testDamage(FactoryJsonObject condition, EntityDamageEvent event) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					return evaluateDamageConditions(condition.getJsonArray("conditions"), event, true);
				}
				case "apoli:or" -> {
					return evaluateDamageConditions(condition.getJsonArray("conditions"), event, false);
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

	private static boolean evaluateDamageConditions(FactoryJsonArray conditions, EntityDamageEvent event, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<DamageConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
				DamageConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testDamage(obj, event));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
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
					return evaluateEntityConditions(condition.getJsonArray("conditions"), entity, true);
				}
				case "apoli:or" -> {
					return evaluateEntityConditions(condition.getJsonArray("conditions"), entity, false);
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

	private static boolean evaluateEntityConditions(FactoryJsonArray conditions, Entity entity, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<EntityConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
				EntityConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testEntity(obj, entity));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
	}


	public static boolean testItem(FactoryJsonObject condition, ItemStack itemStack) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					return evaluateItemConditions(condition.getJsonArray("conditions"), itemStack, true);
				}
				case "apoli:or" -> {
					return evaluateItemConditions(condition.getJsonArray("conditions"), itemStack, false);
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

	private static boolean evaluateItemConditions(FactoryJsonArray conditions, ItemStack itemStack, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<ItemConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
				ItemConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testItem(obj, itemStack));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
	}


	public static boolean testFluid(FactoryJsonObject condition, Fluid fluid) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (isMetaCondition(condition)) {
			String type = condition.getString("type");
			switch (type) {
				case "apoli:and" -> {
					return evaluateFluidConditions(condition.getJsonArray("conditions"), fluid, true);
				}
				case "apoli:or" -> {
					return evaluateFluidConditions(condition.getJsonArray("conditions"), fluid, false);
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

	private static boolean evaluateFluidConditions(FactoryJsonArray conditions, Fluid fluid, boolean isAnd) {
		for (var object : conditions.asList) {
			if (object.isJsonObject()) {
				FactoryJsonObject obj = object.toJsonObject();
				Registrar<FluidConditions.ConditionFactory> factoryx = OriginsPaper.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
				FluidConditions.ConditionFactory conx = factoryx.get(ResourceLocation.parse(obj.getString("type")));
				boolean conditionResult;
				boolean inverted = obj.getBooleanOrDefault("inverted", false);

				if (conx != null) {
					conditionResult = getPossibleInvert(inverted, testFluid(obj, fluid));
				} else {
					conditionResult = getPossibleInvert(inverted, true);
				}

				if (isAnd && !conditionResult) {
					return false;
				} else if (!isAnd && conditionResult) {
					return true;
				}
			}
		}
		return isAnd;
	}


	protected static boolean getPossibleInvert(boolean inverted, boolean original) {
		return inverted != original;
	}
}
