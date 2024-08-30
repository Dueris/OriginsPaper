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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifySwimSpeedPower extends ModifierPower {
	public ModifySwimSpeedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								@Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_swim_speed"));
	}

	@Override
	public void tick(Player player) {
		if (getPlayers().contains(player) && isActive(player) && !player.isFallFlying() && !player.getAbilities().flying && player.isSwimming()) {
			BlockState state = player.getInBlockState();
			if (state.hasProperty(BlockStateProperties.WATERLOGGED) || state.is(Blocks.WATER)) {
				double newSpeed = ModifierUtil.applyModifiers(player, getModifiers(), 0) / 3.3;
				player.getBukkitEntity().setVelocity(player.getBukkitEntity().getLocation().getDirection().multiply(newSpeed > 10 ? 10 : Math.max(newSpeed, 0)));
			}
		}
	}
}
