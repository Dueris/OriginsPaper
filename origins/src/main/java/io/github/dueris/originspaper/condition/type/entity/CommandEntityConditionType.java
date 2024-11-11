package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<CommandEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("command", SerializableDataTypes.STRING)
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
        data -> new CommandEntityConditionType(
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

    public CommandEntityConditionType(String command, Comparison comparison, int compareTo) {
        this.command = command;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Entity entity) {

        if (!(entity.level() instanceof ServerLevel serverWorld)) {
            return false;
        }

        MinecraftServer server = serverWorld.getServer();
        AtomicInteger result = new AtomicInteger();

        CommandSourceStack commandSource = entity.createCommandSourceStack()
            .withCallback((successful, returnValue) -> result.set(returnValue))
            .withPermission(OriginsPaper.config.executeCommand.permissionLevel)
            .withSource(CommandSource.NULL);

        if (OriginsPaper.config.executeCommand.showOutput) {

            CommandSource output = entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null
                ? serverPlayer
                : server;

            commandSource = commandSource.withSource(output);

        }

        server.getCommands().performPrefixedCommand(commandSource, command);
        return comparison.compare(result.get(), compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.COMMAND;
    }

}
