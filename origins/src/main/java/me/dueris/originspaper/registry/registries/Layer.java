package me.dueris.originspaper.registry.registries;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.registry.Registrar;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.OriginsDataTypes;
import me.dueris.originspaper.factory.data.types.GuiTitle;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.screen.ScreenNavigator;
import me.dueris.originspaper.util.LangFile;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Layer {
	private final ResourceLocation key;
	private final String tag;
	private int order;
	private int loadingPriority;
	private List<ConditionedOrigin> origins;
	private boolean replace;
	private boolean enabled;
	private @NotNull TextComponent name;
	private GuiTitle guiTitle;
	private boolean allowRandom;
	private boolean allowRandomUnchoosable;
	private List<ResourceLocation> excludeRandom;
	private boolean replaceExcludeRandom;
	private ResourceLocation defaultOrigin;
	private boolean autoChoose;
	private boolean hidden;

	public Layer(@NotNull ResourceLocation key, int order, int loadingPriority, List<ConditionedOrigin> origins, boolean replace, boolean enabled, Component name, GuiTitle guiTitle, boolean allowRandom,
				 boolean allowRandomUnchoosable, List<ResourceLocation> excludeRandom, boolean replaceExcludeRandom, ResourceLocation defaultOrigin, boolean autoChoose, boolean hidden) {
		this.key = key;
		this.tag = key.toString();
		this.order = order;
		this.loadingPriority = loadingPriority;
		this.origins = origins;
		this.replace = replace;
		this.enabled = enabled;
		this.name = net.kyori.adventure.text.Component.text(
			LangFile.transform((name != null ? name.getString() : "origin.$namespace.$path.name")
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

	public static InstanceDefiner buildDefiner() {
		return InstanceDefiner.instanceDefiner()
			.add("order", SerializableDataTypes.INT, Integer.MAX_VALUE)
			.add("loading_priority", SerializableDataTypes.INT, 0)
			.required("origins", SerializableDataTypes.list(OriginsDataTypes.ORIGIN_OR_CONDITIONED_ORIGIN))
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
		List<Origin> tested = new ArrayList<>();

		for (ConditionedOrigin origin : this.getOrigins()) {
			List<Origin> conditionedOrigins = origin.origins.stream().map(CraftApoli::getOrigin).toList();
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
		if (autoChoose) {
			PowerHolderComponent.setOrigin(entity.getBukkitEntityRaw(), this, CraftApoli.getOrigin(defaultOrigin));
			return true;
		} else if (ScreenNavigator.orbChoosing.contains(entity)) {
			return false;
		} else {
			if (!defaultOrigin.equals(CraftApoli.EMPTY_ORIGIN.key())) {
				Origin origin = OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN).get(defaultOrigin);
				if (origin != null) {
					PowerHolderComponent.setOrigin(entity.getBukkitEntityRaw(), this, origin);
					return true;
				}
			}

			return false;
		}
	}

	public List<ResourceLocation> getOriginIdentifiers() {
		return Util.collapseList(this.getOrigins().stream().map(ConditionedOrigin::origins).toList());
	}

	public List<Origin> getRandomOrigins() {
		return !this.isAllowRandom()
			? new ArrayList<>()
			: getOriginIdentifiers().stream()
			.map(CraftApoli::getOrigin)
			.filter(origin -> !origin.unchoosable() || this.isAllowRandomUnchoosable())
			.filter(origin -> {
				if (!this.getExcludeRandom().isEmpty()) {
					for (ResourceLocation identifier : this.getExcludeRandom().stream().toList()) {
						if (origin.key().equals(identifier)) {
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

	public boolean isAllowRandom() {
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
		Registrar<Layer> registrar = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER);
		boolean merge = registrar.keySet().contains(this.key);
		if (merge) {
			Layer otherLayer = registrar.get(this.key);
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
			return false;
		}

		return true;
	}

	public ResourceLocation key() {
		return key;
	}

	public String getTag() {
		return tag;
	}

	/**
	 * Acts as a wrapper for origins inside layers, allowing for an ENTITY_CONDITION value(Nullable) to be present.
	 */
	public record ConditionedOrigin(ConditionFactory<net.minecraft.world.entity.Entity> condition,
									List<ResourceLocation> origins) {
		public static final InstanceDefiner DATA = InstanceDefiner.instanceDefiner()
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("origins", SerializableDataTypes.list(SerializableDataTypes.IDENTIFIER));

		public boolean isConditionFulfilled(Player playerEntity) {
			return condition == null || condition.test(playerEntity);
		}

		@Deprecated
		public ConditionFactory<Entity> getCondition() {
			return condition;
		}

		@Deprecated
		public List<ResourceLocation> getOrigins() {
			return origins;
		}
	}
}
