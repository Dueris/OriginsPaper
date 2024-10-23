package io.github.dueris.originspaper.power;

import com.mojang.serialization.*;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class Power implements Validatable {

	public static final String TYPE_KEY = "type";

	public static final SerializableData DATA = new SerializableData()
		.add("id", SerializableDataTypes.IDENTIFIER)
		.add(TYPE_KEY, ApoliDataTypes.POWER_TYPE_FACTORY)
		.addFunctionedDefault("name", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, data -> createTranslatable(data.getId("id"), "name"))
		.addFunctionedDefault("description", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, data -> createTranslatable(data.getId("id"), "description"))
		.add("hidden", SerializableDataTypes.BOOLEAN, false);

	public static final Codec<Power> CODEC = Codec.recursive("Power", powerCodec -> new MapCodec<Power>() {

		@Override
		public <T> DataResult<Power> decode(DynamicOps<T> ops, MapLike<T> input) {

			DataResult<SerializableData.Instance> powerDataResult = DATA.decode(ops, input);
			DataResult<PowerTypeFactory<?>> factoryResult = powerDataResult.map(powerData -> powerData.get(TYPE_KEY));

			return powerDataResult
				.flatMap(powerData -> factoryResult
					.flatMap(factory -> factory.getSerializableData().decode(ops, input)
						.map(factory::fromData)
						.map(instance -> new Power(instance, powerData))));

		}

		@Override
		public <T> RecordBuilder<T> encode(Power input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

			PowerTypeFactory<?>.Instance instance = input.getFactoryInstance();

			DATA.getFields().forEach((name, field) -> {

				prefix.add(name, field.write(ops, input.data.get(name)));

				if (name.equals(TYPE_KEY)) {

					if (input instanceof MultiplePower multiplePower) {
						multiplePower
							.getSubPowers()
							.forEach(subPower -> prefix.add(subPower.getSubName(), powerCodec.encodeStart(ops, subPower)));
					}

					instance.getSerializableData().encode(instance.getData(), ops, prefix);

				}

			});

			return prefix;

		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return DATA.keys(ops);
		}

	}.codec());

	protected final SerializableData.Instance data;

	private final PowerTypeFactory<? extends PowerType>.Instance factoryInstance;
	private final ResourceLocation id;

	private final Component name;
	private final Component description;

	private final boolean hidden;

	protected Power(PowerTypeFactory<? extends PowerType>.Instance factoryInstance, SerializableData.@NotNull Instance data) {
		this.factoryInstance = factoryInstance;
		this.data = data;
		this.id = data.getId("id");
		this.name = data.get("name");
		this.description = data.get("description");
		this.hidden = data.getBoolean("hidden");
	}

	private static @NotNull Component createTranslatable(ResourceLocation id, String type) {
		String translationKey = Util.makeDescriptionId("power", id) + "." + type;
		return Component.translatable(translationKey);
	}

	@Override
	public void validate() throws Exception {
		this.getFactoryInstance().validate();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
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

	@Override
	public String toString() {
		return "Power{data=" + data + ", factoryInstance=" + factoryInstance + '}';
	}

	public ResourceLocation getId() {
		return id;
	}

	public PowerTypeFactory<? extends PowerType>.Instance getFactoryInstance() {
		return factoryInstance;
	}

	public PowerType create(@Nullable LivingEntity entity) {
		PowerType type = this.getFactoryInstance().apply(this, entity);
		if (type instanceof Listener listener) {
			OriginsPaper.getPlugin().getServer().getPluginManager().registerEvents(listener, OriginsPaper.getPlugin());
		}
		return type;
	}

	public boolean isHidden() {
		return this.isSubPower()
			|| this.hidden;
	}

	public final boolean isMultiple() {
		return this.getFactoryInstance().getFactory() == PowerTypes.MULTIPLE
			|| this instanceof MultiplePower;
	}

	public final boolean isSubPower() {
		return this instanceof SubPower;
	}

	public boolean isActive(Entity entity) {
		PowerType powerType = this.getType(entity);
		return powerType != null
			&& powerType.isActive();
	}

	@Nullable
	public PowerType getType(Entity entity) {

		if (entity != null && PowerHolderComponent.KEY.isProvidedBy(entity)) {
			return PowerHolderComponent.KEY.get(entity).getPowerType(this);
		} else {
			return null;
		}

	}

	public MutableComponent getName() {
		return name.copy();
	}

	public MutableComponent getDescription() {
		return description.copy();
	}

	public record Entry(PowerTypeFactory<?> typeFactory, PowerReference power, @Nullable Tag nbtData,
						List<ResourceLocation> sources) {

		private static final SerializableDataType<List<ResourceLocation>> MUTABLE_IDENTIFIERS = SerializableDataTypes.IDENTIFIER.list(1, Integer.MAX_VALUE).xmap(LinkedList::new, Function.identity());

		public static final SerializableDataType<Entry> CODEC = SerializableDataType.compound(
			new SerializableData()
				.add("Factory", ApoliDataTypes.POWER_TYPE_FACTORY, null)
				.addFunctionedDefault("type", ApoliDataTypes.POWER_TYPE_FACTORY, data -> data.get("Factory"))
				.add("Type", ApoliDataTypes.POWER_REFERENCE, null)
				.addFunctionedDefault("id", ApoliDataTypes.POWER_REFERENCE, data -> data.get("Type"))
				.add("Data", SerializableDataTypes.NBT_ELEMENT, null)
				.addFunctionedDefault("data", SerializableDataTypes.NBT_ELEMENT, data -> data.get("Data"))
				.add("Sources", MUTABLE_IDENTIFIERS, null)
				.addFunctionedDefault("sources", MUTABLE_IDENTIFIERS, data -> data.get("Sources"))
				.validate(data -> {

					if (!data.isPresent("type")) {
						return CraftCalio.createMissingRequiredFieldError("type");
					} else if (!data.isPresent("id")) {
						return CraftCalio.createMissingRequiredFieldError("id");
					} else if (!data.isPresent("sources")) {
						return CraftCalio.createMissingRequiredFieldError("sources");
					} else {
						return DataResult.success(data);
					}

				}),
			data -> new Entry(
				data.get("type"),
				data.get("id"),
				data.get("data"),
				data.get("sources")
			),
			(entry, serializableData) -> serializableData.instance()
				.set("type", entry.typeFactory())
				.set("id", entry.power())
				.set("data", entry.nbtData())
				.set("sources", entry.sources())
		);

	}

}
