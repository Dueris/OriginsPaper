package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.Entity;

public class ShowToastActionType {

	public static void action(SerializableData.Instance data, Entity entity) {

		// TODO
//        if (!entity.level().isClientSide && entity instanceof CustomToastViewer viewer) {
//            viewer.apoli$showToast(CustomToastData.FACTORY.fromData(data));
//        }

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("show_toast"),
			new SerializableData(),
//            CustomToastData.FACTORY.getSerializableData(),
			ShowToastActionType::action
		);
	}

}
