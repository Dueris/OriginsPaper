package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import me.dueris.originspaper.factory.data.types.modifier.ModifierUtil;
import me.dueris.originspaper.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class DamageAction {

	public static void action(@NotNull DeserializedFactoryJson data, Entity entity) {

		Float damageAmount = data.get("amount");
		List<Modifier> modifiers = new LinkedList<>();

		data.<Modifier>ifPresent("modifier", modifiers::add);
		data.<List<Modifier>>ifPresent("modifiers", modifiers::addAll);

		if (!modifiers.isEmpty() && entity instanceof LivingEntity livingEntity) {
			damageAmount = (float) ModifierUtil.applyModifiers(livingEntity, modifiers, livingEntity.getMaxHealth());
		}

		if (damageAmount == null) {
			return;
		}

		try {
			DamageSource source;
			if (data.isPresent("damage_type")) {
				source = Util.getDamageSource(Util.DAMAGE_REGISTRY.get(data.getId("damage_type")));
			} else {
				source = entity.level().damageSources().generic();
			}
			if (data.isPresent("source") && !data.isPresent("damage_type")) {
				OriginsPaper.getPlugin().getLogger().warning("A \"source\" field was provided in the bientity_action \"apoli:damage\", please use the \"damage_type\" field instead.");
			}
			entity.hurt(source, damageAmount);
		} catch (Throwable t) {
			OriginsPaper.getPlugin().getLog4JLogger().error("Error trying to deal damage via the `damage` entity action: {}", t.getMessage());
		}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("damage"),
			InstanceDefiner.instanceDefiner()
				.add("amount", SerializableDataTypes.FLOAT, null)
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			DamageAction::action
		);
	}
}
