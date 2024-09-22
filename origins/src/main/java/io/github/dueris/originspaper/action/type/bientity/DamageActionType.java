package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
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

	public static void action(Entity actor, Entity target, ResourceKey<DamageType> damageTypeKey, @Nullable Float amount, Collection<Modifier> modifiers) {

		if (actor == null || target == null) {
			return;
		}

		if (!modifiers.isEmpty() && target instanceof LivingEntity livingTarget) {
			amount = (float) ModifierUtil.applyModifiers(actor, modifiers.stream().toList(), livingTarget.getMaxHealth());
		}

		if (amount != null) {
			target.hurt(source(damageTypeKey, actor), amount);
		}

	}

	private static @NotNull DamageSource source(ResourceKey<DamageType> key, @javax.annotation.Nullable Entity attacker) {
		return new DamageSource(CraftRegistry.getMinecraftRegistry().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(key), attacker);
	}

	public static @NotNull ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
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
			(data, actorAndTarget) -> {

				Collection<Modifier> modifiers = new LinkedList<>();

				data.ifPresent("modifier", modifiers::add);
				data.ifPresent("modifiers", modifiers::addAll);

				action(actorAndTarget.getA(), actorAndTarget.getB(),
					data.get("damage_type"),
					data.get("amount"),
					modifiers
				);

			}
		);
	}

}
