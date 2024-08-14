package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.condition.types.item.AmountCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class InvulnerablePower extends PowerType {
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;

	public InvulnerablePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							 @NotNull ConditionFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);

		if (damageCondition.getSerializableData() == AmountCondition.getFactory().getSerializableData()) {
			throw new IllegalArgumentException("Using the 'amount' damage condition type in a power that uses the 'invulnerability' power type is not allowed!");
		}

		this.damageCondition = damageCondition;
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("invulnerability"))
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION);
	}

	@EventHandler
	public void DamageEvent(@NotNull EntityDamageEvent e) {
		if (((CraftEntity) e.getEntity()).getHandle() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (isActive(p) && damageCondition.test(new Tuple<>(((CraftDamageSource) e.getDamageSource()).getHandle(), 0.0F))) {
					e.setCancelled(true);
					e.setDamage(0);
				}

			}
		}
	}
}
