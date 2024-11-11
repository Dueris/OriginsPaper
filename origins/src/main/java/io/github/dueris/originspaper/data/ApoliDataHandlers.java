package io.github.dueris.originspaper.data;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import java.util.HashSet;
import java.util.Set;

public class ApoliDataHandlers {

	public static final EntityDataSerializer<Set<String>> STRING_SET = EntityDataSerializer.forValueType(ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.stringUtf8(32767)));

	public static void register() {
		EntityDataSerializers.registerSerializer(STRING_SET);
	}

}
