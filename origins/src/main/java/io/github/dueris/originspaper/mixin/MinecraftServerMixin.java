package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.util.ApoliScheduler;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "tickServer", locator = At.Value.RETURN)
	public static void tickHook(MinecraftServer server, BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		ApoliScheduler scheduler = ApoliScheduler.INSTANCE;
		scheduler.tick(server);
	}
}
