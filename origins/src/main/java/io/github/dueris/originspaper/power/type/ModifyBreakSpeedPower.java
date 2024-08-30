package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Since the client needs this information aswell, we mess around with the players
 * block break speed attribute
 */
public class ModifyBreakSpeedPower extends ModifierPower {

	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final LinkedList<Modifier> hardnessModifiers;

	public ModifyBreakSpeedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ConditionTypeFactory<BlockInWorld> blockCondition, @Nullable Modifier hardnessModifier, @Nullable List<Modifier> hardnessModifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
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
			getModifiers().add(new Modifier(m.getOperation(), m.getData().set("amount",
				original < 0 ? original * 10 : original * 100)));
		}
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_break_speed"))
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("hardness_modifier", Modifier.DATA_TYPE, null)
			.add("hardness_modifiers", Modifier.LIST_TYPE, null);
	}

	public LinkedList<Modifier> getHardnessModifiers() {
		return hardnessModifiers;
	}

	public boolean doesApply(BlockPos pos, Entity entity) {
		return isActive(entity) && (blockCondition == null || blockCondition.test(new BlockInWorld(entity.level(), pos, true)));
	}

	public void applyPower(BlockPos pos, @NotNull Player player, boolean modifyHardness) {
		float b = 1.0F;
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();

		AttributeInstance instance = craftPlayer.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
		if (modifyHardness ? getHardnessModifiers().isEmpty() : getModifiers().isEmpty())
			return; // Don't apply empty modifiers
		instance.setBaseValue(
			doesApply(pos, player) ? ModifierUtil.applyModifiers(player,
				modifyHardness ? getHardnessModifiers() : getModifiers(), b) : b
		);

	}

	@Override
	public void onRemoved(@NotNull Player player) {
		player.getBukkitEntity().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1.0F);
	}

	public ConditionTypeFactory<BlockInWorld> getBlockCondition() {
		return blockCondition;
	}
}
