package io.github.dueris.calio.data;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.github.dueris.calio.codec.JsonCodec;
import io.github.dueris.calio.mixin.ShufflingListAccessor;
import io.github.dueris.calio.util.*;
import io.github.dueris.calio.registry.DataObjectFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializableDataType<T> {

	private static final Supplier<SerializableDataType<Set<TagEntry>>> TAG_ENTRY_SET = Suppliers.memoize(() -> SerializableDataTypes.TAG_ENTRIES.xmap(
		ObjectOpenHashSet::new,
		ObjectArrayList::new
	));
	private final Codec<T> codec;
	private final Optional<String> name;
	private final boolean root;

	public SerializableDataType(Codec<T> codec, Optional<String> name, boolean root) {
		this.codec = codec;
		this.name = name;
		this.root = root;
	}

	public SerializableDataType(Codec<T> codec) {
		this(codec, Optional.empty(), true);
	}

	@Deprecated(forRemoval = true/* marked for removal by OriginsPaper */)
	public SerializableDataType(Class<?> dataClass, Function<JsonElement, T> fromJson, Function<T, JsonElement> toJson) {
		this(new JsonCodec<>(fromJson, toJson));
	}

	public static <T> @NotNull SerializableDataType<T> of(Codec<T> codec) {
		return new SerializableDataType<>(codec);
	}

	public static <T> @NotNull SerializableDataType<T> jsonBacked(Function<T, JsonElement> toJson, Function<JsonElement, T> fromJson) {
		return new SerializableDataType<>(new JsonCodec<>(fromJson, toJson));
	}

	public static <T> @NotNull RecursiveSerializableDataType<T> recursive(Function<SerializableDataType<T>, SerializableDataType<T>> wrapped) {
		return new RecursiveSerializableDataType<>(wrapped);
	}

	public static <T> @NotNull RecursiveSerializableDataType<T> lazy(Supplier<SerializableDataType<T>> delegate) {
		return recursive(self -> delegate.get());
	}

	public static <T> @NotNull SerializableDataTypeList<T> list(SerializableDataType<T> singleDataType) {
		return list(singleDataType, Integer.MAX_VALUE);
	}

	public static <T> @NotNull SerializableDataTypeList<T> list(SerializableDataType<T> singleDataType, int max) {
		return list(singleDataType, 0, max);
	}

	public static <T> @NotNull SerializableDataTypeList<T> list(SerializableDataType<T> singleDataType, int min, int max) {
		return new SerializableDataTypeList<>(new SerializableDataTypeList.CustomCodec<>(singleDataType, min, max));
	}

	@SuppressWarnings("unchecked")
	public static <T> SerializableDataType<ShufflingList<T>> weightedList(SerializableDataType<T> singleDataType) {

		CompoundSerializableDataType<ShufflingList.WeightedEntry<T>> entryDataType = compound(
			new SerializableData()
				.add("element", singleDataType)
				.add("weight", SerializableDataTypes.INT, 1),
			data -> {
				Class<ShufflingList.WeightedEntry> weightedEntryClass = ShufflingList.WeightedEntry.class;
				try {
					Constructor<ShufflingList.WeightedEntry> constructor = weightedEntryClass.getDeclaredConstructor(Object.class, int.class);
					return constructor.newInstance(data.get("element"), data.get("weight"));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
						 NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			},
			(entry, serializableData) -> serializableData.instance()
				.set("element", entry.getData())
				.set("weight", entry.getWeight())
		);

		return entryDataType.list().xmap(
			(list) -> {
				Class<ShufflingList> shufflingListClass = ShufflingList.class;
				try {
					Constructor<ShufflingList> constructor = shufflingListClass.getDeclaredConstructor(List.class);
					return constructor.newInstance(list);
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
						 InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			},
			weightedList -> new ObjectArrayList<>(((ShufflingListAccessor<T>) weightedList).getEntries())
		);

	}

	/**
	 *  Use {@link #registry(Registry)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry) {
		return registry(dataClass, registry, false);
	}

	/**
	 *  Use {@link #registry(Registry, String)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry, String defaultNamespace) {
		return registry(dataClass, registry, defaultNamespace, false);
	}

	/**
	 *  Use {@link #registry(Registry, boolean)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry, boolean showPossibleValues) {
		return registry(dataClass, registry, ResourceLocation.DEFAULT_NAMESPACE, showPossibleValues);
	}

	/**
	 *  Use {@link #registry(Registry, String, boolean)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry, String defaultNamespace, boolean showPossibleValues) {
		return registry(dataClass, registry, defaultNamespace, null, (reg, id) -> {
			String possibleValues = showPossibleValues ? ". Expected value to be any of " + String.join(", ", reg.keySet().stream().map(ResourceLocation::toString).toList()) : "";
			return new RuntimeException("Type \"%s\" is not registered in registry \"%s\"%s".formatted(id, registry.key().location(), possibleValues));
		});
	}

	/**
	 *  Use {@link #registry(Registry, BiFunction)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry, BiFunction<Registry<T>, ResourceLocation, RuntimeException> exception) {
		return registry(dataClass, registry, ResourceLocation.DEFAULT_NAMESPACE, null, exception);
	}

	/**
	 *  Use {@link #registry(Registry, String, IdentifierAlias, BiFunction)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry, String defaultNamespace, @Nullable IdentifierAlias aliases, BiFunction<Registry<T>, ResourceLocation, RuntimeException> exception) {
		return registry(registry, defaultNamespace, aliases, (_registry, id) -> exception.apply(_registry, id).getMessage());
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry) {
		return registry(registry, false);
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry, String defaultNamespace) {
		return registry(registry, defaultNamespace, false);
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry, boolean showPossibleValues) {
		return registry(registry, ResourceLocation.DEFAULT_NAMESPACE, showPossibleValues);
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry, String defaultNamespace, boolean showPossibleValues) {
		return registry(registry, defaultNamespace, null, (reg, id) -> {
			String possibleValues = showPossibleValues ? " Expected value to be any of " + String.join(", ", reg.keySet().stream().map(ResourceLocation::toString).toList()) : "";
			return "Type \"%s\" is not registered in registry \"%s\"!%s".formatted(id, registry.key().location(), possibleValues);
		});
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry, BiFunction<Registry<T>, ResourceLocation, String> exception) {
		return registry(registry, ResourceLocation.DEFAULT_NAMESPACE, null, exception);
	}

	public static <T> SerializableDataType<T> registry(Registry<T> registry, String defaultNamespace, @Nullable IdentifierAlias aliases, BiFunction<Registry<T>, ResourceLocation, String> exception) {
		return of(
			new Codec<>() {

				@Override
				public <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input) {
					return ops.getStringValue(input)
						.flatMap(str -> DynamicIdentifier.ofResult(str, defaultNamespace))
						.flatMap(id -> registry
							.getOptional(aliases == null ? id : aliases.resolveAlias(id, registry::containsKey))
							.map(t -> Pair.of(t, input))
							.map(DataResult::success)
							.orElse(DataResult.error(() -> exception.apply(registry, id))));
				}

				@Override
				public <I> DataResult<I> encode(T input, DynamicOps<I> ops, I prefix) {
					return registry.byNameCodec().encode(input, ops, prefix);
				}

			}
		);
	}

	public static <A> @NotNull SerializableDataType<Holder<A>> registryEntry(Registry<A> registry) {
		return new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<Holder<A>, T>> decode(DynamicOps<T> ops, T input) {
					return SerializableDataTypes.IDENTIFIER.codec().decode(ops, input)
						.flatMap(idAndInput -> {

							Pair<ResourceKey<A>, T> keyAndInput = idAndInput.mapFirst(id -> ResourceKey.create(registry.key(), id));
							ResourceKey<A> key = keyAndInput.getFirst();

							return registry.getHolder(key)
								.map(entry -> keyAndInput.mapFirst(k -> (Holder<A>) entry))
								.map(DataResult::success)
								.orElse(DataResult.error(() -> "Type \"" + key.location() + "\" is not registered in registry \"" + registry.key().location() + "\"!"));

						});
				}

				@Override
				public <T> DataResult<T> encode(Holder<A> input, DynamicOps<T> ops, T prefix) {
					return input.unwrap().map(
						key -> SerializableDataTypes.IDENTIFIER.codec().encode(key.location(), ops, prefix),
						a -> registry.byNameCodec().encode(a, ops, prefix)
					);
				}

			}
		);
	}

	/**
	 * <p>Use any of the following instead:</p>
	 *
	 * <ul>
	 *     <li>{@link #compound(DataObjectFactory) compound(DataObjectFactory&lt;T&gt;)} for processing with a {@link DataObjectFactory}.</li>
	 *     <li>{@link #compound(SerializableData, Function, BiFunction) compound(SerializableData, Function&lt;SerializableData.Instance, T&gt, BiFunction&lt;T, SerializableData, SerializableData.Instance&gt;)} for simple processing.</li>
	 *     <li>{@link CompoundSerializableDataType(SerializableData, Function, Function) CompoundSerializableDataType(SerializableData, Function&lt;SerializableData, MapCodec&lt;T&gt;&gt;, Function&lt;SerializableData, PacketCodec&lt;RegistryByteBuf, T&gt;&gt;)} for more granular control on how the element will be decoded/encoded.</li>
	 * </ul>
	 */
	@Deprecated
	public static <T> @NotNull CompoundSerializableDataType<T> compound(Class<T> dataClass, SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<SerializableData, T, SerializableData.Instance> toData) {
		return compound(serializableData, fromData, (t, _serializableData) -> toData.apply(_serializableData, t));
	}

	public static <T> @NotNull CompoundSerializableDataType<T> compound(@NotNull DataObjectFactory<T> factory) {
		return compound(factory.getSerializableData(), factory::fromData, factory::toData);
	}

	public static <T> @NotNull CompoundSerializableDataType<T> compound(SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return new CompoundSerializableDataType<>(
			serializableData,
			_serializableData -> new MapCodec<>() {

				@Override
				public <I> DataResult<T> decode(DynamicOps<I> ops, MapLike<I> input) {
					return _serializableData.decode(ops, input).flatMap(data -> {

						try {
							return DataResult.success(fromData.apply(data));
						} catch (Exception e) {

							if (_serializableData.isRoot()) {
								return DataResult.error(e::getMessage);
							} else {
								throw e;
							}

						}

					});
				}

				@Override
				public <I> RecordBuilder<I> encode(T input, DynamicOps<I> ops, RecordBuilder<I> prefix) {

					try {
						return _serializableData.encode(toData.apply(input, _serializableData), ops, prefix);
					} catch (Exception e) {

						if (_serializableData.isRoot()) {
							return prefix.withErrorsFrom(DataResult.error(e::getMessage));
						} else {
							throw e;
						}

					}

				}

				@Override
				public <I> Stream<I> keys(DynamicOps<I> ops) {
					return _serializableData.keys(ops);
				}

			}
		);
	}

	public static <V> SerializableDataType<Map<String, V>> map(SerializableDataType<V> valueDataType) {
		return lazy(() -> map(SerializableDataTypes.STRING, valueDataType));
	}

	public static <K, V> SerializableDataType<Map<K, V>> map(SerializableDataType<K> keyDataType, SerializableDataType<V> valueDataType) {
		return new SerializableDataType<>(
			new UnboundedMapCodec<>(keyDataType.codec(), valueDataType.codec())
		);
	}

	/**
	 * Use {@link #mapped(Supplier)} <b>(recommended)</b> or {@link #mapped(BiMap)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> mapped(Class<T> dataClass, @NotNull BiMap<String, T> biMap) {
		return mapped(biMap);
	}

	public static <V> SerializableDataType<V> mapped(@NotNull BiMap<String, V> biMap) {
		return mapped(Suppliers.memoize(() -> biMap));
	}

	public static <V> SerializableDataType<V> mapped(Supplier<BiMap<String, V>> acceptedValuesSupplier) {
		return new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
					return ops.getStringValue(input)
						.flatMap(stringInput -> {

							BiMap<String, V> acceptedValues = acceptedValuesSupplier.get();
							V value = acceptedValues.get(stringInput);

							if (acceptedValues.containsKey(stringInput)) {
								return DataResult.success(Pair.of(value, input));
							} else {
								return DataResult.error(() -> "Expected value to be any of " + String.join(", ", acceptedValues.keySet()));
							}

						});
				}

				@Override
				public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {

					BiMap<String, V> acceptedValues = acceptedValuesSupplier.get();
					String key = acceptedValues.inverse().get(input);

					if (key != null) {
						return DataResult.success(ops.createString(key));
					} else {
						return DataResult.error(() -> "Element " + input + " is not associated with any keys!");
					}

				}

			}
		);
	}

	/**
	 * Use {@link #xmap(Function, Function)} instead.
	 */
	@Deprecated
	public static <T, U> SerializableDataType<T> wrap(Class<T> dataClass, SerializableDataType<U> base, Function<T, U> toFunction, Function<U, T> fromFunction) {
		return base.xmap(fromFunction, toFunction);
	}

	/**
	 * Use {@link #tagKey(ResourceKey)} instead.
	 */
	@Deprecated
	public static <T> SerializableDataType<TagKey<T>> tag(ResourceKey<? extends Registry<T>> registryRef) {
		return tagKey(registryRef);
	}

	public static <T> SerializableDataType<TagKey<T>> tagKey(ResourceKey<? extends Registry<T>> registryRef) {
		return lazy(() -> new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <I> DataResult<Pair<TagKey<T>, I>> decode(DynamicOps<I> ops, I input) {
					return SerializableDataTypes.IDENTIFIER.codec().decode(ops, input)
						.map(idAndInput -> idAndInput.mapFirst(id -> TagKey.create(registryRef, id)));
				}

				@Override
				public <I> DataResult<I> encode(TagKey<T> input, DynamicOps<I> ops, I prefix) {
					return SerializableDataTypes.IDENTIFIER.codec().encode(input.location(), ops, prefix);
				}

			}
		));
	}

	public static <A> @NotNull SerializableDataType<Holder<A>> holder(Registry<A> registry) {
		return new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<Holder<A>, T>> decode(DynamicOps<T> ops, T input) {
					return SerializableDataTypes.IDENTIFIER.codec().decode(ops, input)
						.flatMap(idAndInput -> {

							Pair<ResourceKey<A>, T> keyAndInput = idAndInput.mapFirst(id -> ResourceKey.create(registry.key(), id));
							ResourceKey<A> key = keyAndInput.getFirst();

							return registry.getHolder(key)
								.map(entry -> keyAndInput.mapFirst(k -> (Holder<A>) entry))
								.map(DataResult::success)
								.orElse(DataResult.error(() -> "Type \"" + key.location() + "\" is not registered in registry \"" + registry.key().location() + "\"!"));

						});
				}

				@Override
				public <T> DataResult<T> encode(Holder<A> input, DynamicOps<T> ops, T prefix) {
					return input.unwrap().map(
						key -> SerializableDataTypes.IDENTIFIER.codec().encode(key.location(), ops, prefix),
						a -> registry.byNameCodec().encode(a, ops, prefix)
					);
				}

			}
		);
	}

	public static <T> SerializableDataType<ResourceKey<T>> registryKey(ResourceKey<? extends Registry<T>> registryRef) {
		return registryKey(registryRef, Set.of());
	}

	public static <T> SerializableDataType<ResourceKey<T>> registryKey(ResourceKey<? extends Registry<T>> registryRef, Collection<ResourceKey<T>> exemptions) {
		return new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <I> DataResult<Pair<ResourceKey<T>, I>> decode(DynamicOps<I> ops, I input) {
					return SerializableDataTypes.IDENTIFIER.codec().decode(ops, input)
						.flatMap(idAndInput -> {

							Pair<ResourceKey<T>, I> keyAndInput = idAndInput.mapFirst(id -> ResourceKey.create(registryRef, id));
							ResourceKey<T> key = keyAndInput.getFirst();

							if (exemptions.contains(key)) {
								return DataResult.success(keyAndInput);
							} else {
								return Util.getEntryLookup(ops, registryRef)
									.map(DataResult::success)
									.orElse(DataResult.error(() -> "Couldn't find registry \"" + registryRef.location() + "\"; " + (ops instanceof RegistryOps<I> ? "it doesn't exist!" : "the passed dynamic ops is not a registry ops!")))
									.flatMap(entryLookup -> entryLookup.get(key)
										.map(ref -> keyAndInput)
										.map(DataResult::success)
										.orElse(DataResult.error(() -> "Type \"" + key.location() + "\" is not registered in registry \"" + registryRef.location() + "\"!")));
							}

						});
				}

				@Override
				public <I> DataResult<I> encode(ResourceKey<T> input, DynamicOps<I> ops, I prefix) {
					return SerializableDataTypes.IDENTIFIER.codec().encode(input.location(), ops, prefix);
				}

			}
		);
	}

	public static <E extends Enum<E>> SerializableDataType<E> enumValue(Class<E> enumClass) {
		return enumValueInternal(enumClass, HashMap::new);
	}

	public static <E extends Enum<E>> SerializableDataType<E> enumValue(Class<E> enumClass, Map<String, E> additionalMap) {
		return enumValue(enumClass, () -> additionalMap);
	}

	public static <E extends Enum<E>> SerializableDataType<E> enumValue(Class<E> enumClass, Supplier<Map<String, E>> additionalMapSupplier) {
		return enumValueInternal(enumClass, Suppliers.memoize(() -> additionalMapSupplier.get().entrySet()
			.stream()
			.map(e -> Map.entry(e.getKey().toUpperCase(Locale.ROOT), e.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> curr, HashMap::new))));
	}

	@ApiStatus.Internal
	private static <E extends Enum<E>> @NotNull SerializableDataType<E> enumValueInternal(@NotNull Class<E> enumClass, Supplier<Map<String, E>> additionalMapSupplier) {
		IntFunction<E> ordinalToEnum = ByIdMap.continuous(Enum::ordinal, enumClass.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.CLAMP);
		return new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {

					DataResult<Pair<E, T>> enumResult = ops.getNumberValue(input)
						.map(Number::intValue)
						.map(ordinalToEnum::apply)
						.map(e -> Pair.of(e, input));

					if (enumResult.isSuccess()) {
						return enumResult;
					} else {
						return ops.getStringValue(input)
							.map(stringInput -> stringInput.toUpperCase(Locale.ROOT))
							.flatMap(stringInput -> {

								Map<String, E> additionalMap = additionalMapSupplier.get();
								E[] enumValues = enumClass.getEnumConstants();

								if (additionalMap.containsKey(stringInput)) {
									return DataResult.success(Pair.of(additionalMap.get(stringInput), input));
								}

								E queriedEnum = EnumUtils.getEnumIgnoreCase(enumClass, stringInput);
								if (queriedEnum != null) {
									return DataResult.success(Pair.of(queriedEnum, input));
								} else {

									Set<String> validValues = new LinkedHashSet<>();

									Stream.of(enumValues).map(Enum::name).forEach(validValues::add);
									validValues.addAll(additionalMap.keySet());

									return DataResult.error(() -> "Expected value to be any of: " + String.join(", ", validValues) + " (case-insensitive)");

								}

							});
					}

				}

				@Override
				public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
					return DataResult.success(ops.createString(input.name()));
				}

			}
		);
	}

	/**
	 * Use {@link #enumSet(SerializableDataType)} instead.
	 */
	@Deprecated
	public static <E extends Enum<E>> SerializableDataType<EnumSet<E>> enumSet(Class<E> enumClass, SerializableDataType<E> enumDataType) {
		return enumSet(enumDataType);
	}

	public static <E extends Enum<E>> SerializableDataType<EnumSet<E>> enumSet(SerializableDataType<E> enumDataType) {
		return enumDataType.list(1, Integer.MAX_VALUE).xmap(
			EnumSet::copyOf,
			ObjectArrayList::new
		);
	}

	@Deprecated
	public static <N extends Number & Comparable<N>> SerializableDataType<N> boundNumber(SerializableDataType<N> numberDataType, N min, N max, Function<N, BiFunction<N, N, N>> read) {
		return numberDataType.comapFlatMap(
			number -> {

				try {
					return DataResult.success(read.apply(number).apply(min, max));
				} catch (Exception e) {
					return DataResult.error(e::getMessage);
				}

			},
			Function.identity()
		);
	}

	public static <N extends Number & Comparable<N>> SerializableDataType<N> boundNumber(SerializableDataType<N> numberDataType, N min, N max) {
		return boundNumber(numberDataType,
			min, (value, _min) -> "Expected value to be at least " + _min + "! (current value: " + value + ")",
			max, (value, _max) -> "Expected value to be at most " + _max + "! (current value: " + value + ")"
		);
	}

	public static <N extends Number & Comparable<N>> SerializableDataType<N> boundNumber(SerializableDataType<N> numberDataType, N min, BiFunction<N, N, String> underMinError, N max, BiFunction<N, N, String> overMaxError) {
		return numberDataType.comapFlatMap(
			number -> {

				if (number.compareTo(min) < 0) {
					return DataResult.error(() -> underMinError.apply(number, min));
				} else if (number.compareTo(max) > 0) {
					return DataResult.error(() -> overMaxError.apply(number, max));
				} else {
					return DataResult.success(number);
				}

			},
			Function.identity()
		);
	}

	public static <T, U extends ArgumentType<T>> @NotNull SerializableDataType<ArgumentWrapper<T>> argumentType(U argumentType) {
		return lazy(() -> SerializableDataTypes.STRING.comapFlatMap(
			input -> {

				try {

					StringReader inputReader = new StringReader(input);
					T argument = argumentType.parse(inputReader);

					return DataResult.success(new ArgumentWrapper<>(argument, input));

				} catch (Exception e) {
					return DataResult.error(e::getMessage);
				}

			},
			ArgumentWrapper::input
		));
	}

	public static <E> @NotNull SerializableDataType<TagLike<E>> tagLike(Registry<E> registry) {
		return lazy(() -> of(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<TagLike<E>, T>> decode(DynamicOps<T> ops, T input) {
					return TAG_ENTRY_SET.get().codec().decode(ops, input)
						.map(entriesAndInput -> entriesAndInput
							.mapFirst(entries -> new TagLike.Builder<>(registry.key(), entries))
							.mapFirst(builder -> builder.build(registry.asLookup())));
				}

				@Override
				public <T> DataResult<T> encode(TagLike<E> input, DynamicOps<T> ops, T prefix) {
					return TAG_ENTRY_SET.get().codec().encode(input.entries(), ops, prefix);
				}

			}
		));
	}

	public static <E> SerializableDataType<TagLike<E>> tagLike(ResourceKey<E> registryKey) {
		return lazy(() -> of(
			new Codec<>() {

				private final ResourceKey<? extends Registry<E>> registryRef = registryKey.registryKey();

				@Override
				public <T> DataResult<Pair<TagLike<E>, T>> decode(DynamicOps<T> ops, T input) {
					return Util.getEntryLookup(ops, registryRef)
						.map(DataResult::success)
						.orElse(DataResult.error(() -> "Couldn't find registry \"" + registryRef.location() + "\"; " + (ops instanceof RegistryOps<T> ? "it doesn't exist!" : "the passed dynamic ops is not a registry ops!")))
						.flatMap(entryLookup -> TAG_ENTRY_SET.get().codec().decode(ops, input)
							.map(entriesAndInput -> entriesAndInput
								.mapFirst(entries -> new TagLike.Builder<>(registryRef, entries))
								.mapFirst(builder -> builder.build(entryLookup))));
				}

				@Override
				public <T> DataResult<T> encode(TagLike<E> input, DynamicOps<T> ops, T prefix) {
					return TAG_ENTRY_SET.get().codec().encode(input.entries(), ops, prefix);
				}

			}
		));
	}

	public static <T> SerializableDataType<Optional<T>> optional(SerializableDataType<T> dataType, boolean lenient) {
		return optional(dataType, lenient, warn -> {
		});
	}

	public static <A> SerializableDataType<Optional<A>> optional(SerializableDataType<A> dataType, boolean lenient, Consumer<String> warningHandler) {
		return recursive(optDt -> of(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> ops, T input) {
					return dataType.setRoot(optDt.isRoot()).codec().decode(ops, input)
						.map(aAndInput -> aAndInput.mapFirst(Optional::of))
						.mapOrElse(
							DataResult::success,
							error -> {

								if (lenient) {
									warningHandler.accept(error.message());
									return DataResult.success(Pair.of(Optional.empty(), input));
								} else {
									return error;
								}

							}
						);
				}

				@Override
				public <T> DataResult<T> encode(Optional<A> input, DynamicOps<T> ops, T prefix) {
					return input
						.map(a -> dataType.setRoot(optDt.isRoot()).codec().encodeStart(ops, a))
						.orElse(DataResult.success(prefix));
				}

			}
		));
	}

	public Optional<String> getName() {
		return name;
	}

	@Override
	public String toString() {
		return name.orElseGet(super::toString);
	}

	public Codec<T> codec() {
		return codec;
	}

	public <I> DataResult<T> read(DynamicOps<I> ops, I input) {
		return this.codec().parse(ops, input);
	}

	public <I> DataResult<I> write(DynamicOps<I> ops, T input) {
		return this.codec().encodeStart(ops, input);
	}

	/**
	 * Use {@link #read(DynamicOps, Object)} with {@link JsonOps#INSTANCE} instead.
	 */
	@Deprecated
	public T read(JsonElement jsonElement) {
		return read(JsonOps.INSTANCE, jsonElement).getOrThrow();
	}

	/**
	 * Use {@link #write(DynamicOps, T)} with {@link JsonOps#INSTANCE} instead.
	 */
	@Deprecated
	public JsonElement writeUnsafely(Object value) throws Exception {

		try {
			return this.write(this.cast(value));
		} catch (ClassCastException e) {
			throw new Exception(e);
		}

	}

	/**
	 * Use {@link #write(DynamicOps, T)} with {@link JsonOps#INSTANCE} instead.
	 */
	@Deprecated
	public JsonElement write(T value) {
		return write(JsonOps.INSTANCE, value).getOrThrow();
	}

	public T cast(Object data) {
		return (T) data;
	}

	public <S> SerializableDataType<S> xmap(Function<? super T, ? extends S> to, Function<? super S, ? extends T> from) {
		return new SerializableDataType<>(codec().xmap(to, from), this.getName(), this.isRoot());
	}

	public <S> SerializableDataType<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from) {
		return new SerializableDataType<>(codec().comapFlatMap(to, from), this.getName(), this.isRoot());
	}

	public <S> SerializableDataType<S> flatComapMap(Function<? super T, ? extends S> to, Function<? super S, ? extends DataResult<? extends T>> from) {
		return new SerializableDataType<>(codec().flatComapMap(to, from), this.getName(), this.isRoot());
	}

	public <S> SerializableDataType<S> flatXmap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends T>> from) {
		return new SerializableDataType<>(codec().flatXmap(to, from), this.getName(), this.isRoot());
	}

	public SerializableDataType<List<T>> list() {
		return list(this);
	}

	public SerializableDataType<List<T>> list(int max) {
		return list(this, max);
	}

	public SerializableDataType<List<T>> list(int min, int max) {
		return list(this, min, max);
	}

	public SerializableDataType<Optional<T>> optional() {
		return optional(this, false);
	}

	public SerializableDataType<Optional<T>> lenientOptional() {
		return optional(this, true);
	}

	public SerializableDataType<Optional<T>> lenientOptional(Consumer<String> warningHandler) {
		return optional(this, true, warningHandler);
	}

	public boolean isRoot() {
		return root;
	}

	public SerializableDataType<T> setRoot(boolean root) {
		return new SerializableDataType<>(this.codec, this.name, root);
	}

	public SerializableData.Field<T> field(String name) {
		return new SerializableData.FieldImpl<>(name, setRoot(false));
	}

	public SerializableData.Field<T> field(String name, Supplier<T> defaultSupplier) {
		return new SerializableData.OptionalFieldImpl<>(name, setRoot(false), defaultSupplier);
	}

	public SerializableData.Field<T> functionedField(String name, Function<SerializableData.Instance, T> defaultFunction) {
		return new SerializableData.FunctionedFieldImpl<>(name, setRoot(false), defaultFunction);
	}

}
