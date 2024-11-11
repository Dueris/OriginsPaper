package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandBlockConditionType extends BlockConditionType {

    public static final TypedDataObjectFactory<CommandBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("command", SerializableDataTypes.STRING)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
            .add("compare_to", SerializableDataTypes.INT, 0),
        data -> new CommandBlockConditionType(
            data.get("command"),
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("command", conditionType.command)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final String command;

    private final Comparison comparison;
    private final int compareTo;

    public CommandBlockConditionType(String command, Comparison comparison, int compareTo) {
        this.command = command;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

        if (!(world instanceof ServerLevel serverWorld)) {
            return false;
        }

        MinecraftServer server = serverWorld.getServer();
        AtomicInteger result = new AtomicInteger();

        String blockTranslationKey = blockState.getBlock().getDescriptionId();
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

        commandSource = commandSource.withCallback((successful, returnValue) -> result.set(returnValue));
        server.getCommands().performPrefixedCommand(commandSource, command);

        return comparison.compare(result.get(), compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BlockConditionTypes.COMMAND;
    }

}
