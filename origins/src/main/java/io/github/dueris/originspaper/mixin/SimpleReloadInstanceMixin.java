package io.github.dueris.originspaper.mixin;

import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.AvailablePackSourceRetriever;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.ModifyTypeTagPower;
import io.github.dueris.originspaper.power.type.RecipePower;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(SimpleReloadInstance.class)
public class SimpleReloadInstanceMixin {

	@Inject(method = "create", at = @At("HEAD"))
	private static void reloadCalio(ResourceManager manager, List<PreparableReloadListener> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, boolean profiled, CallbackInfoReturnable<ReloadInstance> cir) {
		try {
			CraftCalio calio = originspaper$buildNewCalio();
			CraftCalio.setRegistryAccess(OriginsPaper.REGISTRY_ACCESS.get());

			if (PowerType.REGISTRY != null) PowerType.REGISTRY.clear();
			else PowerType.REGISTRY = new ConcurrentHashMap<>();
			if (PowerType.REGISTRY != null) Origin.REGISTRY.clear();
			else Origin.REGISTRY = new ConcurrentHashMap<>();
			OriginLayer.REGISTRY.clear();
			RecipePower.recipeMapping.clear();
			RecipePower.tags.clear();
			ModifyTypeTagPower.resetTagCache();

			PlayerPowerRepository.clearRepository();

			AtomicInteger i = new AtomicInteger(0);
			((AvailablePackSourceRetriever) OriginsPaper.PACK_REPOSITORY.get()).originspaper$getAvailable().forEach((id, pack) -> {
				Path packPath = null;
				if (id.startsWith("file/")) {
					String packName = id.split("file/")[1];
					packPath = OriginsPaper.DATAPACK_PATH.get().resolve(packName);
				} else if (id.startsWith("origins/")) {
					String pluginName = id.split("origins/")[1];
					packPath = Paths.get("plugins/").resolve(pluginName);
				}

				if (packPath != null) {
					calio.parse(packPath.toAbsolutePath());
					i.incrementAndGet();
				}
			});
			CalioParser.LOGGER.info("Successfully parsed {} packs!", i.get());

			if (OriginsPaper.getPlugin() != null) {
				for (PowerType powerType : PowerType.REGISTRY.values()) {
					OriginsPaper.getPlugin().getServer().getPluginManager().registerEvents(powerType, OriginsPaper.getPlugin());
				}
			}

			int[] registrySizes = new int[]{
				PowerType.REGISTRY.size(), Origin.REGISTRY.size(), OriginLayer.REGISTRY.size()
			};
			OriginsPaper.LOGGER.info("Registry contains {} powers, {} origins, and {} layers.", registrySizes[0], registrySizes[1], registrySizes[2]);
			OriginsPaper.LOGGER.info("OriginsPaper {} has initialized. Ready to power up your game!", OriginsPaper.version);

			calio.shutdown();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	@Unique
	private static CraftCalio originspaper$buildNewCalio() {
		return CraftCalio.buildInstance().startBuilder()
			.withAccessor(new DataBuildDirective<>(List.of(), "powers", PowerTypeFactory.DATA, 0, null))
			.withAccessor(new DataBuildDirective<>(List.of(), "origins", Origin.DATA, 1, null))
			.withAccessor(new DataBuildDirective<>(List.of(), "origin_layers", OriginLayer.DATA, 2, null))
			.build();
	}
}

