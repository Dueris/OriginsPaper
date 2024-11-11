package io.github.dueris.originspaper.util.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class Modifier implements Comparable<Modifier> {

	private static final String TYPE_KEY = "operation";
	private static final SerializableData DATA = new SerializableData().add(TYPE_KEY, IModifierOperation.DATA_TYPE);
	public static final SerializableDataType<Modifier> DATA_TYPE = SerializableDataType.recursive(dataType -> SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<Pair<Modifier, T>> decode(DynamicOps<T> ops, T input) {
				boolean root = dataType.isRoot();
				return ops.getMap(input)
					.flatMap(mapInput -> DATA.setRoot(root).decode(ops, mapInput)
						.flatMap(modifierData -> {
							IModifierOperation operation = modifierData.get(TYPE_KEY);
							return operation.getSerializableData().setRoot(root).decode(ops, mapInput)
								.map(operationData -> new Modifier(operation, operationData))
								.map(modifier -> Pair.of(modifier, input));
						}));
			}

			@Override
			public <T> DataResult<T> encode(Modifier input, DynamicOps<T> ops, T prefix) {

				RecordBuilder<T> mapBuilder = ops.mapBuilder();
				IModifierOperation operation = input.getOperation();

				mapBuilder.add(TYPE_KEY, IModifierOperation.DATA_TYPE.write(ops, operation));
				operation.getSerializableData().setRoot(dataType.isRoot()).encode(input.getData(), ops, mapBuilder);

				return mapBuilder.build(prefix);

			}

		}
	));
	public static final SerializableDataType<List<Modifier>> LIST_TYPE = DATA_TYPE.list(1, Integer.MAX_VALUE);
	private final IModifierOperation operation;
	private final SerializableData.Instance data;

	public Modifier(IModifierOperation operation, SerializableData.Instance data) { // OriginsPaper - protected -> public
		this.operation = operation;
		this.data = data;
	}

	public static Modifier of(IModifierOperation operation, Consumer<SerializableData.Instance> processor) {

		SerializableData.Instance data = operation.getSerializableData().instance();
		processor.accept(data);

		return new Modifier(operation, data);

	}

	/**
	 * Constructs a {@link Modifier} that specifically use a {@link ModifierOperation}. For defining values for fields of custom implementations of {@link IModifierOperation},
	 * <b>use {@link #of(IModifierOperation, Consumer) Modifier#of(IModifierOperation, Consumer&lt;SerializableData.Instance&gt;)} instead.</b>
	 */
	public static Modifier of(ModifierOperation operation, double amount) {
		return of(operation, data -> data.set("amount", amount));
	}

	public IModifierOperation getOperation() {
		return operation;
	}

	public SerializableData.Instance getData() {
		return data;
	}

	public double apply(Entity entity, double value) {
		return operation.apply(entity, List.of(data), value, value);
	}

	@Override
	public int compareTo(@NotNull Modifier that) {

		IModifierOperation thisOp = this.getOperation();
		IModifierOperation thatOp = that.getOperation();

		if (thisOp.equals(thatOp)) {
			return 0;
		} else if (thisOp.getPhase() == thatOp.getPhase()) {
			return Integer.compare(thisOp.getOrder(), thatOp.getOrder());
		} else {
			return thisOp.getPhase().compareTo(thatOp.getPhase());
		}

	}

}
