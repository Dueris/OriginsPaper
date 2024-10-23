package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

// TODO - Make this more plugin-compatible
public class ModifyBreakSpeedPowerType extends ValueModifyingPowerType {

	private final Predicate<BlockInWorld> blockCondition;
	private final List<Modifier> hardnessModifiers;

	public ModifyBreakSpeedPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> blockCondition, Modifier deltaModifier, List<Modifier> deltaModifiers, Modifier hardnessModifier, List<Modifier> hardnessModifiers) {
		super(power, entity);

		if (deltaModifier != null) {
			this.addModifier(deltaModifier);
		}

		if (deltaModifiers != null) {
			deltaModifiers.forEach(this::addModifier);
		}

		this.blockCondition = blockCondition;
		this.hardnessModifiers = new LinkedList<>();

		if (hardnessModifier != null) {
			this.hardnessModifiers.add(hardnessModifier);
		}

		if (hardnessModifiers != null) {
			this.hardnessModifiers.addAll(hardnessModifiers);
		}

		List<Modifier> copy = new LinkedList<>(getModifiers());
		getModifiers().clear();
		for (Modifier m : copy) {
			double original = m.getData().getDouble("amount");
			getModifiers().add(new Modifier(m.getOperation(), m.getData().set("amount", original * 10)));
		}
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_break_speed"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null)
				.addFunctionedDefault("delta_modifier", Modifier.DATA_TYPE, data -> data.get("modifier"))
				.addFunctionedDefault("delta_modifiers", Modifier.LIST_TYPE, data -> data.get("modifiers"))
				.add("hardness_modifier", Modifier.DATA_TYPE, null)
				.add("hardness_modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyBreakSpeedPowerType(power, entity,
				data.get("block_condition"),
				data.get("delta_modifier"),
				data.get("delta_modifiers"),
				data.get("hardness_modifier"),
				data.get("hardness_modifiers")
			)
		).allowCondition();
	}

	public void applyPower(@NotNull BlockPos pos, @NotNull Player player, boolean modifyHardness) {
		float b = 1.0F;
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();

		AttributeInstance instance = craftPlayer.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
		if (modifyHardness ? getHardnessModifiers().isEmpty() : getModifiers().isEmpty())
			return; // Don't apply empty modifiers
		instance.setBaseValue(
			isActive() && doesApply(pos) ? ModifierUtil.applyModifiers(player,
				modifyHardness ? getHardnessModifiers() : getModifiers(), b) : b
		);

	}

	public List<Modifier> getHardnessModifiers() {
		return hardnessModifiers;
	}

	public boolean doesApply(BlockPos pos) {
		return blockCondition == null || blockCondition.test(new BlockInWorld(entity.level(), pos, true));
	}

	@Override
	public void onRemoved() {
		if (entity instanceof Player player) {
			player.getBukkitLivingEntity().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1.0F);
		}
	}

}
