package me.dueris.genesismc.content;

import me.dueris.genesismc.registry.nms.OriginLootCondition;
import me.dueris.genesismc.registry.nms.PowerLootCondition;
import me.dueris.genesismc.util.JsonObjectBuilder;
import me.dueris.genesismc.util.WrappedBootstrapContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class NMSBootstrap {

	public static void bootstrap(WrappedBootstrapContext context) {
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("apoli", "power"), PowerLootCondition.TYPE);
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "origin"), OriginLootCondition.TYPE);

		context.addDataDrivenPointer(Registries.ENCHANTMENT);

		context.registerData(
			Registries.ENCHANTMENT,
			new JsonObjectBuilder()
				.number("anvil_cost", 2)
				.jsonObject("description", new JsonObjectBuilder()
					.string("type", "text")
					.string("text", "Water Protection")
					.build())
				.jsonObject("effects", new JsonObjectBuilder()
					.jsonArray("minecraft:damage_protection", new JsonObjectBuilder.JsonArrayBuilder()
						.jsonObject(new JsonObjectBuilder()
							.jsonObject("effect", new JsonObjectBuilder()
								.string("type", "minecraft:add")
								.jsonObject("value", new JsonObjectBuilder()
									.string("type", "minecraft:linear")
									.number("base", 2.0)
									.number("per_level_above_first", 2.0)
									.build())
								.build())
							.jsonObject("requirements", new JsonObjectBuilder()
								.string("condition", "minecraft:all_of")
								.jsonArray("terms", new JsonObjectBuilder.JsonArrayBuilder()
									.jsonObject(new JsonObjectBuilder()
										.string("condition", "minecraft:damage_source_properties")
										.jsonObject("predicate", new JsonObjectBuilder()
											.jsonArray("tags", new JsonObjectBuilder.JsonArrayBuilder()
												.jsonObject(new JsonObjectBuilder()
													.bool("expected", true)
													.string("id", "origins:water_protection_effects")
													.build())
												.build())
											.build())
										.build())
									.build())
								.build())
							.build())
						.build())
					.build())
				.string("exclusive_set", "#minecraft:exclusive_set/armor")
				.jsonObject("max_cost", new JsonObjectBuilder()
					.number("base", 18)
					.number("per_level_above_first", 8)
					.build())
				.number("max_level", 4)
				.jsonObject("min_cost", new JsonObjectBuilder()
					.number("base", 10)
					.number("per_level_above_first", 8)
					.build())
				.stringArray("slots", "armor")
				.string("supported_items", "#minecraft:enchantable/armor")
				.number("weight", 5)
				.build(),
			ResourceLocation.fromNamespaceAndPath("origins", "water_protection")
		);

	}
}
