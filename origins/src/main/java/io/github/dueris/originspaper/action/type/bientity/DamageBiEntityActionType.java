package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class DamageBiEntityActionType extends BiEntityActionType {

	public static final TypedDataObjectFactory<DamageBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("damage_type", SerializableDataTypes.DAMAGE_TYPE)
			.add("amount", SerializableDataTypes.FLOAT.optional(), Optional.empty())
			.add("modifier", Modifier.DATA_TYPE, null)
			.addFunctionedDefault("modifiers", Modifier.LIST_TYPE, data -> Util.singletonListOrNull(data.get("modifier")))
			.validate(Util.validateAnyFieldsPresent("amount", "modifier", "modifiers")),
		data -> new DamageBiEntityActionType(
			data.get("damage_type"),
			data.get("amount"),
			data.get("modifiers")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("damage_type", actionType.damageType)
			.set("amount", actionType.amount)
			.set("modifiers", actionType.modifiers)
	);

	private final ResourceKey<DamageType> damageType;
	private final Optional<Float> amount;

	private final List<Modifier> modifiers;

	public DamageBiEntityActionType(ResourceKey<DamageType> damageType, Optional<Float> amount, List<Modifier> modifiers) {
		this.damageType = damageType;
		this.amount = amount;
		this.modifiers = modifiers;
	}

	@Override
	protected void execute(Entity actor, Entity target) {

		if (actor != null && target != null) {
			this.amount
				.or(() -> getModifiedAmount(actor, target))
				.ifPresent(amount -> target.hurt(actor.damageSources().source(damageType, actor), amount));
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.DAMAGE;
	}

	private Optional<Float> getModifiedAmount(Entity actor, Entity target) {
		return !modifiers.isEmpty() && target instanceof LivingEntity livingTarget
			? Optional.of((float) ModifierUtil.applyModifiers(actor, modifiers, livingTarget.getMaxHealth()))
			: Optional.empty();
	}

}
