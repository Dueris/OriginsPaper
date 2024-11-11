package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExecuteCommandBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<ExecuteCommandBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("command", SerializableDataTypes.STRING),
        data -> new ExecuteCommandBlockActionType(
            data.get("command")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("command", actionType.command)
    );

    private final String command;

    public ExecuteCommandBlockActionType(String command) {
        this.command = command;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

        if (!(world instanceof ServerLevel serverWorld)) {
            return;
        }

        BlockState blockState = world.getBlockState(pos);
        String blockTranslationKey = blockState.getBlock().getDescriptionId();

        MinecraftServer server = serverWorld.getServer();
        CommandSourceStack commandSource = new CommandSourceStack(
            OriginsPaper.config.executeCommand.showOutput ? server : CommandSource.NULL,
            pos.getCenter(),
            Vec2.ZERO,
            serverWorld,
            OriginsPaper.config.executeCommand.permissionLevel,
            blockTranslationKey,
            Component.translatable(blockTranslationKey),
            server,
            null
        );

        server.getCommands().performPrefixedCommand(commandSource, command);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.EXECUTE_COMMAND;
    }

}
