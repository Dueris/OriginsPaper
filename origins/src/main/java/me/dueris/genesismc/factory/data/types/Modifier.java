package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.util.Utils;

import java.util.Map;
import java.util.function.BinaryOperator;

public class Modifier {
	public FactoryJsonObject handle;

	public Modifier(FactoryJsonObject factoryJsonObject) {
		this.handle = factoryJsonObject;
	}

	public Float value() {
		if (handle.isPresent("modifier")) {
			Modifier modifier = new Modifier(handle.getJsonObject("modifier"));
			Map<String, BinaryOperator<Float>> operators = Utils.getOperationMappingsFloat();
			BinaryOperator<Float> operation = operators.get(modifier.operation());
			return operation.apply(this.value(), modifier.value());
		} else {
			return this.handle.getNumber("value").getFloat();
		}
	}

	public String operation() {
		return this.handle.getStringOrDefault("operation", "add");
	}
}
