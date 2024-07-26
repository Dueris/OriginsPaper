package io.github.dueris.calio.parser.reader;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DeserializedFactoryJson {

	public static @Nullable DeserializedFactoryJson decompileJsonObject(JsonObject jsonObject, InstanceDefiner definer) {
		Optional<Pair<List<Pair<String, ?>>, List<Pair<String, ?>>>> compiledInstance = CalioParser.compileFromInstanceDefinition(
			definer, jsonObject, Optional.empty(), Optional.empty()
		);
		if (compiledInstance.isEmpty()) return null;
		List<Pair<String, ?>> compiledArguments = compiledInstance.get().second();
		List<?> args = definer.sortByPriorities(compiledArguments);
	}
}
