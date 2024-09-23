package io.github.dueris.originspaper.data.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Renderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record HudRender(@Nullable ConditionTypeFactory<Entity> condition, Renderer.RenderImpl render,
						boolean shouldRender, boolean inverted, int order) implements Comparable<HudRender> {

	public static final ResourceLocation DEFAULT_SPRITE = OriginsPaper.apoliIdentifier("textures/gui/resource_bar.png");
	public static final HudRender DONT_RENDER = new HudRender(null, new Renderer.RenderImpl(0, DEFAULT_SPRITE, null, BarColor.WHITE), false, false, 0);

	public static final SerializableDataType<HudRender> DATA_TYPE = SerializableDataType.strictCompound(
		new SerializableData()
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("sprite_location", SerializableDataTypes.IDENTIFIER, DEFAULT_SPRITE)
			.add("should_render", SerializableDataTypes.BOOLEAN, true)
			.add("inverted", SerializableDataTypes.BOOLEAN, false)
			.add("bar_index", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("order", SerializableDataTypes.INT, 0),
		data -> new HudRender(
			data.get("condition"),
			Renderer.findRender(data.getInt("bar_index"), data.getId("sprite_location")),
			data.getBoolean("should_render"),
			data.getBoolean("inverted"),
			data.getInt("order")
		), HudRender.class
	);

	public static final SerializableDataType<List<HudRender>> LIST_DATA_TYPE = SerializableDataType.of(DATA_TYPE.listOf(1, Integer.MAX_VALUE));

	@Override
	public int compareTo(@NotNull HudRender other) {
		int orderResult = Integer.compare(this.order(), other.order());
		return orderResult != 0
			? orderResult
			: this.render.spriteLocation().compareTo(other.render.spriteLocation());
	}


	public boolean shouldRender(Entity viewer) {
		return this.shouldRender() && (this.condition() == null || this.condition().test(viewer));
	}


	public HudRender withOrder(int order) {

		if (this.order() != 0) {
			return this;
		}

		return new HudRender(
			this.condition(),
			this.render,
			this.shouldRender(),
			this.inverted(),
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
