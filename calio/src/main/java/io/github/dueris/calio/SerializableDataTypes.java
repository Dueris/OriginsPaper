package io.github.dueris.calio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.data.exceptions.DataException;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.calio.util.FilterableWeightedList;
import io.github.dueris.calio.util.StatusEffectChance;
import io.github.dueris.calio.util.Util;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
import net.minecraft.nbt.*;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
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
import net.minecraft.world.item.crafting.RecipeSerializer;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.util.GsonHelper.getAsDouble;

/**
 * The provided calio data types that can be defined by
 * your {@link SerializableData}s.
 * Each SerializableDataType is an instanceof a {@link SerializableDataBuilder}, where T is the
 * actual instance of the type. When defined by the InstanceDefiner, this points to the type of class that is in your constructor. For example:
 * A {@code STRING} data type will define a String param of your constructor. Each SerializableDataBuilder has a method for converting the Gson {@link JsonElement}instance to a Java Object.
 * <br><br>
 * SerializableDataBuilders can also be used outside of instance parsing by calling the {@code deserialize} method with a provided JsonElement. The data builder
 * will then decompile your JsonElement into the java object equivalent
 */
@SuppressWarnings({"unused", "unchecked"})
public class SerializableDataTypes {
	public static final SerializableDataBuilder<Integer> INT = SerializableDataBuilder.of(Codec.INT, int.class);
	public static final SerializableDataBuilder<List<Integer>> INTS = SerializableDataBuilder.of(INT.listOf());
	public static final SerializableDataBuilder<Integer> POSITIVE_INT = boundNumber(INT, 1, Integer.MAX_VALUE);
	public static final SerializableDataBuilder<List<Integer>> POSITIVE_INTS = SerializableDataBuilder.of(POSITIVE_INT.listOf());
	public static final SerializableDataBuilder<Integer> NON_NEGATIVE_INT = boundNumber(INT, 0, Integer.MAX_VALUE);
	public static final SerializableDataBuilder<List<Integer>> NON_NEGATIVE_INTS = SerializableDataBuilder.of(NON_NEGATIVE_INT.listOf());
	public static final SerializableDataBuilder<Boolean> BOOLEAN = SerializableDataBuilder.of(Codec.BOOL, boolean.class);
	public static final SerializableDataBuilder<Float> FLOAT = SerializableDataBuilder.of(Codec.FLOAT, float.class);
	public static final SerializableDataBuilder<List<Float>> FLOATS = SerializableDataBuilder.of(FLOAT.listOf());
	public static final SerializableDataBuilder<Float> POSITIVE_FLOAT = boundNumber(FLOAT, 1F, Float.MAX_VALUE);
	public static final SerializableDataBuilder<List<Float>> POSITIVE_FLOATS = SerializableDataBuilder.of(POSITIVE_FLOAT.listOf());
	public static final SerializableDataBuilder<Float> NON_NEGATIVE_FLOAT = boundNumber(FLOAT, 0F, Float.MAX_VALUE);
	public static final SerializableDataBuilder<List<Float>> NON_NEGATIVE_FLOATS = SerializableDataBuilder.of(NON_NEGATIVE_FLOAT.listOf());
	public static final SerializableDataBuilder<Double> DOUBLE = SerializableDataBuilder.of(Codec.DOUBLE, double.class);
	public static final SerializableDataBuilder<List<Double>> DOUBLES = SerializableDataBuilder.of(DOUBLE.listOf());
	public static final SerializableDataBuilder<Double> POSITIVE_DOUBLE = boundNumber(DOUBLE, 1D, Double.MAX_VALUE);
	public static final SerializableDataBuilder<List<Double>> POSITIVE_DOUBLES = SerializableDataBuilder.of(POSITIVE_DOUBLE.listOf());
	public static final SerializableDataBuilder<Double> NON_NEGATIVE_DOUBLE = boundNumber(DOUBLE, 0D, Double.MAX_VALUE);
	public static final SerializableDataBuilder<List<Double>> NON_NEGATIVE_DOUBLES = SerializableDataBuilder.of(NON_NEGATIVE_DOUBLE.listOf());
	public static final SerializableDataBuilder<String> STRING = SerializableDataBuilder.of(Codec.STRING, String.class);
	public static final SerializableDataBuilder<List<String>> STRINGS = SerializableDataBuilder.of(STRING.listOf());
	public static final SerializableDataBuilder<Number> NUMBER = SerializableDataBuilder.of(new PrimitiveCodec<Number>() {

		@Override
		public <T> DataResult<Number> read(DynamicOps<T> ops, T input) {
			return ops.getNumberValue(input);
		}

		@Override
		public <T> T write(DynamicOps<T> ops, Number value) {
			return ops.createNumeric(value);
		}

	}, Number.class);
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
		Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString),
		ResourceLocation.class
	);
	public static final SerializableDataBuilder<List<ResourceLocation>> IDENTIFIERS = SerializableDataBuilder.of(IDENTIFIER.listOf());
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
				double value = DOUBLE.deserialize(jo.get("amount"));
				return new AttributeModifier(identifier, value, operation);
			} else {
				throw new JsonSyntaxException("Expected json object when creating AttributeModifier instance");
			}
		}, AttributeModifier.class
	);
	public static final SerializableDataBuilder<DataComponentPatch> COMPONENT_CHANGES = SerializableDataBuilder.of(DataComponentPatch.CODEC, DataComponentPatch.class);
	public static final SerializableDataBuilder<Item> ITEM = registry(Item.class, BuiltInRegistries.ITEM);
	public static final SerializableDataBuilder<Holder<Item>> ITEM_ENTRY = registryEntry(BuiltInRegistries.ITEM);
	public static final SerializableDataBuilder<ItemStack> UNCOUNTED_ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo)) {
				return new ItemStack(ITEM_ENTRY.deserialize(jsonElement));
			}
			return new ItemStack(
				ITEM_ENTRY.deserialize(jo.get("id")), 1,
				jo.has("components") ? COMPONENT_CHANGES.deserialize(jo.get("components")) : DataComponentPatch.EMPTY
			);
		}, ItemStack.class
	);
	public static final SerializableDataBuilder<ItemStack> ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			ItemStack stack = UNCOUNTED_ITEM_STACK.deserialize(jsonElement);
			stack.setCount(
				(jsonElement instanceof JsonObject jo) ?
					(jo.has("count") ? boundNumber(INT, 1, 99).deserialize(jo.get("count")) : 1) : 1
			);
			return stack;
		}, ItemStack.class
	);
	public static final SerializableDataBuilder<MobEffect> STATUS_EFFECT = registry(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataBuilder<Holder<MobEffect>> STATUS_EFFECT_ENTRY = registryEntry(BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataBuilder<MobEffectInstance> STATUS_EFFECT_INSTANCE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo))
				throw new JsonSyntaxException("StatusEffectInstance should be a JsonObject!");
			SerializableData.Instance data = SerializableDataBuilder.compound(
				SerializableData.serializableData()
					.add("id", STATUS_EFFECT_ENTRY)
					.add("duration", INT, 100)
					.add("amplifier", INT, 0)
					.add("ambient", BOOLEAN, false)
					.add("show_particles", BOOLEAN, true)
					.add("show_icon", BOOLEAN, true), jo, MobEffectInstance.class
			);
			return new MobEffectInstance(
				data.get("id"),
				data.getInt("duration"),
				data.getInt("amplifier"),
				data.getBoolean("ambient"),
				data.getBoolean("show_particles"),
				data.getBoolean("show_icon"));
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
			BiConsumer<JsonObject, List<Ingredient.Value>> initValues = (object, entries) -> {
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
			};
			List<Ingredient.Value> entries = new ArrayList<>();
			if (jsonElement.isJsonObject()) {
				initValues.accept(jsonElement.getAsJsonObject(), entries);
			} else if (jsonElement.isJsonArray()) {
				JsonArray array = jsonElement.getAsJsonArray();
				array.asList().stream().map(JsonElement::getAsJsonObject).forEach(object -> initValues.accept(object, entries));
			}

			Ingredient ingredient = new Ingredient(entries.stream());
			return ingredient.isEmpty() ? Ingredient.EMPTY : ingredient;
		}, Ingredient.class
	);
	public static final SerializableDataBuilder<Ingredient> VANILLA_INGREDIENT = SerializableDataBuilder.of(Ingredient.CODEC_NONEMPTY, Ingredient.class);
	public static final SerializableDataBuilder<Block> BLOCK = registry(Block.class, BuiltInRegistries.BLOCK);
	public static final SerializableDataBuilder<BlockState> BLOCK_STATE = SerializableDataBuilder.of(STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), str, false).blockState());
			} catch (Exception e) {
				return DataResult.error(e::getMessage);
			}

		}, BlockStateParser::serialize), BlockState.class
	);
	public static final SerializableDataBuilder<ResourceKey<DamageType>> DAMAGE_TYPE = registryKey(Registries.DAMAGE_TYPE);
	public static final SerializableDataBuilder<TagKey<EntityType<?>>> ENTITY_GROUP_TAG = mapped(Util.castClass(TagKey.class), HashBiMap.create(ImmutableMap.of(
		"undead", EntityTypeTags.UNDEAD,
		"arthropod", EntityTypeTags.ARTHROPOD,
		"illager", EntityTypeTags.ILLAGER,
		"aquatic", EntityTypeTags.AQUATIC
	)));
	public static final SerializableDataBuilder<EquipmentSlot> EQUIPMENT_SLOT = enumValue(EquipmentSlot.class);
	public static final SerializableDataBuilder<EnumSet<EquipmentSlot>> EQUIPMENT_SLOT_SET = enumSet(EquipmentSlot.class, EQUIPMENT_SLOT);
	public static final SerializableDataBuilder<EquipmentSlotGroup> ATTRIBUTE_MODIFIER_SLOT = enumValue(EquipmentSlotGroup.class);
	public static final SerializableDataBuilder<EnumSet<EquipmentSlotGroup>> ATTRIBUTE_MODIFIER_SLOT_SET = enumSet(EquipmentSlotGroup.class, ATTRIBUTE_MODIFIER_SLOT);
	public static final SerializableDataBuilder<SoundEvent> SOUND_EVENT = SerializableDataBuilder.of(IDENTIFIER.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation), SoundEvent.class);
	public static final SerializableDataBuilder<EntityType<?>> ENTITY_TYPE = registry(Util.castClass(EntityType.class), BuiltInRegistries.ENTITY_TYPE);
	public static final SerializableDataBuilder<ParticleType<?>> PARTICLE_TYPE = registry(Util.castClass(ParticleType.class), BuiltInRegistries.PARTICLE_TYPE);
	public static final SerializableDataBuilder<Tag> NBT_ELEMENT = SerializableDataBuilder.of(
		Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(), nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement.copy())),
		Tag.class
	);

	public static final SerializableDataBuilder<CompoundTag> NBT_COMPOUND = SerializableDataBuilder.of(Codec.withAlternative(CompoundTag.CODEC, TagParser.LENIENT_CODEC), CompoundTag.class);
	public static final SerializableDataBuilder<ParticleOptions> PARTICLE_EFFECT = SerializableDataBuilder.of(
		(jsonElement) -> {
			ParticleType<? extends ParticleOptions> particleType;
			CompoundTag paramsNbt = null;
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				if (jo.has("params")) {
					paramsNbt = NBT_COMPOUND.deserialize(jo.get("params"));
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
	public static final SerializableDataBuilder<FoodProperties.PossibleEffect> FOOD_STATUS_EFFECT_ENTRY = SerializableDataBuilder.of(FoodProperties.PossibleEffect.CODEC, FoodProperties.PossibleEffect.class);
	public static final SerializableDataBuilder<List<FoodProperties.PossibleEffect>> FOOD_STATUS_EFFECT_ENTRIES = SerializableDataBuilder.of(FOOD_STATUS_EFFECT_ENTRY.listOf());
	public static final SerializableDataBuilder<FoodProperties> FOOD_COMPONENT = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo))
				throw new JsonSyntaxException("Food Properties should be a JsonObject!");
			SerializableData.Instance data = SerializableDataBuilder.compound(
				SerializableData.serializableData()
					.add("nutrition", NON_NEGATIVE_INT)
					.add("saturation", FLOAT)
					.add("can_always_eat", BOOLEAN, false)
					.add("eat_seconds", POSITIVE_FLOAT, 1.6F)
					.addSupplied("using_converts_to", optional(UNCOUNTED_ITEM_STACK), Optional::empty)
					.add("effect", FOOD_STATUS_EFFECT_ENTRY, null)
					.add("effects", FOOD_STATUS_EFFECT_ENTRIES, null),
				jo, FoodProperties.class
			);

			List<FoodProperties.PossibleEffect> effects = new ArrayList<>();

			data.<FoodProperties.PossibleEffect>ifPresent("effect", effects::add);
			data.<List<FoodProperties.PossibleEffect>>ifPresent("effects", effects::addAll);

			return new FoodProperties(
				data.getInt("nutrition"),
				data.getFloat("saturation"),
				data.getBoolean("can_always_eat"),
				data.getFloat("eat_seconds"),
				data.get("using_converts_to"),
				effects
			);
		}, FoodProperties.class
	);
	public static final SerializableDataBuilder<Component> TEXT = SerializableDataBuilder.of(ComponentSerialization.CODEC, Component.class);
	public static final SerializableDataBuilder<net.kyori.adventure.text.Component> KYORI_COMPONENT = SerializableDataBuilder.of(
		(jsonElement) -> GsonComponentSerializer.gson().deserializeFromTree(jsonElement), net.kyori.adventure.text.Component.class
	);
	public static final SerializableDataBuilder<RecipeHolder<? extends Recipe<?>>> RECIPE = SerializableDataBuilder.of(
		Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.IDENTIFIER.fieldOf("id").forGetter(RecipeHolder::id),
			BuiltInRegistries.RECIPE_SERIALIZER
				.byNameCodec()
				.dispatchMap(Recipe::getSerializer, RecipeSerializer::codec).forGetter(RecipeHolder::value)
		).apply(instance, RecipeHolder::new))), RecipeHolder.class
	);
	public static final SerializableDataBuilder<GameEvent> GAME_EVENT = registry(GameEvent.class, BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataBuilder<Holder<GameEvent>> GAME_EVENT_ENTRY = registryEntry(BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataBuilder<TagKey<GameEvent>> GAME_EVENT_TAG = tag(Registries.GAME_EVENT);
	public static final SerializableDataBuilder<Fluid> FLUID = registry(Fluid.class, BuiltInRegistries.FLUID);
	public static final SerializableDataBuilder<FogType> CAMERA_SUBMERSION_TYPE = enumValue(FogType.class);
	public static final SerializableDataBuilder<InteractionHand> HAND = enumValue(InteractionHand.class);
	public static final SerializableDataBuilder<InteractionResult> ACTION_RESULT = enumValue(InteractionResult.class);
	public static final SerializableDataBuilder<UseAnim> USE_ACTION = enumValue(UseAnim.class);
	public static final SerializableDataBuilder<Direction> DIRECTION = enumValue(Direction.class);
	public static final SerializableDataBuilder<EnumSet<Direction>> DIRECTION_SET = enumSet(Direction.class, DIRECTION);
	public static final SerializableDataBuilder<Class<?>> CLASS = SerializableDataBuilder.of(
		STRING.comapFlatMap(
			str -> {

				try {
					return DataResult.success(Class.forName(str));
				} catch (ClassNotFoundException ignored) {
					return DataResult.error(() -> "Specified class does not exist: \"" + str + "\"");
				}

			},
			Class::getName
		), Class.class
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
	public static final SerializableDataBuilder<PotionContents> POTION_CONTENTS_COMPONENT = SerializableDataBuilder.of(PotionContents.CODEC, PotionContents.class);
	public static final SerializableDataBuilder<ResourceKey<LootItemFunction>> ITEM_MODIFIER = registryKey(Registries.ITEM_MODIFIER);
	public static final SerializableDataBuilder<ResourceKey<LootItemCondition>> PREDICATE = registryKey(Registries.PREDICATE);
	private static final Gson GSON = new Gson();

	public static <T> @NotNull SerializableDataBuilder<Set<T>> set(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				Set<T> set = new HashSet<>();
				if (jsonElement.isJsonArray()) {
					int i = 0;
					for (JsonElement je : jsonElement.getAsJsonArray()) {
						try {
							set.add(singular.deserialize(je));
						} catch (DataException e) {
							throw e.prepend("[" + i + "]");
						} catch (Exception e) {
							throw new DataException(DataException.Phase.READING, "[" + i + "]", e);
						}
						i++;
					}
				} else {
					set.add(singular.deserialize(jsonElement));
				}
				return set;
			}, Set.class
		);
	}

	public static <T extends Enum<T>> @NotNull SerializableDataBuilder<EnumSet<T>> enumSet(Class<T> enumClass, SerializableDataBuilder<T> enumDataType) {
		return SerializableDataBuilder.of(
			(json) -> {
				EnumSet<T> set = EnumSet.noneOf(enumClass);
				if (json.isJsonPrimitive()) {
					T t = enumDataType.deserialize(json);
					set.add(t);
				} else if (json.isJsonArray()) {
					JsonArray array = json.getAsJsonArray();
					for (JsonElement jsonElement : array) {
						T t = enumDataType.deserialize(jsonElement);
						set.add(t);
					}
				} else {
					throw new RuntimeException("Expected enum set to be either an array or a primitive.");
				}
				return set;
			}, Util.castClass(EnumSet.class));
	}

	public static <T> @NotNull SerializableDataBuilder<List<T>> list(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				LinkedList<T> list = new LinkedList<>();
				if (jsonElement.isJsonArray()) {
					int i = 0;
					for (JsonElement je : jsonElement.getAsJsonArray()) {
						try {
							list.add(singular.deserialize(je));
						} catch (DataException e) {
							throw e.prepend("[" + i + "]");
						} catch (Exception e) {
							throw new DataException(DataException.Phase.READING, "[" + i + "]", e);
						}
						i++;
					}
				} else {
					list.add(singular.deserialize(jsonElement));
				}
				return list;
			}, List.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<ConcurrentLinkedQueue<T>> concurrentQueue(SerializableDataBuilder<T> singular) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				ConcurrentLinkedQueue<T> linkedQueue = new ConcurrentLinkedQueue<>();
				if (jsonElement.isJsonArray()) {
					int i = 0;
					for (JsonElement je : jsonElement.getAsJsonArray()) {
						try {
							linkedQueue.add(singular.deserialize(je));
						} catch (DataException e) {
							throw e.prepend("[" + i + "]");
						} catch (Exception e) {
							throw new DataException(DataException.Phase.READING, "[" + i + "]", e);
						}
						i++;
					}
				} else {
					linkedQueue.add(singular.deserialize(jsonElement));
				}
				return linkedQueue;
			}, ConcurrentLinkedQueue.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<ResourceKey<T>> registryKey(ResourceKey<Registry<T>> registryRef) {
		return SerializableDataBuilder.of(
			(jsonElement) -> ResourceKey.create(registryRef, IDENTIFIER.deserialize(jsonElement)),
			ResourceKey.class
		);
	}

	@Contract(value = "_, _, _ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<T> calioRegistry(Class<T> dataClass, RegistryKey<T> registry, String defaultNamespace) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				CalioRegistry reg = CalioRegistry.INSTANCE;
				String locationID = jsonElement.getAsString();
				if (!locationID.contains(":")) {
					locationID = defaultNamespace + ":" + jsonElement.getAsString();
				}
				ResourceLocation id = ResourceLocation.parse(locationID);
				return reg.retrieve(registry).get(id);
			}, dataClass
		);
	}

	public static <T> @NotNull SerializableDataBuilder<T> registry(Class<T> dataClass, Registry<T> registry) {
		return registry(dataClass, registry, ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static <T> @NotNull SerializableDataBuilder<T> registry(Class<T> dataClass, Registry<T> registry, String defaultNamespace) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				String locationID = jsonElement.getAsString();
				if (!locationID.contains(":")) {
					locationID = defaultNamespace + ":" + jsonElement.getAsString();
				}
				ResourceLocation id = ResourceLocation.parse(locationID);
				return registry
					.getOptional(id)
					.orElseThrow();
			}, dataClass
		);
	}

	public static <T> @NotNull SerializableDataBuilder<Holder<T>> registryEntry(Registry<T> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				return (registry)
					.getHolder(IDENTIFIER.deserialize(jsonElement))
					.orElseThrow();
			}, Holder.class
		);
	}

	public static <T extends Enum<T>> @NotNull SerializableDataBuilder<T> enumValue(Class<T> dataClass) {
		return enumValue(dataClass, null);
	}

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

	public static <T> @NotNull SerializableDataBuilder<Optional<T>> optional(SerializableDataBuilder<T> builder) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				return Optional.of(builder.deserialize(jsonElement));
			}, Optional.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<TagKey<T>> tag(ResourceKey<? extends Registry<T>> registryRef) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				return TagKey.create(registryRef, IDENTIFIER.deserialize(jsonElement));
			}, TagKey.class
		);
	}

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

	public static <T> @NotNull SerializableDataBuilder<FilterableWeightedList<T>> weightedList(SerializableDataBuilder<T> singleDataType) {
		return SerializableDataBuilder.of((jsonElement) -> {
			FilterableWeightedList<T> list = new FilterableWeightedList<>();
			if (jsonElement.isJsonArray()) {
				int i = 0;
				for (JsonElement je : jsonElement.getAsJsonArray()) {
					try {
						JsonObject weightedObj = je.getAsJsonObject();
						T elem = singleDataType.deserialize(weightedObj.get("element"));
						int weight = GsonHelper.getAsInt(weightedObj, "weight");
						list.add(elem, weight);
					} catch (DataException e) {
						throw e.prepend("[" + i + "]");
					} catch (Exception e) {
						throw new DataException(DataException.Phase.READING, "[" + i + "]", e);
					}
					i++;
				}
			}
			return list;
		}, Util.castClass(FilterableWeightedList.class));
	}

	public static <N extends Number & Comparable<N>> SerializableDataBuilder<N> boundNumber(SerializableDataBuilder<N> numberDataType, N min, N max) {
		return boundNumber(numberDataType,
			min, (value, _min) -> "Expected value to be at least " + _min + "! (current value: " + value + ")",
			max, (value, _max) -> "Expected value to be at most " + _max + "! (current value: " + value + ")"
		);
	}

	public static <N extends Number & Comparable<N>> SerializableDataBuilder<N> boundNumber(@NotNull SerializableDataBuilder<N> numberDataType, N min, BiFunction<N, N, String> underMinError, N max, BiFunction<N, N, String> overMaxError) {
		return (SerializableDataBuilder<N>) numberDataType.comapFlatMap(
			number -> {

				if (number.compareTo(min) < 0) {
					return DataResult.error(() -> underMinError.apply(number, min));
				} else if (number.compareTo(max) > 0) {
					return DataResult.error(() -> overMaxError.apply(number, max));
				} else {
					return DataResult.success(number);
				}

			},
			Function.identity(), numberDataType.type()
		);
	}

}
