package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.types.Impact;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.RecipePower;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Scheduler;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "tickServer", locator = At.Value.RETURN)
	public static void tickHook(MinecraftServer server, BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		Scheduler scheduler = Scheduler.INSTANCE;
		scheduler.tick(server);
	}

	@Inject(method = "runServer", locator = At.Value.HEAD)
	public static void originspaper$init(@NotNull MinecraftServer server, CallbackInfo info) {
		CraftCalio calio = buildNewCalio();

		parse(server, calio);

		finish();
		int[] registrySizes = new int[]{
			ApoliRegistries.POWER.size(), ApoliRegistries.ORIGIN.size(), ApoliRegistries.ORIGIN_LAYER.size()
		};
		OriginsPaper.LOGGER.info("Registry contains {} powers, {} origins, and {} layers.", registrySizes[0], registrySizes[1], registrySizes[2]);
		OriginsPaper.LOGGER.info("OriginsPaper {} has initialized. Ready to power up your game!", OriginsPaper.pluginData.getFullVersion());

		calio.shutdown();
	}

	@Inject(method = "reloadResources", locator = At.Value.HEAD, params = {
		Collection.class, ServerResourcesReloadedEvent.Cause.class
	})
	public static void calio$reload(@NotNull MinecraftServer server, Collection<String> dataPacks, ServerResourcesReloadedEvent.Cause cause, CallbackInfo info) {
		CraftCalio calio = buildNewCalio();
		try {
			ApoliRegistries.clearRegistries();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		RecipePower.recipeMapping.clear();
		RecipePower.tags.clear();
		for (ServerPlayer player : server.getPlayerList().players) {
			PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(player);
			String saveData = repository.serializePowers(new CompoundTag()).toString();
			player.getBukkitEntity().getPersistentDataContainer()
				.set(identifier(), PersistentDataType.STRING, saveData);
			PowerHolderComponent.unloadPowers(player.getBukkitEntity());
			repository.clearData();
		}

		try {
			PlayerPowerRepository.clearRepository();
			OriginsPaper.reload();
			parse(server, calio);
			finish();

			for (PowerType powerType : ApoliRegistries.POWER) {
				OriginsPaper.getPlugin().getServer().getPluginManager().registerEvents(powerType, OriginsPaper.getPlugin());
			}
		} catch (Throwable e) {
			throw new RuntimeException("Unable to run calio reload!", e);
		}

		for (ServerPlayer player : server.getPlayerList().players) {
			PlayerPowerRepository repository = PlayerPowerRepository.getOrCreateRepo(player);
			repository.readPowers(
				player.getBukkitEntity().getPersistentDataContainer()
					.get(identifier(), PersistentDataType.STRING)
			);

			PowerHolderComponent.loadPowers(player.getBukkitEntity());
		}

		Bukkit.updateRecipes();
	}

	private static void parse(@NotNull MinecraftServer server, CraftCalio calio) {
		Util.unfreezeUntil(new Registry[]{
			ApoliRegistries.POWER, ApoliRegistries.ORIGIN, ApoliRegistries.ORIGIN_LAYER
		}, () -> {
			AtomicInteger i = new AtomicInteger(0);
			PackRepositoryMixin.getAvailable(server.getPackRepository()).forEach((id, pack) -> {
				Path packPath = null;
				if (id.startsWith("file/")) {
					String packName = id.split("file/")[1];
					packPath = PackRepositoryMixin.DATAPACK_PATH.get().resolve(packName);
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
		});
	}

	private static @NotNull NamespacedKey identifier() {
		return new NamespacedKey(OriginsPaper.getPlugin(), "apoli_repository");
	}

	private static CraftCalio buildNewCalio() {
		return CraftCalio.buildInstance().startBuilder()
			.withAccessor(new DataBuildDirective<>(List.of(), "powers", PowerTypeFactory.DATA, 0, ApoliRegistries.POWER))
			.withAccessor(new DataBuildDirective<>(List.of(), "origins", Origin.DATA, 1, ApoliRegistries.ORIGIN))
			.withAccessor(new DataBuildDirective<>(List.of(), "origin_layers", OriginLayer.DATA, 2, ApoliRegistries.ORIGIN_LAYER))
			.build();
	}

	private static void finish() {
		Util.unfreezeUntil(new Registry[]{ApoliRegistries.ORIGIN}, () -> {
			Origin.EMPTY = new Origin(
				ResourceLocation.parse("origins:empty"), List.of(), new ItemStack(Items.AIR), true, Integer.MAX_VALUE, Impact.NONE, 0, null, net.minecraft.network.chat.Component.empty(), net.minecraft.network.chat.Component.empty()
			);
			Registry.register(ApoliRegistries.ORIGIN, ResourceLocation.fromNamespaceAndPath("origins", "empty"), Origin.EMPTY);
		});

	}
}
