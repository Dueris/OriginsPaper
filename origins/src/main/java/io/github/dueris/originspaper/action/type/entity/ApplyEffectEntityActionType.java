package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ApplyEffectEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ApplyEffectEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE.optional(), Optional.empty())
			.add("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES.optional(), Optional.empty()),
		data -> new ApplyEffectEntityActionType(
			data.get("effect"),
			data.get("effects")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("effect", actionType.effect)
			.set("effects", actionType.effects)
	);

	private final Optional<MobEffectInstance> effect;
	private final Optional<List<MobEffectInstance>> effects;

	private final List<MobEffectInstance> allEffects;

	public ApplyEffectEntityActionType(Optional<MobEffectInstance> effect, Optional<List<MobEffectInstance>> effects) {

		this.effect = effect;
		this.effects = effects;

		this.allEffects = new ObjectArrayList<>();

		this.effect.ifPresent(this.allEffects::add);
		this.effects.ifPresent(this.allEffects::addAll);

	}

	@Override
	protected void execute(Entity entity) {

		if (!entity.level().isClientSide() && entity instanceof LivingEntity living) {
			allEffects.forEach(living::addEffect);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.APPLY_EFFECT;
	}

}
