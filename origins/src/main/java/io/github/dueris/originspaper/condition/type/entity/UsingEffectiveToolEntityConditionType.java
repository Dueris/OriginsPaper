package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.mixin.ServerPlayerGameModeAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class UsingEffectiveToolEntityConditionType extends EntityConditionType {

    @Override
    public boolean test(Entity entity) {

        if (!(entity instanceof Player playerEntity)) {
            return false;
        }

        BlockState miningBlockState;
        if (playerEntity instanceof ServerPlayer serverPlayer) {

            ServerPlayerGameModeAccessor interactionManager = (ServerPlayerGameModeAccessor) serverPlayer.gameMode;
            if (!interactionManager.getIsDestroyingBlock()) {
                return false;
            }

            miningBlockState = entity.level().getBlockState(interactionManager.getDestroyPos());

        }

        else {
            return false;
        }

        return playerEntity.hasCorrectToolForDrops(miningBlockState);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.USING_EFFECTIVE_TOOL;
    }

}
