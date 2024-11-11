package io.github.dueris.originspaper.action.type.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SelectorActionEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<SelectorActionEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("selector", ApoliDataTypes.ENTITIES_SELECTOR)
            .add("bientity_action", BiEntityAction.DATA_TYPE)
            .add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
        data -> new SelectorActionEntityActionType(
            data.get("selector"),
            data.get("bientity_action"),
            data.get("bientity_condition")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("selector", actionType.selector)
            .set("bientity_action", actionType.biEntityAction)
            .set("bientity_condition", actionType.biEntityCondition)
    );

    private final ArgumentWrapper<EntitySelector> selector;
    private final EntitySelector unwrappedSelector;

    private final BiEntityAction biEntityAction;
    private final Optional<BiEntityCondition> biEntityCondition;

    public SelectorActionEntityActionType(ArgumentWrapper<EntitySelector> selector, BiEntityAction biEntityAction, Optional<BiEntityCondition> biEntityCondition) {
        this.selector = selector;
        this.unwrappedSelector = selector.parsedValue();
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
    }

    @Override
    protected void execute(Entity entity) {

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return;
        }

        CommandSourceStack commandSource = entity.createCommandSourceStack()
            .withSource(CommandSource.NULL)
            .withPermission(OriginsPaper.config.executeCommand.permissionLevel);

        if (OriginsPaper.config.executeCommand.showOutput) {
            commandSource = commandSource.withSource(entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null
                ? serverPlayer
                : server);
        }

        try {
            unwrappedSelector.findEntities(commandSource)
                .stream()
                .filter(selected -> biEntityCondition.map(condition -> condition.test(entity, selected)).orElse(true))
                .forEach(selected -> biEntityAction.execute(entity, selected));
        }

        catch (CommandSyntaxException cse) {
            commandSource.sendFailure(Component.translationArg(cse.getRawMessage()));
		}

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.SELECTOR_ACTION;
    }

}
