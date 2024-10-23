package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Predicate;

public class InvisibilityPowerType extends PowerType {

	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

	private final boolean renderArmor;
	private final boolean renderOutline;

	public InvisibilityPowerType(Power power, LivingEntity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition, boolean renderArmor, boolean renderOutline) {
		super(power, entity);
		this.biEntityCondition = biEntityCondition;
		this.renderArmor = renderArmor;
		this.renderOutline = renderOutline;
		setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("invisibility"),
			new SerializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("render_armor", SerializableDataTypes.BOOLEAN, false)
				.add("render_outline", SerializableDataTypes.BOOLEAN, false),
			data -> (power, entity) -> new InvisibilityPowerType(power, entity,
				data.get("bientity_condition"),
				data.getBoolean("render_armor"),
				data.getBoolean("render_outline")
			)
		).allowCondition();
	}

	public boolean doesApply(Entity viewer) {
		return biEntityCondition == null || biEntityCondition.test(new Tuple<>(viewer, entity));
	}

	@Override
	public void tick() {
		boolean shouldSetInvisible = isActive();

		entity.getBukkitEntity().setInvisible(shouldSetInvisible || entity.getBukkitLivingEntity().getActivePotionEffects().stream().map(PotionEffect::getType).toList().contains(PotionEffectType.INVISIBILITY));
	}

	@Override
	public void onRemoved() {
		entity.getBukkitEntity().setInvisible(false);
	}

	public boolean shouldRenderArmor() {
		return renderArmor;
	}

	public boolean shouldRenderOutline() {
		return renderOutline;
	}
}
