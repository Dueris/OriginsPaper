package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyDamageDealtPower extends ModifierPower {
	private final ActionFactory<Entity> selfAction;
	private final ActionFactory<Entity> targetAction;
	private final ActionFactory<Tuple<Entity, Entity>> biEntityAction;
	private final ConditionFactory<Entity> targetCondition;
	private final ConditionFactory<Tuple<Entity, Entity>> biEntityCondition;
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;

	public ModifyDamageDealtPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								  @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ActionFactory<Entity> selfAction, ActionFactory<Entity> targetAction, ActionFactory<Tuple<Entity, Entity>> biEntityAction,
								  ConditionFactory<Entity> targetCondition, ConditionFactory<Tuple<Entity, Entity>> biEntityCondition, ConditionFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.selfAction = selfAction;
		this.targetAction = targetAction;
		this.biEntityAction = biEntityAction;
		this.targetCondition = targetCondition;
		this.biEntityCondition = biEntityCondition;
		this.damageCondition = damageCondition;
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_damage_dealt"))
			.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(@NotNull EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			LivingEntity target = ((CraftLivingEntity) e.getEntity()).getHandle();
			DamageSource source = ((CraftDamageSource) e.getDamageSource()).getHandle();
			float amount = (float) e.getDamage();
			if (getPlayers().contains(player) && isActive(player) && doesApply(source, amount, target, player)) {
				e.setDamage(ModifierUtil.applyModifiers(player, getModifiers(), amount));
				executeActions(target, player);
			}
		}
	}

	public boolean doesApply(DamageSource source, float damageAmount, @Nullable LivingEntity target, @NotNull Entity entity) {
		return (damageCondition == null || damageCondition.test(new Tuple<>(source, damageAmount)))
			&& (target == null || targetCondition == null || targetCondition.test(target))
			&& (target == null || biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target)));
	}

	public void executeActions(Entity target, Entity entity) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (targetAction != null) {
			targetAction.accept(target);
		}

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(entity, target));
		}

	}
}
