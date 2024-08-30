package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.registry.BuiltinRegistry;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.util.ApoliScheduler;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "tickServer", locator = At.Value.RETURN)
	public static void tickHook(MinecraftServer server, BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		ApoliScheduler scheduler = ApoliScheduler.INSTANCE;
		scheduler.tick(server);
	}

	@Inject(method = "runServer", locator = At.Value.HEAD)
	public static void originspaper$init(@NotNull MinecraftServer server, CallbackInfo info) {
		CraftCalio calio = CraftCalio.buildInstance().startBuilder()
			.withAccessor(new DataBuildDirective<>(List.of(), "powers", PowerTypeFactory.DATA, 0, Registries.POWER))
			.withAccessor(new DataBuildDirective<>(List.of(), "origins", Origin.DATA, 1, Registries.ORIGIN))
			.withAccessor(new DataBuildDirective<>(List.of(), "origin_layers", OriginLayer.DATA, 2, Registries.LAYER))
			.build();
		PackRepositoryMixin.getAvailable(server.getPackRepository()).forEach((id, pack) -> {
			Path packPath = null;
			if (id.startsWith("file/")) {
				String packName = id.split("file/")[1];
				packPath = PackRepositoryMixin.DATAPACK_PATH.get().resolve(packName);
			} else if (id.startsWith("assets/origins/")) {
				String pluginName = id.split("origins/")[1];
				packPath = Paths.get("plugins/").resolve(pluginName);
			}

			if (packPath != null) {
				calio.parse(packPath.toAbsolutePath());
			}
		});

		BuiltinRegistry.bootstrap();
		int[] registrySizes = new int[]{
			OriginsPaper.getRegistry().retrieve(Registries.POWER).registrySize(), OriginsPaper.getRegistry().retrieve(Registries.ORIGIN).registrySize(), OriginsPaper.getRegistry().retrieve(Registries.LAYER).registrySize()
		};
		OriginsPaper.LOGGER.info("Registry contains {} powers, {} origins, and {} layers.", registrySizes[0], registrySizes[1], registrySizes[2]);
		OriginsPaper.LOGGER.info("OriginsPaper {} has initialized. Ready to power up your game!", OriginsPaper.pluginData.getFullVersion());

		calio.shutdown();
	}
}
