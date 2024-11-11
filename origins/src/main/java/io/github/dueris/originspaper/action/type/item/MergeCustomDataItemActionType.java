package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MergeCustomDataItemActionType extends ItemActionType {

    public static final TypedDataObjectFactory<MergeCustomDataItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("nbt", SerializableDataTypes.NBT_COMPOUND),
        data -> new MergeCustomDataItemActionType(
            data.get("nbt")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("nbt", actionType.nbt)
    );

    private final CompoundTag nbt;

    public MergeCustomDataItemActionType(CompoundTag nbt) {
        this.nbt = nbt;
    }

    @Override
	protected void execute(Level world, SlotAccess stackReference) {
        CustomData.update(DataComponents.CUSTOM_DATA, stackReference.get(), oldNbt -> oldNbt.merge(nbt));
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ItemActionTypes.MERGE_CUSTOM_DATA;
    }

}
