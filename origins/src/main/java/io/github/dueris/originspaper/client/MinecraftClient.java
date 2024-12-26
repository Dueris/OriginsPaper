package io.github.dueris.originspaper.client;

import io.github.dueris.originspaper.client.render.ClientHudRenderer;
import io.github.dueris.originspaper.client.render.EntityRenderer;
import io.github.dueris.originspaper.client.texture.TexturesImpl;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ConcurrentHashMap;

public final class MinecraftClient {
	private static final ConcurrentHashMap<ServerPlayer, MinecraftClient> CLIENTS = new ConcurrentHashMap<>();
	public static HudRenderManager HUD_RENDER = new HudRenderManager();
	private final ServerPlayer player;
	private final ClientHudRenderer hudRenderer;
	private final EntityRenderer entityRenderer;

	public MinecraftClient(ServerPlayer player) {
		this.player = player;
		this.hudRenderer = new ClientHudRenderer(player);
		this.entityRenderer = new EntityRenderer(player);
	}

	public static void init() {
		TexturesImpl.init();
		HudRenderManager.init();
	}

	public static MinecraftClient of(ServerPlayer player) {
		if (!CLIENTS.containsKey(player)) {
			CLIENTS.put(player, new MinecraftClient(player));
		}

		return CLIENTS.get(player);
	}

	public void tick() {
		hudRenderer.tick();
		entityRenderer.tick();
	}

	public ServerPlayer player() {
		return player;
	}

}
