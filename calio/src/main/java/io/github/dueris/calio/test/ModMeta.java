package io.github.dueris.calio.test;

import com.google.gson.JsonObject;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.annotations.SourceProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ModMeta {
	public static ConcurrentLinkedQueue<Class<? extends ModMeta>> INSTANCE_TYPES = new ConcurrentLinkedQueue<>(List.of(TypedTest.class));
	private final String modid;
	private final String name;
	private final int priority;
	private final Set<String> testArray;
	private final boolean testBoolean;
	private final JsonObject jsonObject;
	@SourceProvider
	private JsonObject sourceProvider;

	public ModMeta(String modid, String name, int priority, Set<String> testArray, boolean testBoolean, JsonObject jsonObject) {
		this.modid = modid;
		this.name = name;
		this.priority = priority;
		this.testArray = testArray;
		this.testBoolean = testBoolean;
		this.jsonObject = jsonObject;
	}

	public static InstanceDefiner buildDefiner() {
		return InstanceDefiner.instanceDefiner()
			.required("modid", SerializableDataTypes.STRING)
			.add("name", SerializableDataTypes.STRING, "hi")
			.add("priority", SerializableDataTypes.INT)
			.add("test_array", SerializableDataTypes.set(SerializableDataTypes.STRING))
			.add("test_boolean", SerializableDataTypes.BOOLEAN)
			.add("json_object", SerializableDataTypes.JSON_OBJECT, new JsonObject());
	}

	@Override
	public String toString() {
		return "ModMeta{" +
			"modid='" + modid + '\'' +
			", name='" + name + '\'' +
			", priority=" + priority +
			", testArray=" + testArray +
			", testBoolean=" + testBoolean +
			", jsonObject=" + jsonObject +
			'}';
	}
}
