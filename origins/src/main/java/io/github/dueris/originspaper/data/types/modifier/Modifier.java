package io.github.dueris.originspaper.data.types.modifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.exceptions.DataException;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Modifier implements Comparable<Modifier> {

	public static final SerializableDataType<Modifier> DATA_TYPE = SerializableDataType.of(
		(jsonElement) -> {

			if (!(jsonElement instanceof JsonObject jsonObject)) {
				throw new JsonSyntaxException("Expected modifier to be a JSON object.");
			}

			if (!jsonObject.has("operation")) {
				throw new JsonSyntaxException("Modifier requires an \"operation\" field.");
			}

			try {

				IModifierOperation operation = IModifierOperation.DATA_TYPE.deserialize(jsonObject.get("operation"));
				SerializableData.Instance data = SerializableDataType.strictCompound(operation.getData(), jsonObject, Modifier.class);

				return new Modifier(operation, data);

			} catch (Exception e) {
				throw new DataException(DataException.Phase.READING, "operation", e);
			}

		}, Modifier.class
	);
	public static final SerializableDataType<List<Modifier>> LIST_TYPE = SerializableDataTypes.list(DATA_TYPE);
	private final IModifierOperation operation;
	private final SerializableData.Instance dataInstance;

	public Modifier(IModifierOperation operation, SerializableData.Instance dataInstance) {
		this.operation = operation;
		this.dataInstance = dataInstance;
	}

	public static Modifier of(IModifierOperation operation, Consumer<SerializableData.Instance> processor) {

		SerializableData.Instance data = new SerializableData.Instance(new HashMap<>());
		processor.accept(data);

		return new Modifier(operation, data);

	}

	public static Modifier of(ModifierOperation operation, double amount) {
		return of(operation, data -> data.set("amount", amount));
	}

	public IModifierOperation getOperation() {
		return operation;
	}

	public SerializableData.Instance getData() {
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
