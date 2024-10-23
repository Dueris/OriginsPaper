package io.github.dueris.calio.data;

import com.mojang.serialization.DataResult;
import io.github.dueris.calio.util.Util;

import java.util.*;

public class ClassDataRegistry<T> {
	private static final HashMap<Class<?>, ClassDataRegistry<?>> REGISTRIES = new HashMap();
	private final Class<T> clazz;
	private final HashMap<String, Class<? extends T>> directMappings = new HashMap();
	private final List<String> packages = new LinkedList();
	private final String classSuffix;
	private SerializableDataType<Class<? extends T>> dataType;
	private SerializableDataType<List<Class<? extends T>>> listDataType;

	protected ClassDataRegistry(Class<T> cls, String classSuffix) {
		this.clazz = cls;
		this.classSuffix = classSuffix;
	}

	public static Optional<ClassDataRegistry<?>> get(Class<?> cls) {
		return REGISTRIES.containsKey(cls) ? Optional.of((ClassDataRegistry) REGISTRIES.get(cls)) : Optional.empty();
	}

	public static <T> ClassDataRegistry<T> getOrCreate(Class<T> cls, String classSuffix) {
		if (REGISTRIES.containsKey(cls)) {
			return (ClassDataRegistry) REGISTRIES.get(cls);
		} else {
			ClassDataRegistry<T> cdr = new ClassDataRegistry(cls, classSuffix);
			REGISTRIES.put(cls, cdr);
			return cdr;
		}
	}

	private static String transformJsonToClass(String jsonName, String classSuffix) {
		StringBuilder builder = new StringBuilder();
		boolean caps = true;
		int capsOffset = 'A' - 'a';
		for (char c : jsonName.toCharArray()) {
			if (c == '_') {
				caps = true;
				continue;
			}
			if (caps) {
				builder.append(Character.toUpperCase(c));
				caps = false;
			} else {
				builder.append(c);
			}
		}
		builder.append(classSuffix);
		return builder.toString();
	}

	public void addMapping(String className, Class<?> cls) {
		this.directMappings.put(className, Util.castClass(cls));
	}

	public void addPackage(String packagePath) {
		this.packages.add(packagePath);
	}

	public SerializableDataType<Class<? extends T>> getDataType() {
		if (this.dataType == null) {
			this.dataType = this.createDataType();
		}

		return this.dataType;
	}

	public SerializableDataType<List<Class<? extends T>>> getListDataType() {
		if (this.listDataType == null) {
			this.listDataType = SerializableDataType.list(this.getDataType());
		}

		return this.listDataType;
	}

	public Optional<Class<? extends T>> mapStringToClass(String str) {
		return this.mapStringToClass(str, new StringBuilder());
	}

	public Optional<Class<? extends T>> mapStringToClass(String str, StringBuilder failedClasses) {
		if (this.directMappings.containsKey(str)) {
			return Optional.of(this.directMappings.get(str));
		} else {
			try {
				return Optional.of((Class<? extends T>) Class.forName(str));
			} catch (Exception var9) {
				failedClasses.append(str);
				Iterator var3 = this.packages.iterator();

				while (var3.hasNext()) {
					String pkg = (String) var3.next();
					String full = pkg + "." + str;

					try {
						return Optional.of((Class<? extends T>) Class.forName(full));
					} catch (Exception var8) {
						failedClasses.append(", ");
						failedClasses.append(full);
						full = pkg + "." + transformJsonToClass(str, this.classSuffix);

						try {
							return Optional.of((Class<? extends T>) Class.forName(full));
						} catch (Exception var7) {
							failedClasses.append(", ");
							failedClasses.append(full);
						}
					}
				}

				return Optional.empty();
			}
		}
	}

	private SerializableDataType<Class<? extends T>> createDataType() {
		return SerializableDataTypes.STRING.comapFlatMap(
			str -> {

				StringBuilder failedClasses = new StringBuilder();
				Optional<Class<? extends T>> mappedClass = mapStringToClass(str, failedClasses);

				return mappedClass.isPresent()
					? DataResult.success(mappedClass.get())
					: DataResult.error(() -> "Specified class does not exist: \"" + str + "\". Searched at [" + failedClasses + "]");

			},
			Class::getName
		);
	}
}
