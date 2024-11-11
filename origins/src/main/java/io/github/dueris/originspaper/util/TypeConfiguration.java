package io.github.dueris.originspaper.util;

import com.mojang.serialization.MapCodec;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.resources.ResourceLocation;

public interface TypeConfiguration<T> {

	ResourceLocation id();

	TypedDataObjectFactory<T> dataFactory();


	default CompoundSerializableDataType<T> dataType() {
		return dataFactory().getDataType();
	}

	default CompoundSerializableDataType<T> dataType(boolean root) {
		return dataType().setRoot(root);
	}


	default MapCodec<T> mapCodec() {
		return dataType().mapCodec();
	}

	default MapCodec<T> mapCodec(boolean root) {
		return dataType(root).mapCodec();
	}

}

