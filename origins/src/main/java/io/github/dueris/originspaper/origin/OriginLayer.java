package io.github.dueris.originspaper.origin;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.parser.RootResult;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.OriginsDataTypes;
import io.github.dueris.originspaper.data.types.GuiTitle;
import io.github.dueris.originspaper.screen.ScreenNavigator;
import io.github.dueris.originspaper.storage.OriginComponent;
import io.github.dueris.originspaper.util.ComponentUtil;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class OriginLayer {
	public static Map<ResourceLocation, OriginLayer> REGISTRY = new ConcurrentHashMap<>();
	public static SerializableDataType<RootResult<OriginLayer>> DATA = SerializableDataType.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo)) {
				throw new JsonSyntaxException("Expected JsonObject for root 'OriginLayer'");
			}

			try {
				SerializableData.Instance compound = SerializableDataType.strictCompound(getFactory(), jo, OriginLayer.class);
				return new RootResult<>(
					io.github.dueris.calio.util.Util.generateConstructor(OriginLayer.class, getFactory()), compound, (oL) -> {
				}
				);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}, OriginLayer.class
	);
	private final ResourceLocation key;
	private final String tag;
	private final int loadingPriority;
	private final List<ConditionedOrigin> origins;
	private final boolean replace;
	private final List<ResourceLocation> excludeRandom;
	private final boolean replaceExcludeRandom;
	private int order;
	private boolean enabled;
	private @NotNull TextComponent name;
	private GuiTitle guiTitle;
	private boolean allowRandom;
	private boolean allowRandomUnchoosable;
	private ResourceLocation defaultOrigin;
	private boolean autoChoose;
	private boolean hidden;

	public OriginLayer(@NotNull ResourceLocation key, int order, int loadingPriority, List<ConditionedOrigin> origins, boolean replace, boolean enabled, Component name, GuiTitle guiTitle, boolean allowRandom,
					   boolean allowRandomUnchoosable, List<ResourceLocation> excludeRandom, boolean replaceExcludeRandom, ResourceLocation defaultOrigin, boolean autoChoose, boolean hidden) {
		this.key = key;
		this.tag = key.toString();
		this.order = order;
		this.loadingPriority = loadingPriority;
		this.origins = origins;
		this.replace = replace;
		this.enabled = enabled;
		this.name = ComponentUtil.nmsToKyori(
			LangFile.translatable((name != null ? name.getString() : "origin.$namespace.$path.name")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.guiTitle = guiTitle;
		this.allowRandom = allowRandom;
		this.allowRandomUnchoosable = allowRandomUnchoosable;
		this.excludeRandom = excludeRandom;
		this.replaceExcludeRandom = replaceExcludeRandom;
		this.defaultOrigin = defaultOrigin;
		this.autoChoose = autoChoose;
		this.hidden = hidden;
	}

	public static SerializableData getFactory() {
		return SerializableData.serializableData()
			.add("order", SerializableDataTypes.INT, Integer.MAX_VALUE)
			.add("loading_priority", SerializableDataTypes.INT, 0)
			.add("origins", SerializableDataTypes.list(OriginsDataTypes.ORIGIN_OR_CONDITIONED_ORIGIN))
			.add("replace", SerializableDataTypes.BOOLEAN, false)
			.add("enabled", SerializableDataTypes.BOOLEAN, true)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("gui_title", OriginsDataTypes.GUI_TITLE, null)
			.add("allow_random", SerializableDataTypes.BOOLEAN, false)
			.add("allow_random_unchoosable", SerializableDataTypes.BOOLEAN, false)
			.add("exclude_random", SerializableDataTypes.list(SerializableDataTypes.IDENTIFIER), new LinkedList<>())
			.add("replace_exclude_random", SerializableDataTypes.BOOLEAN, false)
			.add("default_origin", SerializableDataTypes.IDENTIFIER, null)
			.add("auto_choose", SerializableDataTypes.BOOLEAN, false)
			.add("hidden", SerializableDataTypes.BOOLEAN, false);
	}

	public int getOrder() {
		return order;
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public List<ConditionedOrigin> getOrigins() {
		return origins;
	}

	public List<Origin> testChoosable(net.minecraft.world.entity.Entity entity) {
		List<Origin> tested = new LinkedList<>();

		for (ConditionedOrigin origin : this.getOrigins()) {
			List<Origin> conditionedOrigins = origin.origins.stream().map(OriginsPaper::getOrigin).toList();
			if (origin.condition != null) {
				if (origin.condition.test(entity)) tested.addAll(conditionedOrigins);
				continue;
			}
			tested.addAll(conditionedOrigins);
		}

		return tested;
	}

	public boolean testDefaultOrigin(net.minecraft.world.entity.player.Player entity) {
		boolean autoChoose = this.isAutoChoose();
		if (defaultOrigin == null) return false;
		if (autoChoose) {
			OriginComponent.setOrigin(entity.getBukkitEntityRaw(), this, OriginsPaper.getOrigin(defaultOrigin));
			return true;
		} else if (ScreenNavigator.orbChoosing.contains(entity)) {
			return false;
		} else {
			if (!defaultOrigin.equals(Origin.EMPTY.getId())) {
				Origin origin = OriginsPaper.getOrigin(defaultOrigin);
				if (origin != null) {
					OriginComponent.setOrigin(entity.getBukkitEntityRaw(), this, origin);
					return true;
				}
			}

			return false;
		}
	}

	public List<ResourceLocation> getOriginIdentifiers() {
		return Util.collapseCollection(this.getOrigins().stream().map(ConditionedOrigin::origins).toList());
	}

	public List<Origin> getRandomOrigins() {
		return !this.isRandomAllowed()
			? new LinkedList<>()
			: getOriginIdentifiers().stream()
			.map(OriginsPaper::getOrigin)
			.filter(origin -> !origin.unchoosable() || this.isAllowRandomUnchoosable())
			.filter(origin -> {
				if (!this.getExcludeRandom().isEmpty()) {
					for (ResourceLocation identifier : this.getExcludeRandom().stream().toList()) {
						if (origin.getId().equals(identifier)) {
							return false;
						}
					}
				}

				return true;
			})
			.toList();
	}

	public boolean isReplace() {
		return replace;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public @NotNull TextComponent getName() {
		return name;
	}

	public GuiTitle getGuiTitle() {
		return guiTitle;
	}

	public boolean isRandomAllowed() {
		return allowRandom;
	}

	public boolean isAllowRandomUnchoosable() {
		return allowRandomUnchoosable;
	}

	public List<ResourceLocation> getExcludeRandom() {
		return excludeRandom;
	}

	public boolean isReplaceExcludeRandom() {
		return replaceExcludeRandom;
	}

	public ResourceLocation getDefaultOrigin() {
		return defaultOrigin;
	}

	public boolean isAutoChoose() {
		return autoChoose;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean canRegister() {
		boolean merge = REGISTRY.containsKey(this.key);
		if (merge) {
			OriginLayer otherLayer = REGISTRY.get(this.key);
			if (otherLayer == null)
				throw new IllegalStateException("Um... how did u possibly get here? The layer should be in here... somewhere...");
			OriginsPaper.LOGGER.info("Merging other layer, `{}` with {} origins to this layer, `{}` with {} origins", otherLayer.getId(), otherLayer.getOriginIdentifiers().size(), this.getId(), this.getOriginIdentifiers().size());
			this.order = otherLayer.order;
			this.enabled = otherLayer.enabled;

			if (otherLayer.replace) {
				this.origins.clear();
			}

			otherLayer.origins
				.stream()
				.filter(Predicate.not(this.origins::contains))
				.forEach(this.origins::add);

			this.name = otherLayer.name;
			this.guiTitle = otherLayer.guiTitle;
			this.allowRandom = otherLayer.allowRandom;
			this.allowRandomUnchoosable = otherLayer.allowRandomUnchoosable;

			if (otherLayer.replace) {
				this.excludeRandom.clear();
			}

			otherLayer.excludeRandom
				.stream()
				.filter(Predicate.not(this.excludeRandom::contains))
				.forEach(this.excludeRandom::add);

			this.defaultOrigin = otherLayer.defaultOrigin;
			this.autoChoose = otherLayer.autoChoose;
			this.hidden = otherLayer.hidden;
		}

		REGISTRY.put(this.key, this);

		return false;
	}

	public ResourceLocation getId() {
		return key;
	}

	public String getTag() {
		return tag;
	}

	/**
	 * Acts as a wrapper for origins inside layers, allowing for an ENTITY_CONDITION value(Nullable) to be present.
	 */
	public record ConditionedOrigin(ConditionTypeFactory<Entity> condition,
									List<ResourceLocation> origins) {
		public static final SerializableData DATA = SerializableData.serializableData()
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("origins", SerializableDataTypes.list(SerializableDataTypes.IDENTIFIER));

		public boolean isConditionFulfilled(Player playerEntity) {
			return condition == null || condition.test(playerEntity);
		}

		@Deprecated
		public ConditionTypeFactory<Entity> getCondition() {
			return condition;
		}

		@Override
		public @NotNull String toString() {
			return "ConditionedOrigin{" +
				"origins=" + origins +
				'}';
		}
	}
}
