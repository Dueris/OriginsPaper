package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifyStatusEffectDurationPower extends ModifierPower {
	private final Set<Holder<MobEffect>> statusEffects;

	public ModifyStatusEffectDurationPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
										   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, Holder<MobEffect> statusEffect, List<Holder<MobEffect>> statusEffects) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);

		this.statusEffects = new HashSet<>();

		if (statusEffect != null) {
			this.statusEffects.add(statusEffect);
		}

		if (statusEffects != null) {
			this.statusEffects.addAll(statusEffects);
		}
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_status_effect_duration"))
			.add("status_effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
			.add("status_effects", SerializableDataBuilder.of(SerializableDataTypes.STATUS_EFFECT_ENTRY.listOf()), null);
	}

	public boolean doesApply(Holder<MobEffect> statusEffect) {
		return statusEffects.contains(statusEffect);
	}
}
