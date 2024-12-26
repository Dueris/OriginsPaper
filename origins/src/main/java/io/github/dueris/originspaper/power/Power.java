package io.github.dueris.originspaper.power;

import com.mojang.serialization.*;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.power.type.meta.MultiplePowerType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Power implements Validatable {

	public static final SerializableData SERIALIZABLE_DATA = new SerializableData()
		.add("id", SerializableDataTypes.IDENTIFIER)
		.add("type", PowerTypes.DATA_TYPE)
		.add("name", SerializableDataTypes.TEXT.optional(), Optional.empty())
		.add("description", SerializableDataTypes.TEXT.optional(), Optional.empty())
		.add("hidden", SerializableDataTypes.BOOLEAN, false);

	@SuppressWarnings("unchecked")
	public static final SerializableDataType<Power> DATA_TYPE = SerializableDataType.lazy(() -> new CompoundSerializableDataType<>(
		SERIALIZABLE_DATA,
		serializableData -> {
			boolean root = serializableData.isRoot();
			return MapCodec.recursive("Power", self -> new MapCodec<>() {

				@Override
				public <T> Stream<T> keys(DynamicOps<T> ops) {
					return serializableData.keys(ops);
				}

				@Override
				public <T> DataResult<Power> decode(DynamicOps<T> ops, MapLike<T> input) {

					DataResult<SerializableData.Instance> powerDataResult = serializableData.decode(ops, input);
					DataResult<PowerType> powerTypeResult = powerDataResult
						.map(powerData -> (PowerConfiguration<PowerType>) powerData.get("type"))
						.flatMap(config -> config.mapCodec(root).decode(ops, input));

					return powerDataResult
						.flatMap(powerData -> powerTypeResult
							.map(powerType -> new Power(powerType, powerData)));

				}

				@Override
				public <T> RecordBuilder<T> encode(Power input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

					PowerType powerType = input.getPowerType();
					PowerConfiguration<PowerType> config = (PowerConfiguration<PowerType>) powerType.getConfig();

					prefix.add("type", PowerTypes.DATA_TYPE.write(ops, config));

					if (input instanceof MultiplePower multiplePower) {
						multiplePower
							.getSubPowers()
							.forEach(subPower -> prefix.add(subPower.getSubName(), self.encodeStart(ops, subPower)));
					}

					config.mapCodec(root).encode(powerType, ops, prefix);

					prefix.add("name", SerializableDataTypes.TEXT.write(ops, input.getName()));
					prefix.add("description", SerializableDataTypes.TEXT.write(ops, input.getDescription()));
					prefix.add("hidden", ops.createBoolean(input.isHidden()));

					return prefix;

				}

			});
		}
	));


	private final ResourceLocation id;
	private final PowerType powerType;

	private final Component name;
	private final Component description;

	private final boolean hidden;

	protected Power(ResourceLocation id, PowerType powerType, Optional<Component> name, Optional<Component> description, boolean hidden) {

		this.id = id;
		this.powerType = powerType;

		this.name = name.orElse(createTranslatable(id, "name"));
		this.description = description.orElse(createTranslatable(id, "description"));

		this.hidden = hidden;
		this.powerType.setPower(this);

	}

	protected Power(PowerType powerType, SerializableData.Instance data) {
		this(data.get("id"), powerType, data.get("name"), data.get("description"), data.get("hidden"));
	}

	protected Power(Power basePower) {
		this(basePower.getId(), basePower.getPowerType(), Optional.of(basePower.getName()), Optional.of(basePower.getDescription()), basePower.isHidden());
	}

	private static @NotNull Component createTranslatable(ResourceLocation id, String type) {
		String translationKey = Util.makeDescriptionId("power", id) + "." + type;
		return Component.translatable(translationKey);
	}

	@Override
	public void validate() throws Exception {
		getPowerType().validate();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		} else if (obj instanceof Power that) {
			return Objects.equals(this.getId(), that.getId());
		} else {
			return false;
		}

	}

	public ResourceLocation getId() {
		return id;
	}

	@NotNull
	public PowerType getPowerType() {
		return powerType;
	}

	public PowerReference asReference() {
		return PowerReference.of(this.getId());
	}

	public boolean isHidden() {
		return this.isSubPower()
			|| this.hidden;
	}

	public boolean isMultiple() {
		return this.getPowerType() instanceof MultiplePowerType
			|| this instanceof MultiplePower;
	}

	public boolean isSubPower() {
		return this instanceof SubPower;
	}

	public boolean isActive(Entity entity) {
		return PowerHolderComponent.getOptional(entity)
			.map(powerComponent -> powerComponent.getPowerType(this))
			.filter(PowerType::isInitialized)
			.map(PowerType::isActive)
			.orElse(false);
	}

	public MutableComponent getName() {
		return name.copy();
	}

	public MutableComponent getDescription() {
		return description.copy();
	}

	public record DataEntry(PowerConfiguration<?> typeConfig, PowerReference powerReference, Tag nbtData,
							Set<ResourceLocation> sources) {

		private static final SerializableDataType<Set<ResourceLocation>> MUTABLE_IDENTIFIERS = SerializableDataTypes.IDENTIFIER.list(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

		public static final SerializableDataType<DataEntry> CODEC = SerializableDataType.compound(
			new SerializableData()
				.add("Factory", PowerTypes.DATA_TYPE, null)
				.addFunctionedDefault("type", PowerTypes.DATA_TYPE, data -> data.get("Factory"))
				.add("Type", ApoliDataTypes.POWER_REFERENCE, null)
				.addFunctionedDefault("id", ApoliDataTypes.POWER_REFERENCE, data -> data.get("Type"))
				.add("Data", SerializableDataTypes.NBT_ELEMENT, new CompoundTag())
				.addFunctionedDefault("data", SerializableDataTypes.NBT_ELEMENT, data -> data.get("Data"))
				.add("Sources", MUTABLE_IDENTIFIERS, null)
				.addFunctionedDefault("sources", MUTABLE_IDENTIFIERS, data -> data.get("Sources"))
				.validate(io.github.dueris.originspaper.util.Util.validateAllFieldsPresent("type", "id", "data", "sources")),
			data -> new DataEntry(
				data.get("type"),
				data.get("id"),
				data.get("data"),
				data.get("sources")
			),
			(dataEntry, serializableData) -> serializableData.instance()
				.set("type", dataEntry.typeConfig())
				.set("id", dataEntry.powerReference())
				.set("data", dataEntry.nbtData())
				.set("sources", dataEntry.sources())
		);

	}

}
