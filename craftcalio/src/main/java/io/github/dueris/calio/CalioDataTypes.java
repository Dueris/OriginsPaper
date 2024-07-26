package io.github.dueris.calio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.calio.util.StatusEffectChance;
import io.github.dueris.calio.util.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import static net.minecraft.util.GsonHelper.getAsDouble;

@SuppressWarnings({"unused", "unchecked"})
public class CalioDataTypes {
	public static final SerializableDataBuilder<String> STRING = SerializableDataBuilder.of(
		JsonElement::getAsString, String.class
	);
	public static final SerializableDataBuilder<Boolean> BOOLEAN = SerializableDataBuilder.of(
		JsonElement::getAsBoolean, boolean.class
	);
	public static final SerializableDataBuilder<Integer> INT = SerializableDataBuilder.of(
		JsonElement::getAsInt, int.class
	);
	public static final SerializableDataBuilder<Float> FLOAT = SerializableDataBuilder.of(
		JsonElement::getAsFloat, int.class
	);
	public static final SerializableDataBuilder<Double> DOUBLE = SerializableDataBuilder.of(
		JsonElement::getAsDouble, double.class
	);
	public static final SerializableDataBuilder<Integer> POSITIVE_INT = SerializableDataBuilder.of(
		(jsonElement) -> {
			int raw = INT.deserialize(jsonElement);
			if (raw < 0) {
				throw new IllegalArgumentException("Value must be greater than 0! Current value: " + raw);
			}
			return raw;
		}, int.class
	);
	public static final SerializableDataBuilder<Float> POSITIVE_FLOAT = SerializableDataBuilder.of(
		(jsonElement) -> {
			float raw = FLOAT.deserialize(jsonElement);
			if (raw < 0) {
				throw new IllegalArgumentException("Value must be greater than 0! Current value: " + raw);
			}
			return raw;
		}, float.class
	);
	public static final SerializableDataBuilder<Double> POSITIVE_DOUBLE = SerializableDataBuilder.of(
		(jsonElement) -> {
			double raw = DOUBLE.deserialize(jsonElement);
			if (raw < 0) {
				throw new IllegalArgumentException("Value must be greater than 0! Current value: " + raw);
			}
			return raw;
		}, double.class
	);
	public static final SerializableDataBuilder<JsonObject> JSON_OBJECT = SerializableDataBuilder.of(
		JsonElement::getAsJsonObject, JsonObject.class
	);
	public static final SerializableDataBuilder<Vec3> VECTOR = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				return new Vec3(
					getAsDouble(jo, "x", 0),
					getAsDouble(jo, "y", 0),
					getAsDouble(jo, "z", 0)
				);
			} else {
				throw new JsonParseException("Expected an object with x, y, and z fields.");
			}
		}, Vec3.class
	);
	public static final SerializableDataBuilder<ResourceLocation> IDENTIFIER = SerializableDataBuilder.of(
		(jsonElement) -> {
			return ResourceLocation.read(jsonElement.getAsString()).getOrThrow();
		}, ResourceLocation.class
	);
	public static final SerializableDataBuilder<ResourceKey<Enchantment>> ENCHANTMENT = registryKey(Registries.ENCHANTMENT);
	public static final SerializableDataBuilder<ResourceKey<Level>> DIMENSION = registryKey(Registries.DIMENSION);
	public static final SerializableDataBuilder<Attribute> ATTRIBUTE = registry(Attribute.class, BuiltInRegistries.ATTRIBUTE);
	public static final SerializableDataBuilder<Holder<Attribute>> ATTRIBUTE_ENTRY = registryEntry(BuiltInRegistries.ATTRIBUTE);
	public static final SerializableDataBuilder<AttributeModifier.Operation> MODIFIER_OPERATION = enumValue(AttributeModifier.Operation.class);
	public static final SerializableDataBuilder<AttributeModifier> ATTRIBUTE_MODIFIER = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				AttributeModifier.Operation operation = MODIFIER_OPERATION.deserialize(jo.get("operation"));
				ResourceLocation identifier = IDENTIFIER.deserialize(jo.get("id"));
				double value = DOUBLE.deserialize(jo.get("value"));
				return new AttributeModifier(identifier, value, operation);
			} else {
				throw new JsonSyntaxException("Expected json object when creating AttributeModifier instance");
			}
		}, AttributeModifier.class
	);
	public static final SerializableDataBuilder<Item> ITEM = registry(Item.class, BuiltInRegistries.ITEM);
	public static final SerializableDataBuilder<MobEffect> STATUS_EFFECT = registry(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataBuilder<Holder<MobEffect>> STATUS_EFFECT_ENTRY = registryEntry(BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataBuilder<MobEffectInstance> STATUS_EFFECT_INSTANCE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				Holder<MobEffect> effectHolder = STATUS_EFFECT_ENTRY.deserialize(jo.get("effect"));
				int duration = jo.has("duration") ? INT.deserialize(jo.get("duration")) : 100;
				int amplifier = jo.has("amplifier") ? INT.deserialize(jo.get("amplifier")) : 0;
				boolean isAmbient = jo.has("is_ambient") ? BOOLEAN.deserialize(jo.get("is_ambient")) : false;
				boolean showParticles = jo.has("show_particles") ? BOOLEAN.deserialize(jo.get("show_particles")) : true;
				boolean showIcon = jo.has("show_icon") ? BOOLEAN.deserialize(jo.get("show_icon")) : true;
				return new MobEffectInstance(effectHolder, duration, amplifier, isAmbient, showParticles, showIcon);
			} else {
				throw new JsonSyntaxException("StatusEffectInstance must be a json object!");
			}
		}, MobEffectInstance.class
	);
	public static final SerializableDataBuilder<StatusEffectChance> STATUS_EFFECT_CHANCE = SerializableDataBuilder.of(
		(jsonElement) -> {
			JsonObject jo = jsonElement.getAsJsonObject();
			StatusEffectChance sec = new StatusEffectChance();
			sec.statusEffectInstance = STATUS_EFFECT_INSTANCE.deserialize(jo.get("effect"));
			sec.chance = jo.has("chance") ? FLOAT.deserialize(jo.get("chance")) : 1.0F;
			return sec;
		}, StatusEffectChance.class
	);
	public static final SerializableDataBuilder<TagKey<Item>> ITEM_TAG = tag(Registries.ITEM);
	public static final SerializableDataBuilder<TagKey<Fluid>> FLUID_TAG = tag(Registries.FLUID);
	public static final SerializableDataBuilder<TagKey<Block>> BLOCK_TAG = tag(Registries.BLOCK);
	public static final SerializableDataBuilder<TagKey<EntityType<?>>> ENTITY_TAG = tag(Registries.ENTITY_TYPE);
	public static final SerializableDataBuilder<Ingredient> INGREDIENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			List<Ingredient.Value> entries = new ArrayList<>();
			if (jsonElement.isJsonObject()) {
				initValues(jsonElement.getAsJsonObject(), entries);
			} else if (jsonElement.isJsonArray()) {
				JsonArray array = jsonElement.getAsJsonArray();
				array.asList().stream().map(JsonElement::getAsJsonObject).forEach(object -> initValues(object, entries));
			}

			return fromValues(entries.stream());
		}, Ingredient.class
	);
	public static final SerializableDataBuilder<Ingredient> VANILLA_INGREDIENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return Ingredient.CODEC_NONEMPTY
				.parse(JsonOps.INSTANCE, jsonElement)
				.mapError(err -> "Couldn't deserialize ingredient from JSON: " + err)
				.getOrThrow();
		}, Ingredient.class
	);
	public static final SerializableDataBuilder<Block> BLOCK = registry(Block.class, BuiltInRegistries.BLOCK);
	public static final SerializableDataBuilder<BlockState> BLOCK_STATE = SerializableDataBuilder.of(
		(jsonElement) -> {
			try {
				return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), STRING.deserialize(jsonElement), false).blockState();
			} catch (CommandSyntaxException e) {
				throw new JsonParseException(e);
			}
		}, BlockState.class
	);
	public static final SerializableDataBuilder<ResourceKey<DamageType>> DAMAGE_TYPE = registryKey(Registries.DAMAGE_TYPE);
	public static final SerializableDataBuilder<TagKey<EntityType<?>>> ENTITY_GROUP_TAG = mapped(Util.castClass(TagKey.class), HashBiMap.create(ImmutableMap.of(
		"undead", EntityTypeTags.UNDEAD,
		"arthropod", EntityTypeTags.ARTHROPOD,
		"illager", EntityTypeTags.ILLAGER,
		"aquatic", EntityTypeTags.AQUATIC
	)));
	public static final SerializableDataBuilder<EquipmentSlot> EQUIPMENT_SLOT = enumValue(EquipmentSlot.class);
	public static final SerializableDataBuilder<SoundEvent> SOUND_EVENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return SoundEvent.createVariableRangeEvent(IDENTIFIER.deserialize(jsonElement));
		}, SoundEvent.class
	);
	public static final SerializableDataBuilder<EntityType<?>> ENTITY_TYPE = registry(Util.castClass(EntityType.class), BuiltInRegistries.ENTITY_TYPE);
	public static final SerializableDataBuilder<ParticleType<?>> PARTICLE_TYPE = registry(Util.castClass(ParticleType.class), BuiltInRegistries.PARTICLE_TYPE);
	public static final SerializableDataBuilder<CompoundTag> COMPOUND_NBT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return Codec.withAlternative(CompoundTag.CODEC, TagParser.LENIENT_CODEC)
				.parse(JsonOps.INSTANCE, jsonElement)
				.getOrThrow();
		}, CompoundTag.class
	);
	public static final SerializableDataBuilder<ParticleOptions> PARTICLE_EFFECT = SerializableDataBuilder.of(
		(jsonElement) -> {
			ParticleType<? extends ParticleOptions> particleType;
			CompoundTag paramsNbt = null;
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				if (jo.has("params")) {
					paramsNbt = COMPOUND_NBT.deserialize(jo.get("params"));
				}
				particleType = PARTICLE_TYPE.deserialize(jo.get("type"));
			} else {
				particleType = PARTICLE_TYPE.deserialize(jsonElement);
			}

			ResourceLocation particleTypeId = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType));
			if (particleType instanceof SimpleParticleType simpleType) {
				return simpleType;
			} else if (paramsNbt == null || paramsNbt.isEmpty()) {
				throw new JsonSyntaxException("Expected parameters for particle effect \"" + particleTypeId + "\"");
			} else {
				paramsNbt.putString("type", particleTypeId.toString());
				return ParticleTypes.CODEC
					.parse(NbtOps.INSTANCE, paramsNbt)
					.getOrThrow();
			}
		}, ParticleOptions.class
	);
	public static final SerializableDataBuilder<ParticleOptions> PARTICLE_EFFECT_OR_TYPE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString()) {

				ParticleType<?> particleType = PARTICLE_TYPE.deserialize(jsonPrimitive);

				if (particleType instanceof SimpleParticleType simpleParticleType) {
					return simpleParticleType;
				}

			} else if (jsonElement instanceof JsonObject jsonObject) {
				return PARTICLE_EFFECT.deserialize(jsonObject);
			}

			throw new IllegalArgumentException("Expected either a string with parameter-less particle effect, or a JSON object");
		}, ParticleOptions.class
	);
	public static final StreamCodec<ByteBuf, CompoundTag> UNLIMITED_NBT_COMPOUND_PACKET_CODEC = ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap);
	public static final SerializableDataBuilder<DataComponentPatch> COMPONENT_CHANGES = SerializableDataBuilder.of(
		(jsonElement) -> {
			return DataComponentPatch.CODEC
				.parse(JsonOps.INSTANCE, jsonElement)
				.getOrThrow(JsonParseException::new);
		}, DataComponentPatch.class
	);
	public static final SerializableDataBuilder<ItemStack> ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			JsonObject jo = jsonElement.getAsJsonObject();
			Item item = ITEM.deserialize(jo.get("item"));
			ItemStack stack = item.getDefaultInstance();

			stack.setCount(jo.has("amount") ? INT.deserialize(jo.get("amount")) : 1);
			stack.applyComponentsAndValidate(jo.has("components") ? COMPONENT_CHANGES.deserialize(jo.get("components")) : DataComponentPatch.EMPTY);

			return stack;
		}, ItemStack.class
	);
	public static final SerializableDataBuilder<Component> TEXT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return ComponentSerialization.CODEC
				.parse(JsonOps.INSTANCE, jsonElement)
				.getOrThrow(JsonParseException::new);
		}, Component.class
	);
	public static final SerializableDataBuilder<RecipeHolder<? extends Recipe<?>>> RECIPE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jsonObject)) {
				throw new JsonSyntaxException("Expected recipe to be a JSON object.");
			}

			ResourceLocation id = IDENTIFIER.deserialize(GsonHelper.getNonNull(jsonObject, "id"));
			Recipe<?> recipe = Recipe.CODEC
				.parse(JsonOps.INSTANCE, jsonObject)
				.getOrThrow(JsonParseException::new);

			return new RecipeHolder<>(id, recipe);
		}, RecipeHolder.class
	);
	public static final SerializableDataBuilder<GameEvent> GAME_EVENT = registry(GameEvent.class, BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataBuilder<Holder<GameEvent>> GAME_EVENT_ENTRY = registryEntry(BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataBuilder<TagKey<GameEvent>> GAME_EVENT_TAG = tag(Registries.GAME_EVENT);
	public static final SerializableDataBuilder<Fluid> FLUID = registry(Fluid.class, BuiltInRegistries.FLUID);
	public static final SerializableDataBuilder<FogType> CAMERA_SUBMERSION_TYPE = enumValue(FogType.class);
	public static final SerializableDataBuilder<InteractionHand> HAND = enumValue(InteractionHand.class);
	public static final SerializableDataBuilder<InteractionResult> ACTION_RESULT = enumValue(InteractionResult.class);
	public static final SerializableDataBuilder<UseAnim> USE_ACTION = enumValue(UseAnim.class);
	public static final SerializableDataBuilder<FoodProperties.PossibleEffect> FOOD_STATUS_EFFECT_ENTRY = SerializableDataBuilder.of(
		(jsonElement) -> {
			return FoodProperties.PossibleEffect.CODEC
				.parse(JsonOps.INSTANCE, jsonElement)
				.getOrThrow(JsonParseException::new);
		}, FoodProperties.PossibleEffect.class
	);
	public static final SerializableDataBuilder<FoodProperties> FOOD_COMPONENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			JsonObject jo = jsonElement.getAsJsonObject();
			int hunger = INT.deserialize(jo.get("hunger"));
			float saturation = FLOAT.deserialize(jo.get("saturation"));
			boolean alwaysEdible = jo.has("always_edible") ? BOOLEAN.deserialize(jo.get("always_edible")) : false;
			boolean snack = jo.has("snack") ? BOOLEAN.deserialize(jo.get("snack")) : false;
			FoodProperties.PossibleEffect effect = jo.has("effect") ? FOOD_STATUS_EFFECT_ENTRY.deserialize(jo.get("effect")) : null;
			Set<FoodProperties.PossibleEffect> effects = jo.has("effects") ? set(FOOD_STATUS_EFFECT_ENTRY).deserialize(jo.get("effects")) : null;
			ItemStack usingConvertsTo = jo.has("using_converts_to") ? ITEM_STACK.deserialize(jo.get("using_converts_to")) : null;

			FoodProperties.Builder builder = new FoodProperties.Builder()
				.nutrition(hunger)
				.saturationModifier(saturation);

			if (alwaysEdible) {
				builder.alwaysEdible();
			}

			if (snack) {
				builder.fast();
			}

			if (effect != null) {
				builder.effect(effect.effect(), effect.probability());
			}

			if (effects != null) {
				for (FoodProperties.PossibleEffect possibleEffect : effects) {
					builder.effect(possibleEffect.effect(), possibleEffect.probability());
				}
			}

			if (usingConvertsTo != null) {
				try {
					ReflectionUtils.setFieldValue(builder, ReflectionUtils.getField(FoodProperties.Builder.class, "usingConvertsTo").orElseThrow(), Optional.of(usingConvertsTo));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			return builder.build();
		}, FoodProperties.class
	);
	public static final SerializableDataBuilder<Direction> DIRECTION = enumValue(Direction.class);
	public static final SerializableDataBuilder<Set<Direction>> DIRECTION_SET = set(DIRECTION);
	public static final SerializableDataBuilder<Class<?>> CLASS = SerializableDataBuilder.of(
		(jsonElement) -> {
			try {
				return Class.forName(STRING.deserialize(jsonElement));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}, Class.class
	);
	public static final SerializableDataBuilder<ClipContext.Block> SHAPE_TYPE = enumValue(ClipContext.Block.class);
	public static final SerializableDataBuilder<ClipContext.Fluid> FLUID_HANDLING = enumValue(ClipContext.Fluid.class);
	public static final SerializableDataBuilder<Explosion.BlockInteraction> DESTRUCTION_TYPE = enumValue(Explosion.BlockInteraction.class);
	public static final SerializableDataBuilder<Direction.Axis> AXIS = enumValue(Direction.Axis.class);
	public static final SerializableDataBuilder<ArgumentWrapper<NbtPathArgument.NbtPath>> NBT_PATH = argumentType(NbtPathArgument.nbtPath());
	public static final SerializableDataBuilder<Stat<?>> STAT = SerializableDataBuilder.of(
		(jsonElement) -> {
			JsonObject jo = jsonElement.getAsJsonObject();
			StatType statType = registry(Util.castClass(StatType.class), BuiltInRegistries.STAT_TYPE).deserialize(jo.get("type"));
			ResourceLocation statId = IDENTIFIER.deserialize(jo.get("id"));

			Registry statRegistry = statType.getRegistry();
			if (statRegistry.containsKey(statId)) {
				return statType.get(statRegistry.get(statId));
			} else {
				throw new IllegalArgumentException("Desired stat \"" + statId + "\" does not exist in stat type \"" + BuiltInRegistries.STAT_TYPE.getKey(statType) + "\"");
			}
		}, Stat.class
	);
	public static final SerializableDataBuilder<TagKey<Biome>> BIOME_TAG = tag(Registries.BIOME);
	public static final SerializableDataBuilder<PotionContents> POTION_CONTENTS_COMPONENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return PotionContents.CODEC
				.parse(JsonOps.INSTANCE, jsonElement)
				.getOrThrow(JsonParseException::new);
		}, PotionContents.class
	);
	public static final SerializableDataBuilder<ResourceKey<LootItemFunction>> ITEM_MODIFIER = registryKey(Registries.ITEM_MODIFIER);
	public static final SerializableDataBuilder<ResourceKey<LootItemCondition>> PREDICATE = registryKey(Registries.PREDICATE);
	private static final Gson GSON = new Gson();

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<Set<T>> set(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				Set<T> built = new HashSet<>();
				if (jsonElement.isJsonArray()) {
					jsonElement.getAsJsonArray().forEach(instance -> {
						built.add(singular.deserialize(instance));
					});
				} else {
					built.add(singular.deserialize(jsonElement));
				}
				return built;
			}, Set.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<List<T>> list(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				List<T> built = new ArrayList<>();
				if (jsonElement.isJsonArray()) {
					jsonElement.getAsJsonArray().forEach(instance -> {
						built.add(singular.deserialize(instance));
					});
				} else {
					built.add(singular.deserialize(jsonElement));
				}
				return built;
			}, List.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<ConcurrentLinkedQueue<T>> concurrentQueue(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				ConcurrentLinkedQueue<T> built = new ConcurrentLinkedQueue<>();
				if (jsonElement.isJsonArray()) {
					jsonElement.getAsJsonArray().forEach(instance -> {
						built.add(singular.deserialize(instance));
					});
				} else {
					built.add(singular.deserialize(jsonElement));
				}
				return built;
			}, ConcurrentLinkedQueue.class
		);
	}

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<ResourceKey<T>> registryKey(ResourceKey<Registry<T>> registryRef) {
		return SerializableDataBuilder.of(
			(jsonElement) -> ResourceKey.create(registryRef, IDENTIFIER.deserialize(jsonElement)),
			ResourceKey.class
		);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<T> registry(Class<T> dataClass, Registry<T> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				ResourceLocation id = IDENTIFIER.deserialize(jsonElement);
				return registry
					.getOptional(id)
					.orElseThrow();
			}, dataClass
		);
	}

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<Holder<T>> registryEntry(Registry<T> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				return (registry)
					.getHolder(IDENTIFIER.deserialize(jsonElement))
					.orElseThrow();
			}, Holder.class
		);
	}

	@Contract(value = "_ -> new", pure = true)
	public static <T extends Enum<T>> @NotNull SerializableDataBuilder<T> enumValue(Class<T> dataClass) {
		return enumValue(dataClass, null);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static <T extends Enum<T>> @NotNull SerializableDataBuilder<T> enumValue(Class<T> dataClass, HashMap<String, T> additionalMap) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (jsonElement.isJsonPrimitive()) {
					JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
					if (primitive.isNumber()) {
						int enumOrdinal = primitive.getAsInt();
						T[] enumValues = dataClass.getEnumConstants();
						if (enumOrdinal < 0 || enumOrdinal >= enumValues.length) {
							throw new JsonSyntaxException("Expected to be in the range of 0 - " + (enumValues.length - 1));
						}
						return enumValues[enumOrdinal];
					} else if (primitive.isString()) {
						String enumName = primitive.getAsString();
						try {
							return Enum.valueOf(dataClass, enumName);
						} catch (IllegalArgumentException e0) {
							try {
								return Enum.valueOf(dataClass, enumName.toUpperCase(Locale.ROOT));
							} catch (IllegalArgumentException e1) {
								try {
									if (additionalMap == null || !additionalMap.containsKey(enumName)) {
										throw new IllegalArgumentException();
									}
									return additionalMap.get(enumName);
								} catch (IllegalArgumentException e2) {
									T[] enumValues = dataClass.getEnumConstants();
									String stringOf = enumValues[0].name() + ", " + enumValues[0].name().toLowerCase(Locale.ROOT);
									for (int i = 1; i < enumValues.length; i++) {
										stringOf += ", " + enumValues[i].name() + ", " + enumValues[i].name().toLowerCase(Locale.ROOT);
									}
									throw new JsonSyntaxException("Expected value to be a string of: " + stringOf);
								}
							}
						}
					}
				}
				throw new JsonSyntaxException("Expected value to be either an integer or a string.");
			}, dataClass
		);
	}

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<TagKey<T>> tag(ResourceKey<? extends Registry<T>> registryRef) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				return TagKey.create(registryRef, IDENTIFIER.deserialize(jsonElement));
			}, TagKey.class
		);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<T> mapped(Class<T> dataClass, BiMap<String, T> map) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (jsonElement.isJsonPrimitive()) {
					JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
					if (primitive.isString()) {
						String name = primitive.getAsString();
						try {
							if (map == null || !map.containsKey(name)) {
								throw new IllegalArgumentException();
							}
							return map.get(name);
						} catch (IllegalArgumentException e2) {
							throw new JsonSyntaxException("Expected value to be a string of: " + map.keySet().stream().reduce((s0, s1) -> s0 + ", " + s1));
						}
					}
				}
				throw new JsonSyntaxException("Expected value to be a string.");
			}, dataClass
		);
	}

	@Contract(value = "_ -> new", pure = true)
	public static <T, U extends ArgumentType<T>> @NotNull SerializableDataBuilder<ArgumentWrapper<T>> argumentType(U argumentType) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				try {
					String str = STRING.deserialize(jsonElement);
					T t = argumentType.parse(new StringReader(str));
					return new ArgumentWrapper<>(t, str);
				} catch (CommandSyntaxException e) {
					throw new RuntimeException(e.getMessage());
				}
			}, ArgumentWrapper.class
		);
	}

	private static void initValues(@NotNull JsonObject object, List<Ingredient.Value> entries) {
		if (object.has("item")) {
			entries.add(
				new Ingredient.ItemValue(
					MinecraftServer.getServer().registryAccess().registry(Registries.ITEM).get()
						.get(ResourceLocation.parse(object.get("item").getAsString()))
						.getDefaultInstance()
				)
			);
		}

		if (object.has("tag")) {
			try {
				Class<?> tagValueClass = Class.forName("net.minecraft.world.item.crafting.Ingredient$TagValue");
				Constructor<?> constructor = tagValueClass.getDeclaredConstructor(TagKey.class);
				constructor.setAccessible(true);
				Object tagValueInst = constructor.newInstance(TagKey.create(Registries.ITEM, ResourceLocation.parse(object.get("tag").getAsString())));
				entries.add((Ingredient.Value) tagValueInst);
			} catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
					 IllegalAccessException | ClassNotFoundException var5) {
				throw new RuntimeException(var5);
			}
		}
	}

	private static Ingredient fromValues(Stream<? extends Ingredient.Value> entries) {
		Ingredient ingredient = new Ingredient(entries);
		return ingredient.isEmpty() ? Ingredient.EMPTY : ingredient;
	}

}
