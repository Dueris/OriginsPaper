package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ModifierPower extends PowerType {
	private final List<Modifier> modifiers = new LinkedList<>();

	public ModifierPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						 @Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		if (modifier != null) {
			this.modifiers.add(modifier);
		}
		if (modifiers != null) {
			this.modifiers.addAll(modifiers);
		}
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory()
			.add("modifier", Modifier.DATA_TYPE, null)
			.add("modifiers", Modifier.LIST_TYPE, null);
	}

	public void addModifier(Modifier modifier) {
		this.modifiers.add(modifier);
	}

	public List<Modifier> getModifiers() {
		return modifiers;
	}
}
