package io.github.dueris.originspaper.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class AttributePower extends PowerType {
	protected final boolean updateHealth;
	protected final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();

	public AttributePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						  @Nullable AttributedEntityAttributeModifier modifier, @Nullable List<AttributedEntityAttributeModifier> modifiers, boolean updateHealth) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.updateHealth = updateHealth;
		if (modifier != null) {
			this.modifiers.add(modifier);
		}
		if (modifiers != null && !modifiers.isEmpty()) {
			this.modifiers.addAll(modifiers);
		}
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("attribute"), PowerType.getFactory().getSerializableData()
			.add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
			.add("modifiers", SerializableDataTypes.list(ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER), null)
			.add("update_health", SerializableDataTypes.BOOLEAN, true));
	}

	public AttributePower addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
		AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
		this.modifiers.add(mod);
		return this;
	}

	public AttributePower addModifier(AttributedEntityAttributeModifier modifier) {
		this.modifiers.add(modifier);
		return this;
	}

	@Override
	public void onAdded(Player player) {
		this.applyTempMods(player);
	}

	@Override
	public void onRemoved(Player player) {
		this.removeTempMods(player);
	}

	@Override
	public void onLost(Player player) {
		this.removeTempMods(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(@NotNull PlayerJoinEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player)) {
			applyTempMods(player);
		}
	}

	protected void applyTempMods(@NotNull Player entity) {

		if (entity.level().isClientSide) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousHealthPercent = entity.getHealth() / previousMaxHealth;

		modifiers.stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && !pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().addTransientModifier(pair.getFirst().modifier()));

		float currentMaxHealth = entity.getMaxHealth();
		if (updateHealth && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousHealthPercent);
		}

	}

	protected void removeTempMods(@NotNull Player entity) {

		if (entity.level().isClientSide) {
			return;
		}

		float previousMaxHealth = entity.getMaxHealth();
		float previousHealthPercent = entity.getHealth() / previousMaxHealth;

		modifiers.stream()
			.filter(mod -> entity.getAttributes().hasAttribute(mod.attribute()))
			.map(mod -> Pair.of(mod, entity.getAttribute(mod.attribute())))
			.filter(pair -> pair.getSecond() != null && pair.getSecond().hasModifier(pair.getFirst().modifier().id()))
			.forEach(pair -> pair.getSecond().removeModifier(pair.getFirst().modifier().id()));

		float currentMaxHealth = entity.getMaxHealth();
		if (updateHealth && currentMaxHealth != previousMaxHealth) {
			entity.setHealth(currentMaxHealth * previousHealthPercent);
		}

	}
}
