package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.AdvancementUtil;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RevokeAdvancementEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<RevokeAdvancementEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("advancement", SerializableDataTypes.IDENTIFIER.optional(), Optional.empty())
            .add("selection", ApoliDataTypes.ADVANCEMENT_SELECTION, AdvancementCommands.Mode.ONLY)
            .add("criterion", SerializableDataTypes.STRING.optional(), Optional.empty())
            .add("criteria", SerializableDataTypes.STRINGS.optional(), Optional.empty()),
        data -> new RevokeAdvancementEntityActionType(
            data.get("advancement"),
            data.get("selection"),
            data.get("criterion"),
            data.get("criteria")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("advancement", actionType.advancementId)
            .set("selection", actionType.selection)
            .set("criterion", actionType.criterion)
            .set("criteria", actionType.criteria)
    );

    private final Optional<ResourceLocation> advancementId;
    private final AdvancementCommands.Mode selection;

    private final Optional<String> criterion;
    private final Optional<List<String>> criteria;

    private final Set<String> allCriteria;

    public RevokeAdvancementEntityActionType(Optional<ResourceLocation> advancementId, AdvancementCommands.Mode selection, Optional<String> criterion, Optional<List<String>> criteria) {

        this.advancementId = advancementId;
        this.selection = selection;

        this.criterion = criterion;
        this.criteria = criteria;

        this.allCriteria = new ObjectOpenHashSet<>();

        this.criterion.ifPresent(this.allCriteria::add);
        this.criteria.ifPresent(this.allCriteria::addAll);

    }

    @Override
    protected void execute(Entity entity) {

        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }

        MinecraftServer server = serverPlayer.server;
        ServerAdvancementManager advancementLoader = server.getAdvancements();

        if (selection == AdvancementCommands.Mode.EVERYTHING) {
            AdvancementUtil.processAdvancements(advancementLoader.getAllAdvancements(), AdvancementCommands.Action.REVOKE, serverPlayer);
        }

        else if (advancementId.isPresent()) {

            ResourceLocation actualAdvancementId = advancementId.get();
            AdvancementHolder advancementEntry = advancementLoader.get(actualAdvancementId);

            if (advancementEntry == null) {
//                Apoli.LOGGER.warn("Unknown advancement \"{}\" referenced in an entity action that uses the `revoke_advancement` type!", actualAdvancementId);
                return;
            }

            else if (allCriteria.isEmpty()) {
                AdvancementUtil.processAdvancements(AdvancementUtil.selectEntries(advancementLoader.tree(), advancementEntry, selection), AdvancementCommands.Action.REVOKE, serverPlayer);
            }

            else {
                AdvancementUtil.processCriteria(advancementEntry, allCriteria, AdvancementCommands.Action.REVOKE, serverPlayer);
            }

        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.REVOKE_ADVANCEMENT;
    }

}
