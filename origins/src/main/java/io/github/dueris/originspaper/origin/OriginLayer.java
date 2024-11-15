package io.github.dueris.originspaper.origin;

import com.google.common.collect.ImmutableList;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.OriginsDataTypes;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class OriginLayer implements Comparable<OriginLayer> {

	public static final CompoundSerializableDataType<OriginLayer> DATA_TYPE = SerializableDataType.compound(
		new SerializableData()
			.add("id", SerializableDataTypes.IDENTIFIER)
			.addSupplied("order", SerializableDataTypes.INT, OriginLayerManager::size)
			.add("origins", OriginsDataTypes.ORIGINS_OR_CONDITIONED_ORIGINS)
			.add("replace_origins", SerializableDataTypes.BOOLEAN, false)
			.add("replace", SerializableDataTypes.BOOLEAN, false)
			.add("enabled", SerializableDataTypes.BOOLEAN, true)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("gui_title", OriginsDataTypes.GUI_TITLE, null)
			.add("missing_name", SerializableDataTypes.TEXT, null)
			.add("missing_description", SerializableDataTypes.TEXT, null)
			.add("allow_random", SerializableDataTypes.BOOLEAN, false)
			.add("allow_random_unchoosable", SerializableDataTypes.BOOLEAN, false)
			.add("exclude_random", SerializableDataTypes.IDENTIFIERS, new LinkedList<>())
			.add("replace_exclude_random", SerializableDataTypes.BOOLEAN, false)
			.add("default_origin", SerializableDataTypes.IDENTIFIER, null)
			.add("auto_choose", SerializableDataTypes.BOOLEAN, false)
			.add("hidden", SerializableDataTypes.BOOLEAN, false),
		data -> new OriginLayer(
			data.get("id"),
			data.get("order"),
			data.get("origins"),
			data.get("replace_origins"),
			data.get("replace"),
			data.get("enabled"),
			data.get("name"),
			data.get("gui_title"),
			data.get("missing_name"),
			data.get("missing_description"),
			data.get("allow_random"),
			data.get("allow_random_unchoosable"),
			data.get("exclude_random"),
			data.get("replace_exclude_random"),
			data.get("default_origin"),
			data.get("auto_choose"),
			data.get("hidden")
		),
		(layer, serializableData) -> serializableData.instance()
			.set("id", layer.getId())
			.set("order", layer.getOrder())
			.set("origins", layer.getConditionedOrigins())
			.set("replace_origins", layer.shouldReplaceOrigins())
			.set("replace", layer.shouldReplace())
			.set("enabled", layer.isEnabled())
			.set("name", layer.getName())
			.set("gui_title", layer.getGuiTitle())
			.set("missing_name", layer.getMissingName())
			.set("missing_description", layer.getMissingDescription())
			.set("allow_random", layer.isRandomAllowed())
			.set("allow_random_unchoosable", layer.isUnchoosableRandomAllowed())
			.set("exclude_random", layer.getOriginsExcludedFromRandom())
			.set("replace_exclude_random", layer.shouldReplaceExcludedOriginsFromRandom())
			.set("default_origin", layer.getDefaultOrigin())
			.set("auto_choose", layer.shouldAutoChoose())
			.set("hidden", layer.isHidden())
	);

	private final ResourceLocation id;
	private final int order;

	private final Set<ConditionedOrigin> origins;
	private final boolean replaceOrigins;

	private final boolean replace;
	private final boolean enabled;

	private final Component name;
	private final GuiTitle guiTitle;

	@Nullable
	private final Component missingName;
	@Nullable
	private final Component missingDescription;

	private final boolean randomAllowed;
	private final boolean unchoosableRandomAllowed;

	private final Set<ResourceLocation> originsExcludedFromRandom;
	private final boolean replaceOriginsExcludedFromRandom;

	@Nullable
	private final ResourceLocation defaultOrigin;
	private final boolean autoChoose;

	private final boolean hidden;

	protected OriginLayer(ResourceLocation id, int order, Collection<ConditionedOrigin> origins, boolean replaceOrigins, boolean replace, boolean enabled, @Nullable Component name, GuiTitle guiTitle, @Nullable Component missingName, @Nullable Component missingDescription, boolean randomAllowed, boolean unchoosableRandomAllowed, Collection<ResourceLocation> originsExcludedFromRandom, boolean replaceOriginsExcludedFromRandom, @Nullable ResourceLocation defaultOrigin, boolean autoChoose, boolean hidden) {

		this.id = id;

		this.order = order;
		this.origins = new ObjectLinkedOpenHashSet<>(origins);
		this.replaceOrigins = replaceOrigins;
		this.replace = replace;
		this.enabled = enabled;

		this.name = name == null ? Component.translatable("layer." + id.getNamespace() + "." + id.getPath() + ".name") : name;

		if (guiTitle == null) {

			Component viewOriginText = Component.translatable("origins.gui.view_origin.title", this.name);
			Component chooseOriginText = Component.translatable("origins.gui.choose_origin.title", this.name);

			this.guiTitle = new GuiTitle(viewOriginText, chooseOriginText);

		} else {

			Component viewOriginText = guiTitle.viewOrigin();
			Component chooseOriginText = guiTitle.chooseOrigin();

			this.guiTitle = new GuiTitle(viewOriginText, chooseOriginText);

		}

		this.missingName = missingName;
		this.missingDescription = missingDescription;
		this.randomAllowed = randomAllowed;
		this.unchoosableRandomAllowed = unchoosableRandomAllowed;
		this.originsExcludedFromRandom = new ObjectLinkedOpenHashSet<>(originsExcludedFromRandom);
		this.replaceOriginsExcludedFromRandom = replaceOriginsExcludedFromRandom;
		this.defaultOrigin = defaultOrigin;
		this.autoChoose = autoChoose;
		this.hidden = hidden;

	}

	public int getOrder() {
		return order;
	}

	public ImmutableList<ConditionedOrigin> getConditionedOrigins() {
		return ImmutableList.copyOf(origins);
	}

	public ImmutableList<ResourceLocation> getOriginsExcludedFromRandom() {
		return ImmutableList.copyOf(originsExcludedFromRandom);
	}

	public GuiTitle getGuiTitle() {
		return guiTitle;
	}

	public Component getName() {
		return name;
	}

	@Nullable
	public Component getMissingName() {
		return missingName;
	}

	@Nullable
	public Component getMissingDescription() {
		return missingDescription;
	}

	public Component getViewOriginTitle() {
		return guiTitle.viewOrigin();
	}

	public Component getChooseOriginTitle() {
		return guiTitle.chooseOrigin();
	}

	public boolean shouldReplace() {
		return replace;
	}

	public boolean shouldReplaceOrigins() {
		return replaceOrigins;
	}

	public boolean shouldReplaceExcludedOriginsFromRandom() {
		return replaceOriginsExcludedFromRandom;
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean hasDefaultOrigin() {
		return this.getDefaultOrigin() != null;
	}

	@Nullable
	public ResourceLocation getDefaultOrigin() {
		return defaultOrigin;
	}

	public boolean shouldAutoChoose() {
		return autoChoose;
	}

	public List<ResourceLocation> getOrigins() {
		return getOrigins(null);
	}

	public List<ResourceLocation> getOrigins(@Nullable Player playerEntity) {
		return origins
			.stream()
			.filter(co -> playerEntity == null || co.isConditionFulfilled(playerEntity))
			.flatMap(co -> co.origins.stream())
			.filter(OriginManager::contains)
			.collect(Collectors.toList());
	}

	public int getOriginOptionCount(Player playerEntity) {

		int choosableOrigins = (int) getOrigins(playerEntity)
			.stream()
			.map(OriginManager::get)
			.filter(Origin::isChoosable)
			.count();

		if (choosableOrigins > 1 && (randomAllowed && !getRandomOrigins(playerEntity).isEmpty())) {
			choosableOrigins++;
		}

		return choosableOrigins;

	}

	public boolean contains(ResourceLocation originId) {
		return origins
			.stream()
			.flatMap(co -> co.origins().stream())
			.anyMatch(originId::equals);
	}

	public boolean contains(Origin origin) {
		return contains(origin.getId());
	}

	public boolean contains(ResourceLocation originId, Player playerEntity) {
		return origins
			.stream()
			.filter(co -> co.isConditionFulfilled(playerEntity))
			.flatMap(co -> co.origins().stream())
			.anyMatch(originId::equals);
	}

	public boolean contains(Origin origin, Player playerEntity) {
		return contains(origin.getId(), playerEntity);
	}

	public boolean isRandomAllowed() {
		return randomAllowed;
	}

	public boolean isUnchoosableRandomAllowed() {
		return unchoosableRandomAllowed;
	}

	public boolean isHidden() {
		return hidden;
	}

	public List<ResourceLocation> getRandomOrigins(Player playerEntity) {
		return origins
			.stream()
			.filter(co -> co.isConditionFulfilled(playerEntity))
			.flatMap(co -> co.origins.stream())
			.filter(OriginManager::contains)
			.filter(oId -> !originsExcludedFromRandom.contains(oId))
			.filter(oid -> unchoosableRandomAllowed || OriginManager.get(oid).isChoosable())
			.collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof OriginLayer that && Objects.equals(this.getId(), that.getId()));
	}

	@Override
	public int compareTo(OriginLayer that) {
		return Integer.compare(this.order, that.order);
	}

	@Override
	public String toString() {
		return "OriginLayer{" +
			"id=" + id +
			", order=" + order +
			", origins=" + getOrigins() +
			", replaceOrigins=" + replaceOrigins +
			", enabled=" + enabled +
			", name=" + name +
			", guiTitle=" + guiTitle +
			", missingName=" + missingName +
			", missingDescription=" + missingDescription +
			", randomAllowed=" + randomAllowed +
			", unchoosableRandomAllowed=" + unchoosableRandomAllowed +
			", originsExcludedFromRandom=" + originsExcludedFromRandom +
			", replaceOriginsExcludedFromRandom=" + replaceOriginsExcludedFromRandom +
			", defaultOrigin=" + defaultOrigin +
			", autoChoose=" + autoChoose +
			", hidden=" + hidden +
			'}';
	}

	public record GuiTitle(@Nullable Component viewOrigin, @Nullable Component chooseOrigin) {

		public static final CompoundSerializableDataType<GuiTitle> DATA_TYPE = SerializableDataType.compound(
			new SerializableData()
				.add("view_origin", SerializableDataTypes.TEXT, null)
				.add("choose_origin", SerializableDataTypes.TEXT, null),
			data -> new GuiTitle(
				data.get("view_origin"),
				data.get("choose_origin")
			),
			(guiTitle, serializableData) -> serializableData.instance()
				.set("view_origin", guiTitle.viewOrigin())
				.set("choose_origin", guiTitle.chooseOrigin())
		);

	}

	public record ConditionedOrigin(Optional<EntityCondition> condition,
									List<ResourceLocation> origins) {

		public static final CompoundSerializableDataType<ConditionedOrigin> DATA_TYPE = SerializableDataType.compound(
			new SerializableData()
				.add("condition", EntityAction.DATA_TYPE.optional(), Optional.empty())
				.add("origins", SerializableDataTypes.IDENTIFIERS),
			data -> new ConditionedOrigin(
				data.get("condition"),
				data.get("origins")
			),
			(conditionedOrigin, serializableData) -> serializableData.instance()
				.set("condition", conditionedOrigin.condition())
				.set("origins", conditionedOrigin.origins())
		);

		public boolean isConditionFulfilled(Player playerEntity) {
			return condition.isEmpty() || condition.get().test(playerEntity);
		}

	}

}
