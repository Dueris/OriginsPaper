package io.github.dueris.calio.data;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.*;
import io.github.dueris.calio.data.exceptions.DataException;
import io.github.dueris.calio.data.exceptions.DataException.Phase;
import io.github.dueris.calio.util.Validatable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SerializableData extends MapCodec<SerializableData.Instance> {
	// Should be set to the current namespace of the file that is being read. Allows using * in identifiers.
	public static String CURRENT_NAMESPACE;

	// Should be set to the current path of the file that is being read. Allows using * in identifiers.
	public static String CURRENT_PATH;

	protected final Map<String, Field<?>> fields;
	protected final Function<Instance, DataResult<Instance>> validator;

	protected final boolean root;

	protected SerializableData(Map<String, Field<?>> fields, Function<Instance, DataResult<Instance>> validator, boolean root) {
		this.fields = new LinkedHashMap<>(fields);
		this.validator = validator;
		this.root = root;
	}

	public SerializableData() {
		this.fields = new LinkedHashMap<>();
		this.validator = DataResult::success;
		this.root = true;
	}

	@Override
	public SerializableData validate(Function<Instance, DataResult<Instance>> validator) {
		return new SerializableData(this.fields, validator, this.root);
	}

	@Override
	public <T> Stream<T> keys(@NotNull DynamicOps<T> ops) {
		return SerializableData.this.getFieldNames()
			.stream()
			.map(ops::createString);
	}

	@Override
	public <T> DataResult<Instance> decode(DynamicOps<T> ops, MapLike<T> mapInput) {

		try {

			Instance data = instance();
			Map<String, Field<?>> defaultedFields = new Object2ObjectLinkedOpenHashMap<>();

			getFields().forEach((fieldName, field) -> {

				try {

					if (mapInput.get(fieldName) != null) {
						data.set(fieldName, field.read(ops, mapInput.get(fieldName)).getOrThrow());
					} else if (field.hasDefault()) {
						defaultedFields.put(fieldName, field);
					} else {
						throw new NoSuchFieldException("Required field \"" + fieldName + "\" is missing!");
					}

				} catch (DataException de) {
					throw de.prepend(field.path());
				} catch (NoSuchFieldException nsfe) {
					throw new DataException(Phase.READING, "", nsfe);
				} catch (Exception e) {
					throw new DataException(Phase.READING, field.path(), e);
				}

			});

			defaultedFields.forEach((fieldName, field) -> data.set(fieldName, field.getDefault(data)));
			return DataResult.success(data).flatMap(validator);

		} catch (Exception e) {

			if (root) {
				return DataResult.error(e::getMessage);
			} else {
				throw e;
			}

		}

	}

	@Override
	public <T> RecordBuilder<T> encode(Instance input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

		try {

			getFields().forEach((fieldName, field) -> {

				try {

					if (input.isPresent(fieldName)) {
						prefix.add(fieldName, field.write(ops, input.get(fieldName)).getOrThrow());
					}

				} catch (DataException de) {
					throw de.prepend(field.path());
				} catch (Exception e) {
					throw new DataException(Phase.WRITING, field.path(), e);
				}

			});

			return prefix;

		} catch (Exception e) {

			if (root) {
				return prefix.withErrorsFrom(DataResult.error(e::getMessage));
			} else {
				throw e;
			}

		}

	}

	public String toString() {
		return "SerializableData[fields = " + String.valueOf(this.fields) + "]";
	}

	/**
	 * @deprecated
	 */
	@Deprecated(
		forRemoval = true
	)
	public Instance read(JsonObject jsonObject) {
		return this.decoder().parse(JsonOps.INSTANCE, jsonObject).getOrThrow(JsonParseException::new);
	}

	/**
	 * @deprecated
	 */
	@Deprecated(
		forRemoval = true
	)
	public JsonObject write(Instance data) {
		return encoder().encodeStart(JsonOps.INSTANCE, data)
			.flatMap(jsonElement -> jsonElement instanceof JsonObject jsonObject ? DataResult.success(jsonObject) : DataResult.error(() -> "Not a JSON object: " + jsonElement))
			.getOrThrow(JsonParseException::new);
	}

	public <T> SerializableData add(String name, @NotNull Codec<T> codec) {
		return this.add(name, SerializableDataType.of(codec));
	}

	public <T> SerializableData add(String name, @NotNull Codec<T> codec, T defaultValue) {
		return this.add(name, SerializableDataType.of(codec), defaultValue);
	}

	public <T> SerializableData addSupplied(String name, @NotNull Codec<T> codec, @NotNull Supplier<T> defaultSupplier) {
		return this.addSupplied(name, SerializableDataType.of(codec), defaultSupplier);
	}

	public <T> SerializableData addFunctionedDefault(String name, @NotNull Codec<T> codec, @NotNull Function<Instance, T> defaultFunction) {
		return this.addFunctionedDefault(name, SerializableDataType.of(codec), defaultFunction);
	}

	public <T> SerializableData add(String name, @NotNull SerializableDataType<T> dataType) {
		return this.addField(name, dataType.field(name));
	}

	public <T> SerializableData add(String name, @NotNull SerializableDataType<T> dataType, T defaultValue) {
		return this.addField(name, dataType.field(name, Suppliers.memoize(() -> {
			return defaultValue;
		})));
	}

	public <T> SerializableData addSupplied(String name, @NotNull SerializableDataType<T> dataType, @NotNull Supplier<T> defaultSupplier) {
		return this.addField(name, dataType.field(name, defaultSupplier));
	}

	public <T> SerializableData addFunctionedDefault(String name, @NotNull SerializableDataType<T> dataType, @NotNull Function<Instance, T> defaultFunction) {
		return this.addField(name, dataType.functionedField(name, defaultFunction));
	}

	protected <T> SerializableData addField(String name, Field<T> field) {
		if (name != null && !name.isEmpty()) {
			this.fields.put(name, field);
			return this;
		} else {
			throw new IllegalArgumentException("Field name cannot be empty!");
		}
	}

	public boolean isRoot() {
		return this.root;
	}

	public SerializableData setRoot(boolean root) {
		return new SerializableData(this.fields, this.validator, root);
	}

	public SerializableData copy() {
		return new SerializableData(this.fields, this.validator, this.root);
	}

	public ImmutableMap<String, Field<?>> getFields() {
		return ImmutableMap.copyOf(this.fields);
	}

	public ImmutableSet<String> getFieldNames() {
		return ImmutableSet.copyOf(this.fields.keySet());
	}

	public Field<?> getField(String fieldName) {
		if (!this.containsField(fieldName)) {
			String var10002 = String.valueOf(this);
			throw new IllegalArgumentException(var10002 + " contains no field with name \"" + fieldName + "\".");
		} else {
			return (Field) this.fields.get(fieldName);
		}
	}

	public boolean containsField(String fieldName) {
		return this.fields.containsKey(fieldName);
	}

	public Instance instance() {
		return new Instance();
	}

	public interface Field<E> {

		String path();

		SerializableDataType<E> dataType();

		default <I> DataResult<E> read(DynamicOps<I> ops, I input) {
			return dataType().read(ops, input);
		}

		default <I> DataResult<I> write(DynamicOps<I> ops, E input) {
			return dataType().write(ops, input);
		}

		E getDefault(Instance data);

		boolean hasDefault();

		@SuppressWarnings("unchecked")
		default Class<E> getGenericClass() {
			Type type = this.getClass().getGenericInterfaces()[0];
			if (type instanceof ParameterizedType) {
				Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
				if (actualType instanceof Class) {
					return (Class<E>) actualType;
				}
			}
			throw new IllegalStateException("Cannot determine the class of E for Calio Field!");
		}

	}

	public record FieldImpl<E>(String path, SerializableDataType<E> dataType) implements Field<E> {

		@Override
		public E getDefault(Instance data) {
			throw new IllegalStateException("Tried getting default value of field \"" + path + "\", which doesn't and cannot have any!");
		}

		@Override
		public boolean hasDefault() {
			return false;
		}

	}

	public record FunctionedFieldImpl<E>(String path, SerializableDataType<E> dataType,
										 Function<Instance, E> defaultFunction) implements Field<E> {

		@Override
		public E getDefault(Instance data) {
			return defaultFunction().apply(data);
		}

		@Override
		public boolean hasDefault() {
			return true;
		}

	}

	public record OptionalFieldImpl<E>(String path, SerializableDataType<E> dataType,
									   Supplier<E> defaultSupplier) implements Field<E> {

		@Override
		public E getDefault(Instance data) {
			return defaultSupplier().get();
		}

		@Override
		public boolean hasDefault() {
			return true;
		}

	}

	public record DelegateFieldImpl<E>(Supplier<Field<E>> delegate) implements Field<E> {

		@Override
		public String path() {
			return delegate().get().path();
		}

		@Override
		public SerializableDataType<E> dataType() {
			return delegate().get().dataType();
		}

		@Override
		public E getDefault(Instance data) {
			return delegate().get().getDefault(data);
		}

		@Override
		public boolean hasDefault() {
			return delegate().get().hasDefault();
		}

	}

	public class Instance implements Validatable {
		private final Map<String, Object> map = new HashMap<>();

		public Instance() {
			SerializableData.this.getFieldNames().forEach(name -> map.putIfAbsent(name, null));
		}

		@Override
		public void validate() throws Exception {

			getFields().forEach((name, field) -> {

				if (!map.containsKey(name)) {
					return;
				}

				try {

					switch (map.get(name)) {
						case List<?> list -> {

							int index = 0;
							for (Object element : list) {

								try {

									if (element instanceof Validatable validatable) {
										validatable.validate();
									}

									index++;

								} catch (DataException de) {
									throw de.prependArray(index);
								} catch (Exception e) {
									throw new DataException(Phase.READING, index, e);
								}

							}

						}
						case Validatable validatable -> validatable.validate();
						case null, default -> {

						}
					}

				} catch (DataException de) {
					throw de.prepend(field.path());
				} catch (Exception e) {
					throw new DataException(Phase.READING, field.path(), e);
				}

			});

		}

		public SerializableData serializableData() {
			return SerializableData.this;
		}

		public boolean isPresent(String name) {

			if (fields.containsKey(name)) {

				Field<?> field = fields.get(name);

				if (field.hasDefault() && field.getDefault(this) == null) {
					return get(name) != null;
				}

			}

			return map.get(name) != null;

		}

		public <T> void ifPresent(String name, Consumer<T> consumer) {

			if (isPresent(name)) {
				consumer.accept(get(name));
			}

		}

		public Instance set(String name, Object value) {
			this.map.put(name, value);
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T> T get(String name) {

			if (!map.containsKey(name)) {
				throw new RuntimeException("Tried to get field \"" + name + "\" from data " + this + ", which did not exist.");
			} else {
				return (T) map.get(name);
			}

		}

		public <T> T getOrElse(String name, T defaultValue) {
			return getOrElseGet(name, Suppliers.memoize(() -> defaultValue));
		}

		public <T> T getOrElseGet(String name, Supplier<T> defaultSupplier) {

			if (this.isPresent(name)) {
				return this.get(name);
			} else {
				return defaultSupplier.get();
			}

		}

		public boolean isEmpty() {
			return fields.isEmpty();
		}

		public int getInt(String name) {
			return get(name);
		}

		public boolean getBoolean(String name) {
			return get(name);
		}

		public float getFloat(String name) {
			return get(name);
		}

		public double getDouble(String name) {
			return get(name);
		}

		public String getString(String name) {
			return get(name);
		}

		public ResourceLocation getId(String name) {
			return get(name);
		}

		public AttributeModifier getModifier(String name) {
			return get(name);
		}

		public <T> DataResult<T> getSafely(Class<T> dataClass, String name) {

			try {
				return DataResult.success(dataClass.cast(this.get(name)));
			} catch (Exception e) {
				return DataResult.error(e::getMessage);
			}

		}

		@Override
		public String toString() {
			return "SerializableData$Instance[data = " + map + "]";
		}
	}
}
