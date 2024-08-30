package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class SelfActionWhenHitPower extends CooldownPower {
	private final ActionTypeFactory<Entity> entityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public SelfActionWhenHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  ActionTypeFactory<Entity> entityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, HudRender hudRender, int cooldown) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("self_action_when_hit"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1);
	}

	public boolean doesApply(@NotNull Entity entity, DamageSource source, float amount) {
		return canUse(entity)
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(@NotNull Entity entity) {
		use(entity);
		this.entityAction.accept(entity);
	}

	@EventHandler
	public void onDamage(@NotNull EntityDamageEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			if (getPlayers().contains(player) && isActive(player) && doesApply(player, Util.damageSourceFromBukkit(e.getDamageSource()), Double.valueOf(e.getDamage()).floatValue())) {
				whenHit(player);
			}
		}
	}

}
