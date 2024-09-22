package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.util.Scheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	private static MinecraftServer SERVER;

	@Shadow
	public abstract PlayerList getPlayerList();

	@Inject(method = "tickServer", at = @At("RETURN"))
	public void tickScheduler(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		Scheduler scheduler = Scheduler.INSTANCE;
		scheduler.tick(SERVER);
	}
}
