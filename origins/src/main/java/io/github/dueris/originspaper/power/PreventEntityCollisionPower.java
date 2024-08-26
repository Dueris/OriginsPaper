package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

// TODO: make bientity condiiton work | paper needs both entities collision off to make work...
public class PreventEntityCollisionPower extends PowerType {
	private final ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition;

	public PreventEntityCollisionPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									   ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.biEntityCondition = biEntityCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_entity_collision"))
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	public boolean doesApply(Entity target, Entity entity) {
		return biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target));
	}

	@Override
	public void tick(@NotNull Player player) {
		player.getBukkitEntity().setCollidable(!isActive(player));
	}
}
