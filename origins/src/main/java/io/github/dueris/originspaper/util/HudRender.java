package io.github.dueris.originspaper.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.calio.registry.DataObjectFactory;
import io.github.dueris.calio.registry.SimpleDataObjectFactory;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.util.hud_render.ParentHudRender;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HudRender implements Comparable<HudRender>, Validatable {

	public static final ResourceLocation DEFAULT_SPRITE = OriginsPaper.apoliIdentifier("textures/gui/resource_bar.png");
	public static final HudRender DONT_RENDER = new HudRender(null, DEFAULT_SPRITE, false, false, 0, 0, 0);

	public static final DataObjectFactory<HudRender> FACTORY = new SimpleDataObjectFactory<>(
		new SerializableData()
			.add("condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("sprite_location", SerializableDataTypes.IDENTIFIER, DEFAULT_SPRITE)
			.add("should_render", SerializableDataTypes.BOOLEAN, true)
			.add("inverted", SerializableDataTypes.BOOLEAN, false)
			.add("bar_index", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("icon_index", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("order", SerializableDataTypes.INT, 0),
		data -> new HudRender(
			data.get("condition"),
			data.getId("sprite_location"),
			data.getBoolean("should_render"),
			data.getBoolean("inverted"),
			data.getInt("bar_index"),
			data.getInt("icon_index"),
			data.getInt("order")
		),
		(hudRender, serializableData) -> serializableData.instance()
			.set("condition", hudRender.getCondition())
			.set("sprite_location", hudRender.getSpriteLocation())
			.set("should_render", hudRender.shouldRender())
			.set("inverted", hudRender.isInverted())
			.set("bar_index", hudRender.getBarIndex())
			.set("icon_index", hudRender.getIconIndex())
			.set("order", hudRender.getOrder())
	);

	public static final CompoundSerializableDataType<HudRender> STRICT_DATA_TYPE = SerializableDataType.compound(FACTORY);
	public static final SerializableDataType<List<HudRender>> LIST_DATA_TYPE = STRICT_DATA_TYPE.list(1, Integer.MAX_VALUE);
	public static final SerializableDataType<HudRender> DATA_TYPE = SerializableDataType.recursive(self -> {

		SerializableDataType<List<HudRender>> listDataType = LIST_DATA_TYPE.setRoot(self.isRoot());
		SerializableDataType<HudRender> singleDataType = STRICT_DATA_TYPE.setRoot(self.isRoot());

		return SerializableDataType.of(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<HudRender, T>> decode(DynamicOps<T> ops, T input) {
					return listDataType.codec().decode(ops, input)
						.map(hudRendersAndInput -> hudRendersAndInput
							.mapFirst(ObjectArrayList::new)
							.mapFirst(hudRenders -> new ParentHudRender(hudRenders.removeFirst(), hudRenders)));
				}

				@Override
				public <T> DataResult<T> encode(HudRender input, DynamicOps<T> ops, T prefix) {

					if (input instanceof ParentHudRender parent) {
						return listDataType.codec().encode(parent.getChildren(), ops, prefix);
					} else {
						return singleDataType.codec().encode(input, ops, prefix);
					}

				}

			}
		);

	});

	private final Optional<EntityCondition> condition;
	private final ResourceLocation spriteLocation;

	private final boolean shouldRender;
	private final boolean inverted;

	private final int barIndex;
	private final int iconIndex;
	private final int order;

	public HudRender(Optional<EntityCondition> condition, ResourceLocation spriteLocation, boolean shouldRender, boolean inverted, int barIndex, int iconIndex, int order) {
		this.condition = condition;
		this.spriteLocation = spriteLocation;
		this.shouldRender = shouldRender;
		this.inverted = inverted;
		this.barIndex = barIndex;
		this.iconIndex = iconIndex;
		this.order = order;
	}

	@Override
	public int compareTo(@NotNull HudRender other) {
		int orderResult = Integer.compare(this.getOrder(), other.getOrder());
		return orderResult != 0
			? orderResult
			: this.getSpriteLocation().compareTo(other.getSpriteLocation());
	}

	@Override
	public void validate() throws Exception {
		FACTORY.toData(this).validate();
	}

	@Nullable
	public Optional<EntityCondition> getCondition() {
		return this.condition;
	}

	public ResourceLocation getSpriteLocation() {
		return spriteLocation;
	}

	public boolean shouldRender() {
		return shouldRender;
	}

	public boolean shouldRender(Entity viewer) {
		return this.shouldRender()
			&& getCondition().map(condition -> condition.test(viewer)).orElse(true);
	}

	public boolean isInverted() {
		return inverted;
	}

	public int getBarIndex() {
		return barIndex;
	}

	public int getIconIndex() {
		return iconIndex;
	}

	public int getOrder() {
		return order;
	}

	public HudRender withOrder(int order) {

		if (this.getOrder() != 0) {
			return this;
		}

		return new HudRender(
			this.getCondition(),
			this.getSpriteLocation(),
			this.shouldRender(),
			this.isInverted(),
			this.getBarIndex(),
			this.getIconIndex(),
			order
		);

	}

	public Optional<HudRender> getActive(Entity viewer) {

		if (this.shouldRender(viewer)) {
			return Optional.of(this);
		} else {
			return Optional.empty();
		}

	}

}
