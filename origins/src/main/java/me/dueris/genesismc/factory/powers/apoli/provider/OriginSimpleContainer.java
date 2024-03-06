package me.dueris.genesismc.factory.powers.apoli.provider;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.mineskin.com.google.common.base.Preconditions;

import java.lang.reflect.Field;

public class OriginSimpleContainer {

	public static boolean registerPower(Class<? extends CraftPower> clz) {
		try {
			Preconditions.checkArgument(clz.newInstance() instanceof PowerProvider, "CraftPower isnt an instance of a PowerProvider power. This is required to make it so that its marked as able to be its own originPower");
			Preconditions.checkArgument(clz.getDeclaredField("powerReference") != null, "Unable to access required field \"powerReference\" inside CraftPower. This is required to point to what powerFile this PowerProvider will use");

			Field field = clz.getDeclaredField("powerReference");
			field.setAccessible(true);

			CraftPower instance = clz.newInstance();
			GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).register(instance);
			if (instance instanceof Listener || Listener.class.isAssignableFrom(clz)) {
				Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
			}
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException |
				 InstantiationException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
