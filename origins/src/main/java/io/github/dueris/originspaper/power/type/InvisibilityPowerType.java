package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class InvisibilityPowerType extends PowerType {

	public static final TypedDataObjectFactory<InvisibilityPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("render_armor", SerializableDataTypes.BOOLEAN, false)
			.add("render_outline", SerializableDataTypes.BOOLEAN, false),
		(data, condition) -> new InvisibilityPowerType(
			data.get("bientity_condition"),
			data.get("render_armor"),
			data.get("render_outline"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_condition", powerType.biEntityCondition)
			.set("render_armor", powerType.renderArmor)
			.set("render_outline", powerType.renderOutline)
	);

	private final Optional<BiEntityCondition> biEntityCondition;

	private final boolean renderArmor;
	private final boolean renderOutline;

	public InvisibilityPowerType(Optional<BiEntityCondition> biEntityCondition, boolean renderArmor, boolean renderOutline, Optional<EntityCondition> condition) {
		super(condition);
		this.biEntityCondition = biEntityCondition;
		this.renderArmor = renderArmor;
		this.renderOutline = renderOutline;
	}

	@Override
	public void serverTick() {
		boolean shouldSetInvisible = isActive();

		getHolder().getBukkitEntity().setInvisible(shouldSetInvisible || getHolder().getBukkitLivingEntity().getActivePotionEffects().stream().map(PotionEffect::getType).toList().contains(PotionEffectType.INVISIBILITY));
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.INVISIBILITY;
	}

	public boolean doesApply(Entity viewer) {
		return biEntityCondition
			.map(condition -> condition.test(viewer, getHolder()))
			.orElse(true);
	}

	public boolean shouldRenderArmor() {
		return renderArmor;
	}

	public boolean shouldRenderOutline() {
		return renderOutline;
	}

}
