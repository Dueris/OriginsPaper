package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;

public class ModifyJumpPowerType extends ValueModifyingPowerType {

	private final Consumer<Entity> entityAction;

	public ModifyJumpPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);
		this.entityAction = entityAction;

		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_jump"),
			new SerializableData()
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			data -> (power, entity) -> new ModifyJumpPowerType(power, entity,
				data.get("entity_action"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public void executeAction() {

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
