package me.dueris.calio.builder;

import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryInstance;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class CalioBuilder {
	public static CalioBuilder INSTANCE = new CalioBuilder();
	public List<AccessorRoot> accessorRoots = new ArrayList<>();

	public void addAccessorRoot(String dirPath, NamespacedKey putRegistry, FactoryInstance inst, int priority) {
		accessorRoots.add(new AccessorRoot(dirPath, putRegistry, inst, priority));
	}
}
