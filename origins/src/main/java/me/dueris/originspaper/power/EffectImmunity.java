package me.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EffectImmunity extends PowerType {
	private final boolean inverted;
	private final ArrayList<Holder<MobEffect>> effects;

	public EffectImmunity(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						  Holder<MobEffect> mobEffect, List<Holder<MobEffect>> mobEffects, boolean inverted) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.inverted = inverted;
		this.effects = new ArrayList<>();
		if (mobEffect != null) {
			effects.add(mobEffect);
		}

		if (mobEffects != null && !mobEffects.isEmpty()) {
			effects.addAll(mobEffects);
		}
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("effect_immunity"))
			.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
			.add("effects", SerializableDataTypes.list(SerializableDataTypes.STATUS_EFFECT_ENTRY), null)
			.add("inverted", SerializableDataTypes.BOOLEAN, false);
	}

	@Override
	public void tick(Player p) {
		if (!effects.isEmpty() && isActive(p)) {
			List<Holder<MobEffect>> toRemove = new ArrayList<>();
			for (Holder<MobEffect> effect : p.activeEffects.keySet()) {
				boolean shouldRemove = inverted != effects.contains(effect);
				if (shouldRemove) {
					toRemove.add(effect);
				}
			}

			toRemove.forEach(p::removeEffect);
		}
	}
}
