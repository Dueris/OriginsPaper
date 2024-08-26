package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyDamageTakenPower extends ModifierPower {
	private final ActionTypeFactory<Entity> selfAction;
	private final ActionTypeFactory<Entity> attackerAction;
	private final ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public ModifyDamageTakenPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ActionTypeFactory<Entity> selfAction, ActionTypeFactory<Entity> attackerAction, ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction,
								  ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.selfAction = selfAction;
		this.attackerAction = attackerAction;
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
		this.damageCondition = damageCondition;
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_damage_taken"))
			.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("attacker_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null);
	}

	@EventHandler
	public void onDamage(@NotNull EntityDamageEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			DamageSource source = ((CraftDamageSource) e.getDamageSource()).getHandle();
			float amount = (float) e.getDamage();
			@Nullable Entity attacker = source.getEntity();

			if (doesApply(source, player, amount)) {
				e.setDamage(ModifierUtil.applyModifiers(player, getModifiers(), e.getDamage()));
				executeActions(attacker, player);
			}
		}
	}

	public boolean doesApply(@NotNull DamageSource source, Entity entity, float damageAmount) {

		Entity attacker = source.getEntity();
		Tuple<DamageSource, Float> damageAndAmount = new Tuple<>(source, damageAmount);

		return attacker == null
			? (damageCondition == null || damageCondition.test(damageAndAmount)) && biEntityCondition == null
			: (damageCondition == null || damageCondition.test(damageAndAmount)) && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(attacker, entity)));

	}

	public void executeActions(Entity attacker, Entity entity) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (attackerAction != null) {
			attackerAction.accept(attacker);
		}

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(attacker, entity));
		}

	}
}
