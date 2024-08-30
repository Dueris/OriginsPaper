package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PreventEntityRenderPower extends PowerType {
	private final ConditionTypeFactory<Entity> entityCondition;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;
	private final ConcurrentHashMap<Player, ConcurrentLinkedQueue<Entity>> hiddenEntities = new ConcurrentHashMap<>();

	public PreventEntityRenderPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									ConditionTypeFactory<Entity> entityCondition, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_entity_render"))
			.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	@Override
	public void tick(@NotNull Player player) {
		CraftPlayer craftPlayer = ((CraftPlayer) player.getBukkitEntity());
		for (Entity entity : Shape.getEntities(Shape.SPHERE, player.level(), player.position(), 30)) {
			Tuple<Entity, Entity> bientityTuple = new Tuple<>(player, entity);
			if ((bientityCondition == null || bientityCondition.test(bientityTuple)) &&
				(entityCondition == null || entityCondition.test(entity)) && isActive(player)) {
				craftPlayer.hideEntity(OriginsPaper.getPlugin(), entity.getBukkitEntity());
				markHidden(player, entity);
			} else if (isHidden(player, entity)) {
				craftPlayer.showEntity(OriginsPaper.getPlugin(), entity.getBukkitEntity());
				markShown(player, entity);
			}
		}
	}

	private void markHidden(Player player, Entity entity) {
		if (!hiddenEntities.containsKey(player)) {
			hiddenEntities.put(player, new ConcurrentLinkedQueue<>());
			markHidden(player, entity);
			return;
		}
		hiddenEntities.get(player).add(entity);
	}

	private boolean isHidden(Player player, Entity entity) {
		if (!hiddenEntities.containsKey(player)) {
			hiddenEntities.put(player, new ConcurrentLinkedQueue<>());
			return isHidden(player, entity);
		}
		return hiddenEntities.get(player).contains(entity);
	}

	private void markShown(Player player, Entity entity) {
		if (!hiddenEntities.containsKey(player)) {
			hiddenEntities.put(player, new ConcurrentLinkedQueue<>());
			markShown(player, entity);
			return;
		}
		hiddenEntities.get(player).remove(entity);
	}

	@Override
	public void onRemoved(Player player) {
		if (hiddenEntities.containsKey(player) && hiddenEntities.get(player) != null) {
			CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
			for (Entity entity : hiddenEntities.get(player)) {
				if (entity == null) continue;
				craftPlayer.showEntity(OriginsPaper.getPlugin(), entity.getBukkitEntity());
				markShown(player, entity);
			}
		}
	}

	public boolean doesApply(Entity e, Entity entity) {
		return (entityCondition == null || entityCondition.test(e))
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(entity, e)));
	}
}
