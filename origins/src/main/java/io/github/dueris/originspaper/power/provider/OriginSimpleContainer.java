package io.github.dueris.originspaper.power.provider;

import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.SimplePower;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.mineskin.com.google.common.base.Preconditions;

import java.util.HashMap;

public class OriginSimpleContainer {
	public static HashMap<ResourceLocation, PowerProvider> location2PowerMap = new HashMap<>();

	public static void registerPower(@NotNull Class<?> clz) {
		try {
			Preconditions.checkArgument(clz.newInstance() instanceof PowerProvider, "Power isn't an instance of a PowerProvider power. This is required to make it so that its marked as able to be its own originPower");
			Preconditions.checkArgument(clz.getDeclaredField("powerReference") != null, "Unable to access required field \"powerReference\" inside Power. This is required to point to what powerFile this PowerProvider will use");

			PowerProvider instance = (PowerProvider) clz.newInstance();
			location2PowerMap.put((ResourceLocation) ReflectionUtils.getStaticFieldValue(clz, "powerReference"), instance);
			if (instance instanceof Listener || Listener.class.isAssignableFrom(clz)) {
				Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, OriginsPaper.getPlugin());
			}
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException |
				 InstantiationException e) {
			e.printStackTrace();
		}
	}

	public static PowerProvider getFromSimple(@NotNull SimplePower simplePower) {
		return location2PowerMap.get(simplePower.key());
	}
}
