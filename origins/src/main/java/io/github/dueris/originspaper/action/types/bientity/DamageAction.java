package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class DamageAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> entities) {

		Entity actor = entities.getA();
		Entity target = entities.getB();

		if (actor == null || target == null) {
			return;
		}

		Float damageAmount = data.get("amount");
		List<Modifier> modifiers = new LinkedList<>();

		data.<Modifier>ifPresent("modifier", modifiers::add);
		data.<List<Modifier>>ifPresent("modifiers", modifiers::addAll);

		if (!modifiers.isEmpty() && target instanceof LivingEntity livingTarget) {
			damageAmount = (float) ModifierUtil.applyModifiers(actor, modifiers, livingTarget.getMaxHealth());
		}

		if (damageAmount == null) {
			return;
		}

		try {
			DamageSource source;
			if (data.isPresent("damage_type")) {
				source = Util.getDamageSource(Util.DAMAGE_REGISTRY.get((ResourceKey<DamageType>) data.get("damage_type")));
			} else {
				source = actor.level().damageSources().generic();
			}
			if (data.isPresent("source") && !data.isPresent("damage_type")) {
				OriginsPaper.getPlugin().getLogger().warning("A \"source\" field was provided in the bientity_action \"apoli:damage\", please use the \"damage_type\" field instead.");
			}
			target.hurt(source, damageAmount);
		} catch (Throwable t) {
			OriginsPaper.getPlugin().getLog4JLogger().error("Error trying to deal damage via the `damage` bi-entity action: {}", t.getMessage());
		}

	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
				OriginsPaper.apoliIdentifier("damage"),
				SerializableData.serializableData()
						.add("amount", SerializableDataTypes.FLOAT, null)
						.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null)
						.add("modifier", Modifier.DATA_TYPE, null)
						.add("modifiers", Modifier.LIST_TYPE, null),
				DamageAction::action
		);
	}
}
