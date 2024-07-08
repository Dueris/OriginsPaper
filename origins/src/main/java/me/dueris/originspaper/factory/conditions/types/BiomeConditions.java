package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome.Precipitation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import oshi.util.tuples.Pair;

import java.util.function.BiPredicate;

public class BiomeConditions {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (condition, biome) -> {
			NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag"));
			TagKey<net.minecraft.world.level.biome.Biome> key = TagKey.create(net.minecraft.core.registries.Registries.BIOME, CraftNamespacedKey.toMinecraft(tag));
			return MinecraftServer.getServer().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).wrapAsHolder(biome.getA()).is(key);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("precipitation"), (condition, biome) -> biome.getA().hasPrecipitation() ?
        /*
            biome.getA().getTemperature(biome.getB()) <= 0.15f ?
                condition.get("precipitation").toString().equals("snow")
                : condition.get("precipitation").toString().equals("rain")
            : condition.get("precipitation").toString().equals("none");
                */
			biome.getA().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(getPrecipitation(condition)) :
			condition.getString("precipitation").equalsIgnoreCase("none")));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("category"), (condition, biome) -> {
			NamespacedKey tag = OriginsPaper.apoliIdentifier("category/" + condition.getString("category")); // Use category folder
			TagKey key = TagKey.create(net.minecraft.core.registries.Registries.BIOME, CraftNamespacedKey.toMinecraft(tag));
			return ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).wrapAsHolder(biome.getA()).is(key);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("temperature"), (condition, biome) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			return Comparison.fromString(comparison).compare(biome.getA().getBaseTemperature(), compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("high_humidity"), (condition, biome) -> Comparison.fromString(">=").compare(biome.getA().climateSettings.downfall(), 0.85f)));
	}

	private net.minecraft.world.level.biome.Biome.Precipitation getPrecipitation(FactoryJsonObject condition) {
		String lowerCase = condition.getString("precipitation").toLowerCase();
		switch (lowerCase) {
			case "none" -> {
				return Precipitation.NONE;
			}
			case "snow" -> {
				return Precipitation.SNOW;
			}
			case "rain" -> {
				return Precipitation.RAIN;
			}
			default -> {
				return null;
			}
		}
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, Pair<net.minecraft.world.level.biome.Biome, BlockPos>> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, Pair<net.minecraft.world.level.biome.Biome, BlockPos>> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, Pair<net.minecraft.world.level.biome.Biome, BlockPos> tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
