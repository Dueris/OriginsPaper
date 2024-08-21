package io.github.dueris.originspaper.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public class JsonObjectBuilder {
	private final JsonObject jsonObject;

	public JsonObjectBuilder() {
		this.jsonObject = new JsonObject();
	}

	public JsonObjectBuilder string(String key, String value) {
		jsonObject.addProperty(key, value);
		return this;
	}

	public JsonObjectBuilder number(String key, Number value) {
		jsonObject.addProperty(key, value);
		return this;
	}

	public JsonObjectBuilder bool(String key, Boolean value) {
		jsonObject.addProperty(key, value);
		return this;
	}

	public JsonObjectBuilder nullValue(String key) {
		jsonObject.add(key, JsonNull.INSTANCE);
		return this;
	}

	public JsonObjectBuilder stringArray(String key, String... values) {
		JsonArray jsonArray = new JsonArray();
		for (String value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder numberArray(String key, Number... values) {
		JsonArray jsonArray = new JsonArray();
		for (Number value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder boolArray(String key, Boolean... values) {
		JsonArray jsonArray = new JsonArray();
		for (Boolean value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder jsonObject(String key, JsonObject value) {
		jsonObject.add(key, value);
		return this;
	}

	public JsonObjectBuilder jsonObject(String key, JsonObjectBuilder builder) {
		jsonObject.add(key, builder.build());
		return this;
	}

	public JsonObjectBuilder jsonArray(String key, JsonArray value) {
		jsonObject.add(key, value);
		return this;
	}

	public JsonObjectBuilder objectArray(String key, JsonObject... values) {
		JsonArray jsonArray = new JsonArray();
		for (JsonObject value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder stringList(String key, List<String> values) {
		JsonArray jsonArray = new JsonArray();
		for (String value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder numberList(String key, List<Number> values) {
		JsonArray jsonArray = new JsonArray();
		for (Number value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder boolList(String key, List<Boolean> values) {
		JsonArray jsonArray = new JsonArray();
		for (Boolean value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder objectList(String key, List<JsonObject> values) {
		JsonArray jsonArray = new JsonArray();
		for (JsonObject value : values) {
			jsonArray.add(value);
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder builderList(String key, List<JsonObjectBuilder> builders) {
		JsonArray jsonArray = new JsonArray();
		for (JsonObjectBuilder builder : builders) {
			jsonArray.add(builder.build());
		}
		jsonObject.add(key, jsonArray);
		return this;
	}

	public JsonObjectBuilder raw(String key, com.google.gson.JsonElement value) {
		jsonObject.add(key, value);
		return this;
	}

	public JsonObject build() {
		return jsonObject;
	}

	public static class JsonArrayBuilder {
		private final JsonArray jsonArray;

		public JsonArrayBuilder() {
			this.jsonArray = new JsonArray();
		}

		public JsonArrayBuilder string(String value) {
			jsonArray.add(value);
			return this;
		}

		public JsonArrayBuilder number(Number value) {
			jsonArray.add(new com.google.gson.JsonPrimitive(value));
			return this;
		}

		public JsonArrayBuilder bool(Boolean value) {
			jsonArray.add(value);
			return this;
		}

		public JsonArrayBuilder jsonObject(JsonObjectBuilder builder) {
			jsonArray.add(builder.build());
			return this;
		}

		public JsonArrayBuilder jsonObject(JsonObject value) {
			jsonArray.add(value);
			return this;
		}

		public JsonArrayBuilder jsonArray(JsonArray value) {
			jsonArray.add(value);
			return this;
		}

		public JsonArray build() {
			return jsonArray;
		}
	}
}