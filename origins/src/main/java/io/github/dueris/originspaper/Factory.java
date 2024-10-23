package io.github.dueris.originspaper;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.Validatable;
import net.minecraft.resources.ResourceLocation;

public interface Factory {

	ResourceLocation getSerializerId();

	SerializableData getSerializableData();

	Instance fromData(SerializableData.Instance data);

	interface Instance extends Validatable {

		default SerializableData getSerializableData() {
			return this.getFactory().getSerializableData();
		}

		default ResourceLocation getSerializerId() {
			return this.getFactory().getSerializerId();
		}

		SerializableData.Instance getData();

		Factory getFactory();

		@Override
		default void validate() throws Exception {
			this.getData().validate();
		}

	}

}
