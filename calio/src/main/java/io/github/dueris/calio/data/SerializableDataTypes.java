package io.github.dueris.calio.data;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.dueris.calio.mixin.ItemStackAccessor;
import io.github.dueris.calio.mixin.TagEntryAccessor;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.calio.util.DynamicIdentifier;
import io.github.dueris.calio.util.StatusEffectChance;
import io.github.dueris.calio.util.TagLike;
import io.github.dueris.calio.registry.DataObjectFactory;
import io.github.dueris.calio.registry.SimpleDataObjectFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The provided calio data types that can be defined by
 * your {@link SerializableData}s.
 * Each SerializableDataType is an instanceof a {@link SerializableDataType}, where T is the
 * actual instance of the type. When defined by the InstanceDefiner, this points to the type of class that is in your constructor. For example:
 * A {@code STRING} data type will define a String param of your constructor. Each SerializableDataBuilder has a method for converting the Gson {@link JsonElement}instance to a Java Object.
 * <br><br>
 * SerializableDataBuilders can also be used outside of instance parsing by calling the {@code deserialize} method with a provided JsonElement. The data builder
 * will then decompile your JsonElement into the java object equivalent
 */
@SuppressWarnings({"unused", "unchecked"})
public class SerializableDataTypes {
	public static final SerializableDataType<Integer> INT = SerializableDataType.of(Codec.INT);

	public static final SerializableDataType<List<Integer>> INTS = INT.list();

	public static final SerializableDataType<Integer> POSITIVE_INT = SerializableDataType.boundNumber(INT, 1, Integer.MAX_VALUE);

	public static final SerializableDataType<List<Integer>> POSITIVE_INTS = POSITIVE_INT.list();

	public static final SerializableDataType<Integer> NON_NEGATIVE_INT = SerializableDataType.boundNumber(INT, 0, Integer.MAX_VALUE);

	public static final SerializableDataType<List<Integer>> NON_NEGATIVE_INTS = NON_NEGATIVE_INT.list();

	public static final SerializableDataType<Boolean> BOOLEAN = SerializableDataType.of(Codec.BOOL);

	public static final SerializableDataType<Float> FLOAT = SerializableDataType.of(Codec.FLOAT);

	public static final SerializableDataType<List<Float>> FLOATS = FLOAT.list();

	public static final SerializableDataType<Float> POSITIVE_FLOAT = SerializableDataType.boundNumber(FLOAT, 1F, Float.MAX_VALUE);

	public static final SerializableDataType<List<Float>> POSITIVE_FLOATS = POSITIVE_FLOAT.list();

	public static final SerializableDataType<Float> NON_NEGATIVE_FLOAT = SerializableDataType.boundNumber(FLOAT, 0F, Float.MAX_VALUE);

	public static final SerializableDataType<List<Float>> NON_NEGATIVE_FLOATS = NON_NEGATIVE_FLOAT.list();

	public static final SerializableDataType<Double> DOUBLE = SerializableDataType.of(Codec.DOUBLE);

	public static final SerializableDataType<List<Double>> DOUBLES = DOUBLE.list();

	public static final SerializableDataType<Double> POSITIVE_DOUBLE = SerializableDataType.boundNumber(DOUBLE, 1D, Double.MAX_VALUE);

	public static final SerializableDataType<List<Double>> POSITIVE_DOUBLES = POSITIVE_DOUBLE.list();

	public static final SerializableDataType<Double> NON_NEGATIVE_DOUBLE = SerializableDataType.boundNumber(DOUBLE, 0D, Double.MAX_VALUE);

	public static final SerializableDataType<List<Double>> NON_NEGATIVE_DOUBLES = NON_NEGATIVE_DOUBLE.list();

	public static final SerializableDataType<String> STRING = SerializableDataType.of(Codec.STRING);

	public static final SerializableDataType<List<String>> STRINGS = STRING.list();

	public static final SerializableDataType<Number> NUMBER = SerializableDataType.of(
		new PrimitiveCodec<>() {

			@Override
			public <T> DataResult<Number> read(DynamicOps<T> ops, T input) {
				return ops.getNumberValue(input);
			}

			@Override
			public <T> T write(DynamicOps<T> ops, Number value) {
				return ops.createNumeric(value);
			}

		}
	);

	public static final SerializableDataType<List<Number>> NUMBERS = NUMBER.list();

	public static final CompoundSerializableDataType<Vec3> VECTOR = SerializableDataType.compound(
		new SerializableData()
			.add("x", DOUBLE, 0.0)
			.add("y", DOUBLE, 0.0)
			.add("z", DOUBLE, 0.0),
		data -> new Vec3(
			data.getDouble("x"),
			data.getDouble("y"),
			data.getDouble("z")
		),
		(vec3d, serializableData) -> serializableData.instance()
			.set("x", vec3d.x)
			.set("y", vec3d.y)
			.set("z", vec3d.z)
	);

	public static final SerializableDataType<ResourceLocation> IDENTIFIER = SerializableDataType.of(
		Codec.STRING.comapFlatMap(DynamicIdentifier::ofResult, ResourceLocation::toString)
	);

	public static final SerializableDataType<List<ResourceLocation>> IDENTIFIERS = IDENTIFIER.list();

	public static final SerializableDataType<ResourceKey<Enchantment>> ENCHANTMENT = SerializableDataType.registryKey(Registries.ENCHANTMENT);
	public static final SerializableDataType<Attribute> ATTRIBUTE = SerializableDataType.registry(BuiltInRegistries.ATTRIBUTE);
	public static final SerializableDataType<List<Attribute>> ATTRIBUTES = ATTRIBUTE.list();
	public static final SerializableDataType<Holder<Attribute>> ATTRIBUTE_ENTRY = SerializableDataType.registryEntry(BuiltInRegistries.ATTRIBUTE);
	public static final SerializableDataType<List<Holder<Attribute>>> ATTRIBUTE_ENTRIES = ATTRIBUTE_ENTRY.list();
	public static final SerializableDataType<AttributeModifier.Operation> MODIFIER_OPERATION = SerializableDataType.enumValue(AttributeModifier.Operation.class);
	public static final DataObjectFactory<AttributeModifier> ATTRIBUTE_MODIFIER_OBJ_FACTORY = new SimpleDataObjectFactory<>(
		new SerializableData()
			.add("id", IDENTIFIER)
			.add("amount", DOUBLE)
			.add("operation", MODIFIER_OPERATION),
		data -> new AttributeModifier(
			data.get("id"),
			data.get("amount"),
			data.get("operation")
		),
		(entityAttributeModifier, serializableData) -> serializableData.instance()
			.set("id", entityAttributeModifier.id())
			.set("amount", entityAttributeModifier.amount())
			.set("operation", entityAttributeModifier.operation())
	);
	public static final CompoundSerializableDataType<AttributeModifier> ATTRIBUTE_MODIFIER = SerializableDataType.compound(ATTRIBUTE_MODIFIER_OBJ_FACTORY);
	public static final SerializableDataType<List<AttributeModifier>> ATTRIBUTE_MODIFIERS = ATTRIBUTE_MODIFIER.list();
	public static final SerializableDataType<Item> ITEM = SerializableDataType.registry(BuiltInRegistries.ITEM);
	public static final SerializableDataType<Holder<Item>> ITEM_ENTRY = SerializableDataType.registryEntry(BuiltInRegistries.ITEM);
	public static final SerializableDataType<DataComponentPatch> COMPONENT_CHANGES = SerializableDataType.of(DataComponentPatch.CODEC);
	public static final DataObjectFactory<ItemStack> UNCOUNTED_ITEM_STACK_OBJ_FACTORY = new SimpleDataObjectFactory<>(
		new SerializableData()
			.add("id", ITEM_ENTRY)
			.add("components", COMPONENT_CHANGES, DataComponentPatch.EMPTY),
		data -> new ItemStack(
			data.get("id"), 1,
			data.get("components")
		),
		(stack, serializableData) -> serializableData.instance()
			.set("id", stack.getItemHolder())
			.set("components", stack.getComponentsPatch())
	);
	public static final CompoundSerializableDataType<ItemStack> UNCOUNTED_ITEM_STACK = SerializableDataType.compound(UNCOUNTED_ITEM_STACK_OBJ_FACTORY);
	public static final DataObjectFactory<ItemStack> ITEM_STACK_OBJ_FACTORY = new SimpleDataObjectFactory<>(
		UNCOUNTED_ITEM_STACK_OBJ_FACTORY.getSerializableData().copy()
			.add("count", SerializableDataType.boundNumber(INT, 1, 99), 1),
		data -> {

			ItemStack stack = UNCOUNTED_ITEM_STACK_OBJ_FACTORY.fromData(data);
			stack.setCount(data.getInt("count"));

			return stack;

		},
		(stack, serializableData) -> UNCOUNTED_ITEM_STACK_OBJ_FACTORY
			.toData(stack, serializableData)
			.set("count", ((ItemStackAccessor) (Object) stack).getCountOverride())
	);
	public static final CompoundSerializableDataType<ItemStack> ITEM_STACK = SerializableDataType.compound(ITEM_STACK_OBJ_FACTORY);
	public static final SerializableDataType<List<ItemStack>> ITEM_STACKS = ITEM_STACK.list();
	public static final SerializableDataType<MobEffect> STATUS_EFFECT = SerializableDataType.registry(BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataType<List<MobEffect>> STATUS_EFFECTS = STATUS_EFFECT.list();
	public static final SerializableDataType<Holder<MobEffect>> STATUS_EFFECT_ENTRY = SerializableDataType.registryEntry(BuiltInRegistries.MOB_EFFECT);
	public static final SerializableDataType<List<Holder<MobEffect>>> STATUS_EFFECT_ENTRIES = STATUS_EFFECT_ENTRY.list();
	public static final CompoundSerializableDataType<MobEffectInstance> STATUS_EFFECT_INSTANCE = SerializableDataType.compound(
		new SerializableData()
			.add("id", STATUS_EFFECT_ENTRY)
			.add("duration", INT, 100)
			.add("amplifier", INT, 0)
			.add("ambient", BOOLEAN, false)
			.add("show_particles", BOOLEAN, true)
			.add("show_icon", BOOLEAN, true),
		data -> new MobEffectInstance(
			data.get("id"),
			data.getInt("duration"),
			data.getInt("amplifier"),
			data.getBoolean("ambient"),
			data.getBoolean("show_particles"),
			data.getBoolean("show_icon")
		),
		(effectInstance, serializableData) -> serializableData.instance()
			.set("id", effectInstance.getEffect())
			.set("duration", effectInstance.getDuration())
			.set("amplifier", effectInstance.getAmplifier())
			.set("ambient", effectInstance.isAmbient())
			.set("show_particles", effectInstance.isVisible())
			.set("show_icon", effectInstance.showIcon())
	);
	public static final SerializableDataType<List<MobEffectInstance>> STATUS_EFFECT_INSTANCES = STATUS_EFFECT_INSTANCE.list();
	@Deprecated(forRemoval = true)
	public static final CompoundSerializableDataType<StatusEffectChance> STATUS_EFFECT_CHANCE = SerializableDataType.compound(
		new SerializableData()
			.add("effect", STATUS_EFFECT_INSTANCE)
			.add("chance", FLOAT, 1.0F),
		data -> new StatusEffectChance(
			data.get("effect"),
			data.getFloat("chance")
		),
		(effectChance, serializableData) -> serializableData.instance()
			.set("effect", effectChance.statusEffectInstance())
			.set("chance", effectChance.chance())
	);
	@Deprecated(forRemoval = true)
	public static final SerializableDataType<List<StatusEffectChance>> STATUS_EFFECT_CHANCES = STATUS_EFFECT_CHANCE.list();
	public static final SerializableDataType<TagKey<Item>> ITEM_TAG = SerializableDataType.tagKey(Registries.ITEM);
	private static final SerializableDataType<Ingredient.TagValue> INLINE_INGREDIENT_TAG_ENTRY = SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<Ingredient.TagValue, T>> decode(DynamicOps<T> ops, T input) {
				return ops.getStringValue(input)
					.flatMap(str -> str.startsWith("#")
						? DataResult.success(str.substring(1))
						: DataResult.error(() -> "Item tags must start with '#'!"))
					.flatMap(str -> ITEM_TAG.codec().decode(ops, ops.createString(str))
						.map(tagAndInput -> tagAndInput
							.mapFirst(Ingredient.TagValue::new)));
			}

			@Override
			public <T> DataResult<T> encode(Ingredient.TagValue input, DynamicOps<T> ops, T prefix) {
				return DataResult.success(ops.createString("#" + input.tag().location()));
			}

		}
	);
	@Deprecated(since = "1.14.0-alpha.7")
	public static final SerializableDataType<Ingredient.Value> OBJECT_INGREDIENT_ENTRY = SerializableDataType.compound(
		new SerializableData()
			.add("item", ITEM, null)
			.add("tag", ITEM_TAG, null)
			.validate(data -> {

				boolean hasItem = data.isPresent("item");
				boolean hasTag = data.isPresent("tag");

				if (hasItem == hasTag) {
					return DataResult.error(() -> (hasItem ? "Both" : "Any of") + " \"item\" and \"tag\" fields " + (hasItem ? "shouldn't" : "must") + " be defined!");
				} else {
					return DataResult.success(data);
				}

			}),
		data -> {

			if (data.isPresent("item")) {

				Item item = data.get("item");
				ItemStack stack = new ItemStack(item);

				return new Ingredient.ItemValue(stack);

			} else {
				return new Ingredient.TagValue(data.get("tag"));
			}

		},
		(entry, serializableData) -> {
			SerializableData.Instance data = serializableData.instance();
			return switch (entry) {
				case Ingredient.ItemValue stackEntry -> data.set("item", stackEntry.item().getItem());
				case Ingredient.TagValue tagEntry -> data.set("tag", tagEntry.tag());
				default -> throw new UnsupportedOperationException("Ingredient entry is not an item or a tag!");
			};
		}
	);
	public static final SerializableDataType<TagKey<Fluid>> FLUID_TAG = SerializableDataType.tagKey(Registries.FLUID);
	public static final SerializableDataType<TagKey<Block>> BLOCK_TAG = SerializableDataType.tagKey(Registries.BLOCK);
	public static final SerializableDataType<TagKey<EntityType<?>>> ENTITY_TAG = SerializableDataType.tagKey(Registries.ENTITY_TYPE);
	public static final SerializableDataType<Ingredient> VANILLA_INGREDIENT = SerializableDataType.of(Ingredient.CODEC_NONEMPTY);
	public static final SerializableDataType<Block> BLOCK = SerializableDataType.registry(BuiltInRegistries.BLOCK);
	public static final SerializableDataType<BlockState> BLOCK_STATE = STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), str, false).blockState());
			} catch (Exception e) {
				return DataResult.error(e::getMessage);
			}

		},
		BlockStateParser::serialize
	);
	public static final SerializableDataType<ResourceKey<DamageType>> DAMAGE_TYPE = SerializableDataType.registryKey(Registries.DAMAGE_TYPE);
	public static final SerializableDataType<TagKey<EntityType<?>>> ENTITY_GROUP_TAG = SerializableDataType.mapped(ImmutableBiMap.of(
		"undead", EntityTypeTags.UNDEAD,
		"arthropod", EntityTypeTags.ARTHROPOD,
		"illager", EntityTypeTags.ILLAGER,
		"aquatic", EntityTypeTags.AQUATIC
	));
	public static final SerializableDataType<EquipmentSlot> EQUIPMENT_SLOT = SerializableDataType.enumValue(EquipmentSlot.class);
	public static final SerializableDataType<EnumSet<EquipmentSlot>> EQUIPMENT_SLOT_SET = SerializableDataType.enumSet(EQUIPMENT_SLOT);
	public static final SerializableDataType<EquipmentSlotGroup> ATTRIBUTE_MODIFIER_SLOT = SerializableDataType.enumValue(EquipmentSlotGroup.class);
	public static final SerializableDataType<EnumSet<EquipmentSlotGroup>> ATTRIBUTE_MODIFIER_SLOT_SET = SerializableDataType.enumSet(ATTRIBUTE_MODIFIER_SLOT);
	public static final SerializableDataType<SoundEvent> SOUND_EVENT = IDENTIFIER.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation);
	public static final SerializableDataType<EntityType<?>> ENTITY_TYPE = SerializableDataType.registry(BuiltInRegistries.ENTITY_TYPE);
	public static final SerializableDataType<ParticleType<?>> PARTICLE_TYPE = SerializableDataType.registry(BuiltInRegistries.PARTICLE_TYPE);
	public static final SerializableDataType<Tag> NBT_ELEMENT = SerializableDataType.of(
		Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(), nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement.copy()))
	);
	public static final SerializableDataType<CompoundTag> NBT_COMPOUND = SerializableDataType.of(Codec.withAlternative(CompoundTag.CODEC, TagParser.AS_CODEC));
	public static final SerializableDataType<ArgumentWrapper<NbtPathArgument.NbtPath>> NBT_PATH = SerializableDataType.argumentType(NbtPathArgument.nbtPath());
	public static final CompoundSerializableDataType<ParticleOptions> PARTICLE_EFFECT = new CompoundSerializableDataType<>(
		new SerializableData()
			.add("type", PARTICLE_TYPE)
			.add("params", NBT_COMPOUND, new CompoundTag()),
		serializableData -> new MapCodec<>() {

			@Override
			public <T> DataResult<ParticleOptions> decode(DynamicOps<T> ops, MapLike<T> input) {
				return serializableData.decode(ops, input).flatMap(data -> {

					ParticleType<?> particleType = data.get("type");
					CompoundTag paramsNbt = data.get("params");

					ResourceLocation particleTypeId = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), "Particle type (" + particleType + ") is not registered?");
					paramsNbt.putString("type", particleTypeId.toString());

					if (particleType instanceof SimpleParticleType simpleParticleType) {
						return DataResult.success(simpleParticleType);
					} else if (paramsNbt.size() <= 1) {
						return DataResult.error(() -> "Particle effect \"" + particleTypeId + "\" requires parameters!");
					} else if (ops instanceof RegistryOps<T> registryOps) {
						return ParticleTypes.CODEC.parse(registryOps.withParent(NbtOps.INSTANCE), paramsNbt);
					} else {
						return DataResult.error(() -> "Can't decode parameterized particle effects without registry ops!");
					}

				});
			}

			@Override
			public <T> RecordBuilder<T> encode(ParticleOptions input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

				ParticleType<?> particleType = input.getType();
				ResourceLocation particleTypeId = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), "Particle type (" + particleType + ") is not registered?");

				prefix.add("type", IDENTIFIER.write(ops, particleTypeId));

				if (particleType instanceof SimpleParticleType simpleParticleType) {
					return prefix;
				} else if (ops instanceof RegistryOps<T> registryOps) {

					RegistryOps<Tag> nbtOps = registryOps.withParent(NbtOps.INSTANCE);

					return prefix.add("params", ParticleTypes.CODEC.encodeStart(nbtOps, input)
						.flatMap(nbtElement -> nbtElement instanceof CompoundTag nbtCompound ? DataResult.success(nbtCompound) : DataResult.error(() -> "Not a compound tag: " + nbtElement))
						.ifSuccess(nbtCompound -> nbtCompound.remove("type"))
						.map(nbtCompound -> nbtOps.convertTo(ops, nbtCompound)));

				} else {
					return prefix.withErrorsFrom(DataResult.error(() -> "Can't encode parameterized particle effects without registry ops!"));
				}

			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return serializableData.keys(ops);
			}

		}
	);
	public static final SerializableDataType<ParticleOptions> PARTICLE_EFFECT_OR_TYPE = SerializableDataType.recursive(self -> {
		SerializableDataType<ParticleOptions> dataType = PARTICLE_EFFECT.setRoot(self.isRoot());
		return SerializableDataType.of(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<ParticleOptions, T>> decode(DynamicOps<T> ops, T input) {

					if (ops.getStringValue(input).isSuccess()) {
						return PARTICLE_TYPE.codec().parse(ops, input)
							.flatMap(type -> type instanceof SimpleParticleType simpleType
								? DataResult.success(simpleType)
								: DataResult.error(() -> "Particle effect \"" + BuiltInRegistries.PARTICLE_TYPE.getKey(type) + "\" requires parameters!"))
							.map(type -> Pair.of(type, input));
					} else {
						return dataType.codec().decode(ops, input);
					}

				}

				@Override
				public <T> DataResult<T> encode(ParticleOptions input, DynamicOps<T> ops, T prefix) {
					return dataType.codec().encode(input, ops, prefix);
				}

			}
		);
	});
	public static final SerializableDataType<Component> TEXT = SerializableDataType.of(ComponentSerialization.CODEC);
	public static final SerializableDataType<List<Component>> TEXTS = TEXT.list();
	public static final SerializableDataType<RecipeSerializer<?>> RECIPE_SERIALIZER = SerializableDataType.registry(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.DEFAULT_NAMESPACE, null, (recipeSerializers, id) -> "Recipe serializer \"" + id + "\" is not registered!");
	public static final CompoundSerializableDataType<Recipe<?>> RECIPE = new CompoundSerializableDataType<>(
		new SerializableData()
			.add("type", RECIPE_SERIALIZER),
		serializableData -> new MapCodec<>() {

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return serializableData.keys(ops);
			}

			@Override
			public <T> DataResult<Recipe<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
				return serializableData.decode(ops, input)
					.map(data -> (RecipeSerializer<?>) data.get("type"))
					.flatMap(recipeSerializer -> recipeSerializer.codec().decode(ops, input)
						.map(Function.identity()));
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> RecordBuilder<T> encode(Recipe<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

				RecipeSerializer<Recipe<?>> recipeSerializer = (RecipeSerializer<Recipe<?>>) input.getSerializer();

				prefix.add("type", RECIPE_SERIALIZER.write(ops, recipeSerializer));
				recipeSerializer.codec().encode(input, ops, prefix);

				return prefix;

			}

		}
	);
	public static final CompoundSerializableDataType<RecipeHolder<?>> RECIPE_ENTRY = new CompoundSerializableDataType<>(
		RECIPE.serializableData().copy()
			.add("id", IDENTIFIER),
		serializableData -> {
			CompoundSerializableDataType<Recipe<?>> recipeDataType = RECIPE.setRoot(serializableData.isRoot());
			return new MapCodec<>() {

				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return serializableData.keys(ops);
				}

				@Override
				public <T> DataResult<RecipeHolder<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
					return serializableData.decode(ops, input)
						.flatMap(data -> recipeDataType.mapCodec().decode(ops, input)
							.map(recipe -> new RecipeHolder<>(data.get("id"), recipe)));
				}

				@Override
				public <T> RecordBuilder<T> encode(RecipeHolder<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

					prefix.add("id", IDENTIFIER.write(ops, input.id()));
					recipeDataType.mapCodec().encode(input.value(), ops, prefix);

					return prefix;

				}

			};
		}
	);
	public static final SerializableDataType<GameEvent> GAME_EVENT = SerializableDataType.registry(BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataType<List<GameEvent>> GAME_EVENTS = GAME_EVENT.list();
	public static final SerializableDataType<Holder<GameEvent>> GAME_EVENT_ENTRY = SerializableDataType.registryEntry(BuiltInRegistries.GAME_EVENT);
	public static final SerializableDataType<List<Holder<GameEvent>>> GAME_EVENT_ENTRIES = GAME_EVENT_ENTRY.list();
	public static final SerializableDataType<TagKey<GameEvent>> GAME_EVENT_TAG = SerializableDataType.tagKey(Registries.GAME_EVENT);
	public static final SerializableDataType<Fluid> FLUID = SerializableDataType.registry(BuiltInRegistries.FLUID);
	public static final SerializableDataType<FogType> CAMERA_SUBMERSION_TYPE = SerializableDataType.enumValue(FogType.class);
	public static final SerializableDataType<InteractionHand> HAND = SerializableDataType.enumValue(InteractionHand.class, ImmutableMap.of(
		"mainhand", InteractionHand.MAIN_HAND,
		"offhand", InteractionHand.OFF_HAND
	));
	public static final SerializableDataType<EnumSet<InteractionHand>> HAND_SET = SerializableDataType.enumSet(HAND);
	public static final SerializableDataType<InteractionResult> ACTION_RESULT = SerializableDataType.enumValue(InteractionResult.class);
	public static final SerializableDataType<UseAnim> USE_ACTION = SerializableDataType.enumValue(UseAnim.class);
	public static final SerializableDataType<FoodProperties.PossibleEffect> FOOD_STATUS_EFFECT_ENTRY = SerializableDataType.of(FoodProperties.PossibleEffect.CODEC);
	public static final SerializableDataType<List<FoodProperties.PossibleEffect>> FOOD_STATUS_EFFECT_ENTRIES = FOOD_STATUS_EFFECT_ENTRY.list();
	public static final CompoundSerializableDataType<FoodProperties> FOOD_COMPONENT = SerializableDataType.compound(
		new SerializableData()
			.add("nutrition", NON_NEGATIVE_INT)
			.add("saturation", FLOAT)
			.add("can_always_eat", BOOLEAN, false)
			.add("eat_seconds", NON_NEGATIVE_FLOAT, 1.6F)
			.addSupplied("using_converts_to", UNCOUNTED_ITEM_STACK.optional(), Optional::empty)
			.add("effect", FOOD_STATUS_EFFECT_ENTRY, null)
			.add("effects", FOOD_STATUS_EFFECT_ENTRIES, null),
		data -> {

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

		},
		(foodComponent, serializableData) -> serializableData.instance()
			.set("nutrition", foodComponent.nutrition())
			.set("saturation", foodComponent.saturation())
			.set("can_always_eat", foodComponent.canAlwaysEat())
			.set("eat_seconds", foodComponent.eatSeconds())
			.set("using_converts_to", foodComponent.usingConvertsTo())
			.set("effects", foodComponent.effects())
	);
	public static final SerializableDataType<Direction> DIRECTION = SerializableDataType.enumValue(Direction.class);
	public static final SerializableDataType<EnumSet<Direction>> DIRECTION_SET = SerializableDataType.enumSet(DIRECTION);
	public static final SerializableDataType<Class<?>> CLASS = STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(Class.forName(str));
			} catch (ClassNotFoundException ignored) {
				return DataResult.error(() -> "Specified class does not exist: \"" + str + "\"");
			}

		},
		Class::getName
	);
	public static final SerializableDataType<ClipContext.Block> SHAPE_TYPE = SerializableDataType.enumValue(ClipContext.Block.class);
	public static final SerializableDataType<ClipContext.Fluid> FLUID_HANDLING = SerializableDataType.enumValue(ClipContext.Fluid.class);
	public static final SerializableDataType<Explosion.BlockInteraction> DESTRUCTION_TYPE = SerializableDataType.enumValue(Explosion.BlockInteraction.class);
	public static final SerializableDataType<Direction.Axis> AXIS = SerializableDataType.enumValue(Direction.Axis.class);
	public static final SerializableDataType<EnumSet<Direction.Axis>> AXIS_SET = SerializableDataType.enumSet(AXIS);
	public static final SerializableDataType<StatType<?>> STAT_TYPE = SerializableDataType.registry(BuiltInRegistries.STAT_TYPE);
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final CompoundSerializableDataType<Stat<?>> STAT = SerializableDataType.compound(
		new SerializableData()
			.add("type", STAT_TYPE)
			.add("id", IDENTIFIER),
		data -> {

			StatType statType = data.get("type");
			ResourceLocation statId = data.getId("id");

			Registry statRegistry = statType.getRegistry();
			ResourceLocation statTypeId = Objects.requireNonNull(BuiltInRegistries.STAT_TYPE.getKey(statType));

			try {
				return (Stat<?>) statRegistry.getOptional(statId)
					.map(statType::get)
					.orElseThrow();
			} catch (Exception e) {
				throw new IllegalArgumentException("Desired stat \"" + statId + "\" does not exist in stat type \"" + statTypeId + "\"");
			}

		},
		(stat, serializableData) -> {

			SerializableData.Instance data = serializableData.instance();

			StatType statType = stat.getType();
			Optional<ResourceLocation> optId = Optional.ofNullable(statType.getRegistry().getKey(stat.getValue()));

			data.set("type", statType);
			optId.ifPresent(id -> data.set("id", id));

			return data;

		}
	);
	public static final SerializableDataType<TagKey<Biome>> BIOME_TAG = SerializableDataType.tagKey(Registries.BIOME);
	public static final SerializableDataType<ExtraCodecs.TagOrElementLocation> TAG_ENTRY_ID = STRING.comapFlatMap(
		str -> str.startsWith("#")
			? DynamicIdentifier.ofResult(str.substring(1)).map(id -> new ExtraCodecs.TagOrElementLocation(id, true))
			: DynamicIdentifier.ofResult(str).map(id -> new ExtraCodecs.TagOrElementLocation(id, false)),
		ExtraCodecs.TagOrElementLocation::toString
	);
	public static final CompoundSerializableDataType<TagEntry> OBJECT_TAG_ENTRY = SerializableDataType.compound(
		new SerializableData()
			.add("id", TAG_ENTRY_ID)
			.add("required", BOOLEAN, true),
		data -> {
			Class<TagEntry> tagEntryClass = TagEntry.class;
			try {
				Constructor<TagEntry> constructor = tagEntryClass.getDeclaredConstructor(ExtraCodecs.TagOrElementLocation.class, boolean.class);
				TagEntry entry = constructor.newInstance(data.get("id"),
					data.get("required"));
				return entry;
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
					 InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		},
		(tagEntry, serializableData) -> serializableData.instance()
			.set("id", ((TagEntryAccessor) tagEntry).callElementOrTag())
			.set("required", ((TagEntryAccessor) tagEntry).isRequired())
	);
	public static final SerializableDataType<TagEntry> TAG_ENTRY = SerializableDataType.recursive(dataType -> SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<TagEntry, T>> decode(DynamicOps<T> ops, T input) {

				DataResult<Pair<TagEntry, T>> entryIdResult = TAG_ENTRY_ID.codec().decode(ops, input)
					.map(entryIdAndInput -> entryIdAndInput
						.mapFirst(entryId -> {
							Class<TagEntry> tagEntryClass = TagEntry.class;
							try {
								Constructor<TagEntry> constructor = tagEntryClass.getDeclaredConstructor(ExtraCodecs.TagOrElementLocation.class, boolean.class);
								TagEntry entry = constructor.newInstance(entryId, true);
								return entry;
							} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
									 InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						}));
				if (entryIdResult.isSuccess()) {
					return entryIdResult;
				}

				DataResult<Pair<TagEntry, T>> entryResult = OBJECT_TAG_ENTRY.setRoot(dataType.isRoot()).codec().decode(ops, input);
				if (entryResult.isSuccess()) {
					return OBJECT_TAG_ENTRY.codec().decode(ops, input);
				}

				StringBuilder errorBuilder = new StringBuilder("Couldn't decode tag entry");

				entryIdResult.ifError(error -> errorBuilder.append(" as an ID (").append(error.message()).append(")"));
				entryResult.ifError(error -> errorBuilder.append(" or as an object (").append(error.message()).append(")"));

				return DataResult.error(errorBuilder::toString);

			}

			@Override
			public <T> DataResult<T> encode(TagEntry input, DynamicOps<T> ops, T prefix) {

				if (((TagEntryAccessor) input).isRequired()) {
					return TAG_ENTRY_ID.codec().encode(((TagEntryAccessor) input).callElementOrTag(), ops, prefix);
				} else {
					return OBJECT_TAG_ENTRY.setRoot(dataType.isRoot()).codec().encode(input, ops, prefix);
				}

			}

		}
	));
	public static final SerializableDataType<List<TagEntry>> TAG_ENTRIES = TAG_ENTRY.list();
	public static final SerializableDataType<TagLike<Item>> ITEM_TAG_LIKE = SerializableDataType.tagLike(BuiltInRegistries.ITEM);
	public static final SerializableDataType<TagLike<Block>> BLOCK_TAG_LIKE = SerializableDataType.tagLike(BuiltInRegistries.BLOCK);
	public static final SerializableDataType<TagLike<EntityType<?>>> ENTITY_TYPE_TAG_LIKE = SerializableDataType.tagLike(BuiltInRegistries.ENTITY_TYPE);
	public static final SerializableDataType<PotionContents> POTION_CONTENTS_COMPONENT = SerializableDataType.of(PotionContents.CODEC);
	public static final SerializableDataType<ResourceKey<LootItemFunction>> ITEM_MODIFIER = SerializableDataType.registryKey(Registries.ITEM_MODIFIER);
	public static final SerializableDataType<ResourceKey<LootItemCondition>> PREDICATE = SerializableDataType.registryKey(Registries.PREDICATE);
	private static final SerializableDataType<Ingredient.ItemValue> INLINE_INGREDIENT_STACK_ENTRY = ITEM
		.xmap(ItemStack::new, ItemStack::getItem)
		.xmap(Ingredient.ItemValue::new, Ingredient.ItemValue::item);
	public static final SerializableDataType<Ingredient.Value> INLINE_INGREDIENT_ENTRY = SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<Ingredient.Value, T>> decode(DynamicOps<T> ops, T input) {
				return ops.getStringValue(input).flatMap(stringInput -> {

					DataResult<Pair<Ingredient.Value, T>> stackResult = INLINE_INGREDIENT_STACK_ENTRY
						.read(ops, input)
						.map(entry -> Pair.of(entry, input));

					if (stackResult.isSuccess()) {
						return stackResult;
					}

					DataResult<Pair<Ingredient.Value, T>> tagResult = INLINE_INGREDIENT_TAG_ENTRY
						.read(ops, input)
						.map(entry -> Pair.of(entry, input));

					if (tagResult.isSuccess()) {
						return tagResult;
					}

					StringBuilder errorBuilder = new StringBuilder("Couldn't decode ingredient entry");

					stackResult.ifError(error -> errorBuilder
						.append(" as an item (").append(error.message()).append(")"));
					tagResult.ifError(error -> errorBuilder
						.append(" or as a tag (").append(error.message()).append(")"));

					return DataResult.error(errorBuilder::toString);

				});
			}

			@Override
			public <T> DataResult<T> encode(Ingredient.Value input, DynamicOps<T> ops, T prefix) {
				return switch (input) {
					case Ingredient.ItemValue stackEntry ->
						INLINE_INGREDIENT_STACK_ENTRY.codec().encode(stackEntry, ops, prefix);
					case Ingredient.TagValue tagEntry ->
						INLINE_INGREDIENT_TAG_ENTRY.codec().encode(tagEntry, ops, prefix);
					default -> DataResult.error(() -> "Ingredient entry is not an item or tag!");
				};
			}
		}
	);
	public static final SerializableDataType<Ingredient.Value> INGREDIENT_ENTRY = SerializableDataType.recursive(dataType -> SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<Ingredient.Value, T>> decode(DynamicOps<T> ops, T input) {

				if (ops.getMap(input).isSuccess()) {
					return OBJECT_INGREDIENT_ENTRY.setRoot(dataType.isRoot()).codec().decode(ops, input);
				} else {
					return INLINE_INGREDIENT_ENTRY.codec().decode(ops, input);
				}

			}

			@Override
			public <T> DataResult<T> encode(Ingredient.Value input, DynamicOps<T> ops, T prefix) {
				return INLINE_INGREDIENT_ENTRY.codec().encode(input, ops, prefix);
			}

		}
	));
	public static final SerializableDataType<List<Ingredient.Value>> INGREDIENT_ENTRIES = INGREDIENT_ENTRY.list(1, Integer.MAX_VALUE);
	private static final SerializableDataType<Ingredient.Value[]> INGREDIENT_ENTRIES_ARRAY = INGREDIENT_ENTRIES.xmap(entries -> entries.toArray(Ingredient.Value[]::new), ObjectArrayList::new);
	private static final SerializableDataType<Stream<Ingredient.Value>> INGREDIENT_ENTRIES_STREAM = INGREDIENT_ENTRIES_ARRAY.xmap(Arrays::stream, (a) -> (Ingredient.Value[]) a.toArray());
	public static final SerializableDataType<Ingredient> INGREDIENT = SerializableDataType.recursive(dataType -> SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {

				return INGREDIENT_ENTRIES_STREAM.setRoot(dataType.isRoot()).codec().decode(ops, input)
					.map(entriesAndInput -> entriesAndInput
						.mapFirst(Ingredient::new));
			}

			@Override
			public <T> DataResult<T> encode(Ingredient input, DynamicOps<T> ops, T prefix) {

				try {
					Field f = input.getClass().getDeclaredField("values");
					f.setAccessible(true);
					return INGREDIENT_ENTRIES_ARRAY.setRoot(dataType.isRoot()).codec().encode((Ingredient.Value[]) f.get(input), ops, prefix);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}

			}

		}
	));
	public static SerializableDataType<ResourceKey<Level>> DIMENSION = SerializableDataType.registryKey(Registries.DIMENSION, Set.of(
		Level.OVERWORLD,
		Level.NETHER,
		Level.END
	));
}
