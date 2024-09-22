package io.github.dueris.originspaper.access;

import net.minecraft.server.packs.repository.Pack;

import java.util.LinkedHashMap;

public interface AvailablePackSourceRetriever {
	LinkedHashMap<String, Pack> originspaper$getAvailable();
}
