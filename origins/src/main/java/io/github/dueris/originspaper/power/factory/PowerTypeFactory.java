package io.github.dueris.originspaper.power.factory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.parser.RootResult;
import io.github.dueris.calio.util.Util;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class PowerTypeFactory implements Factory {

	@SuppressWarnings("unchecked")
	public static SerializableDataType<RootResult<PowerType>> DATA = SerializableDataType.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo)) {
				throw new JsonSyntaxException("Expected JsonObject for root 'PowerType'");
			}

			ResourceLocation type = jo.has("type") ? SerializableDataTypes.IDENTIFIER.deserialize(jo.get("type")) : PowerType.getFactory().getSerializerId();
			Class<? extends PowerType> powerClass = PowerType.type2Class.get(type);

			if (powerClass == null) {
				throw new IllegalArgumentException("Provided type field, '{}', in Power was not found in the registry!".replace("{}", type.toString()));
			}

			try {
				Method factoryMethod = powerClass.getDeclaredMethod("getFactory");
				factoryMethod.setAccessible(true);

				PowerTypeFactory data = (PowerTypeFactory) factoryMethod.invoke(null);
				SerializableData.Instance compound = SerializableDataType.strictCompound(data.serializableData, jo, powerClass);
				if (compound != null) {
					Constructor<PowerType> parsedConstructor = (Constructor<PowerType>) Util.generateConstructor(powerClass, data.serializableData);
					return new RootResult<>(parsedConstructor, compound, PowerType::onBootstrap);
				}
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}

			return null;
		}, PowerType.class, false
	);

	protected final ResourceLocation id;
	protected SerializableData serializableData;

	public PowerTypeFactory(ResourceLocation id, SerializableData serializableData) {
		this.id = id;
		this.serializableData = new SerializableData(serializableData);
	}

	@Override
	public ResourceLocation getSerializerId() {
		return id;
	}

	@Override
	public SerializableData getSerializableData() {
		return serializableData;
	}

}

