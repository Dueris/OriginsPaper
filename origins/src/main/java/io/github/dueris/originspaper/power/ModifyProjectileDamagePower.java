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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.AbstractProjectile;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyProjectileDamagePower extends ModifierPower {
	private final ActionTypeFactory<Entity> selfAction;
	private final ActionTypeFactory<Entity> targetAction;
	private final ConditionTypeFactory<Entity> targetCondition;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public ModifyProjectileDamagePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ActionTypeFactory<Entity> selfAction, ActionTypeFactory<Entity> targetAction, ConditionTypeFactory<Entity> targetCondition,
									   ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.selfAction = selfAction;
		this.targetAction = targetAction;
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_projectile_damage"))
			.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(@NotNull EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof AbstractProjectile projectile && projectile.getHandle().getOwner() instanceof Player player) {
			LivingEntity target = ((CraftLivingEntity) e.getEntity()).getHandle();
			DamageSource source = ((CraftDamageSource) e.getDamageSource()).getHandle();
			float amount = (float) e.getDamage();
			if (source.is(DamageTypeTags.IS_PROJECTILE) && getPlayers().contains(player) && isActive(player) && doesApply(source, amount, target)) {
				e.setDamage(ModifierUtil.applyModifiers(player, getModifiers(), amount));
				executeActions(target, player);
			}
		}
	}

	public boolean doesApply(DamageSource source, float damageAmount, LivingEntity target) {
		return damageCondition.test(new Tuple<>(source, damageAmount)) && (target == null || targetCondition == null || targetCondition.test(target));
	}

	public void executeActions(Entity target, Entity entity) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (targetAction != null) {
			targetAction.accept(target);
		}

	}
}
