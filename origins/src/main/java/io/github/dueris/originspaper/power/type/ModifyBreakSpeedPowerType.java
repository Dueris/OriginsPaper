package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

// TODO - Make this more plugin-compatible
public class ModifyBreakSpeedPowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifyBreakSpeedPowerType> DATA_FACTORY = createConditionedModifyingDataFactory(
		new SerializableData()
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("hardness_modifier", Modifier.DATA_TYPE, null)
			.addFunctionedDefault("hardness_modifiers", Modifier.LIST_TYPE, data -> Util.singletonListOrEmpty(data.get("hardness_modifier"))),
		(data, modifiers, condition) -> new ModifyBreakSpeedPowerType(
			data.get("block_condition"),
			data.get("hardness_modifiers"),
			modifiers,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("block_condition", powerType.blockCondition)
			.set("hardness_modifiers", powerType.hardnessModifiers)
	);

	private final Optional<BlockCondition> blockCondition;
	private final List<Modifier> hardnessModifiers;

	public ModifyBreakSpeedPowerType(Optional<BlockCondition> blockCondition, List<Modifier> hardnessModifiers, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.blockCondition = blockCondition;
		this.hardnessModifiers = hardnessModifiers;
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

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_BREAK_SPEED;
	}

	public List<Modifier> getHardnessModifiers() {
		return new ObjectArrayList<>(hardnessModifiers);
	}

	public boolean doesApply(BlockPos pos) {
		return blockCondition
			.map(condition -> condition.test(getHolder().level(), pos))
			.orElse(true);
	}

	@Override
	public void onRemoved() {
		if (getHolder() instanceof Player player) {
			player.getBukkitLivingEntity().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1.0F);
		}
	}
}
