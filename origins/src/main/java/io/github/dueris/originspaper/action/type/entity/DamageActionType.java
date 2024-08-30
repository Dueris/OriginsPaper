package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.CraftRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;

public class DamageActionType {

	public static void action(Entity entity, ResourceKey<DamageType> damageTypeKey, @Nullable Float amount, @NotNull Collection<Modifier> modifiers) {

		if (!modifiers.isEmpty() && entity instanceof LivingEntity living) {
			amount = (float) ModifierUtil.applyModifiers(entity, modifiers.stream().toList(), living.getMaxHealth());
		}

		if (amount != null) {
			entity.hurt(source(damageTypeKey), amount);
		}

	}

	private static @NotNull DamageSource source(ResourceKey<DamageType> key) {
		return new DamageSource(CraftRegistry.getMinecraftRegistry().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(key));
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("damage"),
			new SerializableData()
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE)
				.add("amount", SerializableDataTypes.FLOAT, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null)
				.postProcessor(data -> {

					if (!data.isPresent("amount") && !Util.anyPresent(data, "modifier", "modifiers")) {
						throw new IllegalStateException("Any of 'amount', 'modifier', or 'modifiers' fields must be defined!");
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
