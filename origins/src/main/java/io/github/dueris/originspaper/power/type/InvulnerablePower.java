package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.damage.AmountConditionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InvulnerablePower extends PowerType {
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public InvulnerablePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							 @NotNull ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);

		if (damageCondition.getSerializableData() == AmountConditionType.getFactory().getSerializableData()) {
			throw new IllegalArgumentException("Using the 'amount' damage condition type in a power that uses the 'invulnerability' power type is not allowed!");
		}

		this.damageCondition = damageCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("invulnerability"), PowerType.getFactory().getSerializableData()
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION)
			.postProcessor(data -> {

				ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition = data.get("damage_condition");

				if (damageCondition.getSerializerId().equals(OriginsPaper.apoliIdentifier("amount"))) {
					throw new IllegalArgumentException("Using the 'amount' damage condition type in a power that uses the 'invulnerability' power type is not allowed!");
				}

			}));
	}

	public boolean doesApply(DamageSource source) {
		return damageCondition.test(new Tuple<>(source, 0.0F));
	}

}
