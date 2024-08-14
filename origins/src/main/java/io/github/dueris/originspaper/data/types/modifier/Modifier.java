package io.github.dueris.originspaper.data.types.modifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.data.exceptions.DataException;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class Modifier implements Comparable<Modifier> {

	public static final SerializableDataBuilder<Modifier> DATA_TYPE = SerializableDataBuilder.of(
		(jsonElement) -> {

			if (!(jsonElement instanceof JsonObject jsonObject)) {
				throw new JsonSyntaxException("Expected modifier to be a JSON object.");
			}

			if (!jsonObject.has("operation")) {
				throw new JsonSyntaxException("Modifier requires an \"operation\" field.");
			}

			try {

				IModifierOperation operation = IModifierOperation.DATA_TYPE.deserialize(jsonObject.get("operation"));
				DeserializedFactoryJson data = DeserializedFactoryJson.decompileJsonObject(jsonObject, operation.getData(), "Modifier", "null", Optional.of(Modifier.class));

				return new Modifier(operation, data);

			} catch (Exception e) {
				throw new DataException(DataException.Phase.READING, "operation", e);
			}

		}, Modifier.class
	);
	public static final SerializableDataBuilder<List<Modifier>> LIST_TYPE = SerializableDataTypes.list(DATA_TYPE);
	private final IModifierOperation operation;
	private final DeserializedFactoryJson dataInstance;

	public Modifier(IModifierOperation operation, DeserializedFactoryJson dataInstance) {
		this.operation = operation;
		this.dataInstance = dataInstance;
	}

	public IModifierOperation getOperation() {
		return operation;
	}

	public DeserializedFactoryJson getData() {
		return dataInstance;
	}

	public double apply(Entity entity, double value) {
		return operation.apply(entity, List.of(dataInstance), value, value);
	}

	@Override
	public int compareTo(@NotNull Modifier o) {
		if (o.operation == operation) {
			return 0;
		} else if (o.operation.getPhase() == operation.getPhase()) {
			return o.operation.getOrder() - operation.getOrder();
		} else {
			return o.operation.getPhase() == IModifierOperation.Phase.BASE ? 1 : -1;
		}
	}

}
