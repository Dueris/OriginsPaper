package io.github.dueris.calio.test;

import com.google.gson.JsonObject;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.annotations.SourceProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ModMeta {
	public static ConcurrentLinkedQueue<Class<? extends ModMeta>> INSTANCE_TYPES = new ConcurrentLinkedQueue<>(List.of());
	private final int priority;
	@SourceProvider
	private JsonObject sourceProvider;

	public ModMeta(ResourceLocation key, int priority) {
		this.priority = priority;
	}

	public static InstanceDefiner buildFactory() {
		return InstanceDefiner.instanceDefiner()
			.add("priority", SerializableDataTypes.boundNumber(SerializableDataTypes.INT, 0, Integer.MAX_VALUE));
	}
}
