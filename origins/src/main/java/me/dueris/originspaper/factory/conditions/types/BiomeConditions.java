package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome.Precipitation;
import org.bukkit.NamespacedKey;
import oshi.util.tuples.Pair;

import java.util.function.BiPredicate;

public class BiomeConditions {

	public void registerConditions() {

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
