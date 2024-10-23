package io.github.dueris.originspaper.action.type.entity;

import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;

public class DamageActionType {

	public static void action(Entity entity, ResourceKey<DamageType> damageTypeKey, @Nullable Float amount, Collection<Modifier> modifiers) {

		if (!modifiers.isEmpty() && entity instanceof LivingEntity living) {
			amount = (float) ModifierUtil.applyModifiers(entity, modifiers, living.getMaxHealth());
		}

		if (amount != null) {
			entity.hurt(new DamageSource(entity.registryAccess().registry(Registries.DAMAGE_TYPE).orElseThrow().getHolderOrThrow(damageTypeKey), entity), amount);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("damage"),
			new SerializableData()
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE)
				.add("amount", SerializableDataTypes.FLOAT, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null)
				.validate(data -> {

					if (!data.isPresent("amount") && !Util.anyPresent(data, "modifier", "modifiers")) {
						return DataResult.error(() -> "Any of 'amount', 'modifier', or 'modifiers' fields must be defined!");
					} else {
						return DataResult.success(data);
					}

				}),
			(data, entity) -> {

				Collection<Modifier> modifiers = new LinkedList<>();

				data.ifPresent("modifier", modifiers::add);
				data.ifPresent("modifiers", modifiers::addAll);

				action(entity,
					data.get("damage_type"),
					data.get("amount"),
					modifiers
				);

			}
		);
	}

}
