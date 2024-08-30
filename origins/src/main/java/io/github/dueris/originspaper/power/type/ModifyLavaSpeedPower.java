package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyLavaSpeedPower extends ModifierPower {
	/**
	 * We have slightly different values than origins to
	 * try and make this closer to origins mod, so we multiply the max
	 * value by 10, and the base speed 1(because we modify the velocity of the
	 * player in when in lava)
	 */
	public static final double MAX_LAVA_SPEED = 5.0D;
	public static final double MIN_LAVA_SPEED = 0.0D;

	public ModifyLavaSpeedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								@Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_lava_speed"));
	}

	@Override
	public void tick(Player player) {
		if (getPlayers().contains(player) && isActive(player) && !player.isFallFlying() && !player.getAbilities().flying) {
			BlockState state = player.getInBlockState();
			if (state.is(Blocks.LAVA)) {
				double newSpeed = ModifierUtil.applyModifiers(player, getModifiers(), 0);
				player.getBukkitEntity().setVelocity(player.getBukkitEntity().getLocation().getDirection().multiply(newSpeed > MAX_LAVA_SPEED ? MAX_LAVA_SPEED : Math.max(newSpeed, MIN_LAVA_SPEED)));
			}
		}
	}
}
