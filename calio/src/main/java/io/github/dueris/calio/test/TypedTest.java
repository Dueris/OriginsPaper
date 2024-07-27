package io.github.dueris.calio.test;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.InstanceDefiner;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class TypedTest extends ModMeta {
	public TypedTest(String modid, String name, int priority, Set<String> testArray, boolean testBoolean, JsonObject jsonObject) {
		super(modid, name, priority, testArray, testBoolean, jsonObject);
	}

	public static InstanceDefiner buildDefiner() {
		return ModMeta.buildDefiner().typedRegistry(ResourceLocation.read("test:dueris").getOrThrow());
	}

	@Override
	public String toString() {
		return "fdsssssssssssssssssssssssssssssss";
	}
}
